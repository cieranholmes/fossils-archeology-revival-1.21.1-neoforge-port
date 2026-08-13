---
name: fossils-phase3-capabilities-networking-progress
description: "Phase 3 (Capabilities & networking rewrite) COMPLETE as of 2026-08-13 - mammal pregnancy Data Attachment, first-hatch player Data Attachment, machine energy/item-handler RegisterCapabilitiesEvent exposure, and network packets all ported and boot+hopper-verified. STALE NOTE: the 4 debug packets this file originally called out as deferred are no longer deferred - Phase 5 (see [[fossils-phase5-debug-gui-progress]]) ported the debug HUD/InstructionSystem they depended on and wired all 4."
metadata:
  node_type: memory
  type: project
  modified: 2026-08-13T13:30:00.000Z
---

Phase 3 of [[fossils-migration-plan]], done in one continuation session (2026-08-13) as a single TaskList (8 tasks,
all completed). Source: `~/Personal/FossilsArcheologyRevival` (note: the migration-plan memory's path
`~/IdeaProjects/FossilsArcheologyRevival` is stale - the repo moved; there's also an unrelated stale copy at
`~/IdeaProjects` under a different home dir (`cieranholmes1`) - don't use either, use `~/Personal/FossilsArcheologyRevival`).

## Scoping (via an Explore agent before starting)

Confirmed via full-tree greps: target had a **stubbed** `ModCapabilities` (mammal embryo tracking was all no-ops
returning false/0/null; first-hatch used a `player.getPersistentData()` NBT workaround instead of a real
attachment), **zero** capability exposure on any machine block entity (`RegisterCapabilitiesEvent`/`IItemHandler`/
`IEnergyStorage` - no hits at all, meaning hoppers/other mods could not interact with any machine), and **zero**
networking code whatsoever (no `network`/`packet` package, no `CustomPacketPayload`/`PayloadRegistrar`). Source's
Forge-side capability exposure used **mixins** injecting `getCapability` into `EnergyContainerBlockEntity`/
`MachineContainerBlockEntity` directly - NeoForge has no mixin framework in this project at all, so this needed a
real `RegisterCapabilitiesEvent` rewrite, not a mixin port.

## 1. Mammal pregnancy - `com.github.fossils.capabilities.{MammalData,ModAttachments}`

`MammalData` (mutable, `INBTSerializable<CompoundTag>`: `embryoProgress` int + nullable `EntityInfo embryo`,
serialized as `embryo.name()` string exactly like source's `MammalCap`) is a NeoForge `AttachmentType<MammalData>`
on `Animal`, registered via `ModAttachments` (`DeferredRegister<AttachmentType<?>>` against
`NeoForgeRegistries.Keys.ATTACHMENT_TYPES`). Built via `AttachmentType.serializable(MammalData::new)
.sync(streamCodec)` - **`.sync(...)` on the builder makes source's entire `S2CMammalCapMessage` packet and
`ForgeModEvents.onPlayerStartTracking` manual resync-on-tracking-start logic unnecessary**; NeoForge's attachment
framework auto-syncs on `entity.syncData(type)` calls and on a new player starting to track the entity. This is a
real API capability, not a guess - confirmed by reading `AttachmentType.java`/`IAttachmentHolder.java` from the
project's own `neoforge-*-sources.jar` (at `build/moddev/artifacts/`, always available for exactly this kind of
verification - faster than searching Gradle caches for decompiled sources).

**Important nuance preserved from source**: `ModCapabilities.setEmbryoProgress`/`setEmbryo` (the plain per-tick
setters) do **NOT** call `animal.syncData(...)` - only `startPregnancy`/`stopPregnancy` do. This matters because
[[fossils-phase3-capabilities-networking-progress]]'s own `MammalPregnancyEvents.onTick` runs its progress-increment
branch on **both** logical sides every tick unconditionally (matching source's Forge `LivingEvent.LivingTickEvent`,
which also fired both sides) - each side just increments its own local attachment copy in lockstep, exactly like
source's design (S2CMammalCapMessage was only ever sent from `startPregnancy`/`stopPregnancy`, never per-tick).
Only the actual entity-spawning `growEntity` call + `stopPregnancy` are guarded to server-only. **If you make
`setEmbryoProgress` sync on every call, you'll spam a sync packet every tick for every pregnant mammal** - don't do
that; getting this wrong was the first design considered and caught before writing code.

`ModCapabilities` (`com.github.fossils.capabilities.ModCapabilities.java`, rewritten in place - kept the exact same
public method names/signatures other files already called) is now real: `hasEmbryo`/`getEmbryoProgress`/
`getEmbryo`/`setEmbryoProgress`/`setEmbryo` all delegate to `animal.getData(ModAttachments.MAMMAL_DATA)`.
`hasHatchedDinosaur`/`setHatchedDinosaur` now use a second attachment, `ModAttachments.HAS_HATCHED_DINOSAUR`
(`AttachmentType<Boolean>` via `.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().sync(ByteBufCodecs.BOOL)`)
- `.copyOnDeath()` replaces source's manual `ForgeModEvents.onPlayerClone` copy-on-respawn logic entirely, another
genuine simplification via the same builder API.

**New file `com.github.fossils.entity.MammalPregnancyEvents`** (`@EventBusSubscriber`) replaces source's
Architectury `ModEvents.INTERACT_ENTITY` embryo branch (the WHIP branch in that same source method is unrelated,
not touched) and Forge's `ForgeModEvents.onLivingUpdate`:
- `onInteractEntity(PlayerInteractEvent.EntityInteract)`: right-click `MammalEmbryoItem` on an adult mammal `Animal`
  - if already pregnant, `event.setCanceled(true)` + `setCancellationResult(FAIL)`; otherwise on the real
    `ServerPlayer` branch fires `ModTriggers.IMPLANT_EMBRYO_TRIGGER` (closing the gap flagged in
    [[fossils-phase2-items-progress]] task 9 - that trigger now has a real call site), calls `startPregnancy`,
    shrinks the stack (unless creative); the non-`ServerPlayer` (client-side) branch spawns cosmetic SMOKE
    particles via the target's existing `com.github.fossils.entity.util.Util.spawnParticles` helper, matching
    source's client-cosmetic-only branch.
- `onTick(EntityTickEvent.Post)`: the lockstep progress-increment logic described above, `Config.pregnancyDuration`
  gates the grow threshold.
- `growEntity(EntityInfo, LivingEntity)`: ported ~1:1 from source's `ModEvents.growEntity` (horse/Quagga special
  cases, `AgeableMob.getBreedOffspring`, tame-and-reset-age for `Prehistoric` offspring) - source's own top-of-method
  client-side "spawn heart particles" branch was **not** ported since it's genuinely unreachable: the only call site
  (`onLivingUpdate`) already guards the call to server-only in source itself, so that branch never actually ran even
  in source.

**Deliberately out of scope, flagged for whoever does the underlying feature** (same reasoning as
[[fossils-phase2-items-progress]]'s SarcophagusBlock/embryo-implant notes - these are real, pre-existing gaps this
task's scope doesn't cover):
- `OPEN_SARCOPHAGUS_TRIGGER`'s real call site (`SarcophagusBlock`'s missing scarab-gem-unlock `useItemOn` branch) -
  still not wired; unrelated to Phase 3.

## 2. Machine capabilities - `com.github.fossils.block.entity.MachineCapabilities` (new)

`@SubscribeEvent registerCapabilities(RegisterCapabilitiesEvent)` (fires on the **mod** bus - confirmed
`RegisterCapabilitiesEvent implements IModBusEvent`; `@EventBusSubscriber` with no explicit `bus=` still works
because it auto-subscribes to whichever bus the event type belongs to, same as the existing `Config`/`ModEntities`
classes already relied on for `ModConfigEvent`).
- `Capabilities.EnergyStorage.BLOCK` registered for `AnalyzerBlockEntity`/`CultureVatBlockEntity` (the two
  `EnergyContainerBlockEntity` subclasses) via `(be, side) -> Config.machinesRequireEnergy && side != null ?
  (IEnergyStorage) be.getEnergyStorage() : null` - the `side != null` gate faithfully replicates source's own
  `EnergyContainerBlockEntityMixin` check (an unsided capability query returns nothing), which may well have been
  an unintentional mixin quirk rather than deliberate design, but is preserved per the project's faithful-port
  policy (see [[fossils-known-upstream-bugs]]).
- `Capabilities.ItemHandler.BLOCK` registered for `AnalyzerBlockEntity`/`CultureVatBlockEntity`/`FeederBlockEntity`/
  `SifterBlockEntity`/`WorktableBlockEntity` via `SidedInvWrapper::new` (NeoForge's own `net.neoforged.neoforge.
  items.wrapper.SidedInvWrapper`, unchanged from the old Forge one) - method-reference-compatible directly since
  `SidedInvWrapper(WorldlyContainer, @Nullable Direction)` matches `ICapabilityProvider<BE, Direction, IItemHandler>`
  for any `BE extends BlockEntity & WorldlyContainer` (had to use that two-interface generic bound, not
  `MachineContainerBlockEntity`, because `FeederBlockEntity` implements `WorldlyContainer` directly without going
  through the shared base class). The other 6 block entities under `block/entity/` (`AncientChestBlockEntity`,
  `AnuBarrierBlockEntity`, `AnuStatueBlockEntity`, `AnubiteStatueBlockEntity`, `FakeObsidianBlockEntity`,
  `SarcophagusBlockEntity`) are plain `BlockEntity`s with no `Container`/`WorldlyContainer` at all - not inventories,
  correctly excluded.

**Runtime-verified, not just compiled**: booted a live server, placed a chest → hopper → `fossils:analyzer` stack,
put diamonds in the chest. After a few ticks the diamonds moved from the chest into the **hopper's own buffer**
(visible via `/data get block`) but were correctly **rejected** by the analyzer's slot-validation logic (diamonds
aren't a valid analyzer input) - this is the exact expected behavior for a real, working `IItemHandler` capability
matched against the block's own existing input-filtering logic (same shape as a vanilla furnace rejecting a
non-fuel item via a hopper). Proves the whole chain end-to-end, not just "it compiles."

## 3. Networking - `com.github.fossils.network` (new package)

`ModPackets` (`@EventBusSubscriber`, `RegisterPayloadHandlersEvent`) replaces Architectury's `NetworkChannel`/
`NetworkManager` and source's three named channels (`CAP_CHANNEL`/`SYNC_CHANNEL`/`DEBUG_CHANNEL`) - NeoForge
payloads don't need named channels, one `PayloadRegistrar` (`event.registrar("1")`) handles every type, delegating
to `ProductionPackets.register(registrar)` / `DebugPackets.register(registrar)`.

**Packet shape, established once and reused for all 17**: each is a `record ... implements CustomPacketPayload`
with a `public static final Type<X> TYPE = new Type<>(FossilsAndArcheologyRevival.location("..."))` and a
`public static final StreamCodec<RegistryFriendlyByteBuf, X> STREAM_CODEC = StreamCodec.of(encoder, decoder)`
(built directly from the same `FriendlyByteBuf` `read*`/`write*`/`readMap`/`writeMap` calls source used inside its
old `write(FriendlyByteBuf)`/`private ctor(FriendlyByteBuf)` pair - `RegistryFriendlyByteBuf extends FriendlyByteBuf`
so no rewrite needed there), a `type()` override returning `TYPE`, and a `public static void handle(X payload,
IPayloadContext context)` replacing the old `apply(Supplier<PacketContext>)`. Registered via
`registrar.playToClient(...)`/`playToServer(...)`/`playBidirectional(...)` + a method reference to `handle`.
**Gotcha hit once**: don't name a record component `type` - it collides with `CustomPacketPayload.type()`'s
accessor and fails with "invalid accessor method in record" (renamed `C2SDisableAIMessage`'s `type` field to
`aiType`).

**2 of the original 12 production packets are gone by design, not oversight**:
- `S2CMammalCapMessage` - replaced entirely by `ModAttachments.MAMMAL_DATA`'s built-in sync (section 1 above).
- `S2CSyncFoodMappingsMessage` - source's JSON+network food-value system was deliberately not ported in
  [[fossils-phase2-items-progress]] (user's explicit "food system stays simple" decision) - target's `FoodMappings`
  is hardcoded Java, identical on every client, nothing to sync.

**10 production packets ported** (`com.github.fossils.network`): `S2CSyncEntityInfoMessage`/
`S2CSyncEntityVariantsMessage` (both call already-existing `EntityDataLoader.INSTANCE.replaceData`/
`EntityVariantLoader.INSTANCE.replaceVariants` - those methods were already sitting there unused, presumably
ported ahead of time during Phase 4), `S2CSyncConfigMessage` (rewritten to plain fields instead of source's generic
`Map<String,Integer>`/`Map<String,Boolean>` + `FossilConfig.overrideEntries` - target's `Config` already exposes
plain mutable `public static` fields, so a fixed 6-field record + direct field assignment is simpler and equally
correct), `S2CSyncActiveAnimationMessage`, `S2CSyncToyAnimationMessage`, `S2CMusicMessage` (needed porting source's
tiny `ModMusic`/`MusicHandler` too, now under `com.github.fossils.client.sounds` - just 3-line wrappers around
vanilla's own `Minecraft.getInstance().getMusicManager()`, not a real custom music system, so a full faithful port
cost nothing extra), `S2CActivateAttackBoxesMessage`, `C2SHitPlayerMessage`, `C2SRiderForceFlyingMessage`,
`C2SVerticalFlightMessage`.

**Call sites wired** (all had the packet-send silently dropped or explicitly flagged "networking hasn't been
ported (Phase 3)" in earlier-phase code - now restored): `AnimationLogic.triggerAnimation`/`forceAnimation`
(→ `S2CSyncActiveAnimationMessage`, via `PacketDistributor.sendToPlayersTrackingEntity` - a reasonable idiomatic
swap for source's manual `TargetingConditions`-based 30-block player filter, not a literal port, since NeoForge's
own tracking-radius broadcast is the native equivalent), `ToyTetheredLog.hurt` (→ `S2CSyncToyAnimationMessage`,
same swap), `DinosaurEgg.hatchEgg` (→ `S2CMusicMessage` on first-ever-hatch), `DelayedAttackGoal.checkAndPerformAttack`
(→ `S2CActivateAttackBoxesMessage`, via `PacketDistributor.sendToPlayer` - this closes the exact gap flagged in a
`//deferred to Phase 3 networking` comment in [[fossils-combat-and-scaling-bugs-fixed]]-era code). **Not wired**:
`C2SRiderForceFlyingMessage`/`C2SVerticalFlightMessage`'s send side lives in source's `ClientInit.java` - a
substantial client keybind-polling class (custom fly-down key, per-tick `mc.player.input.jumping` state machine)
that doesn't exist in target at all yet. That's Phase 5 (client rendering/input) scope, not networking; the packet
+ server-side handler are ready and waiting for whenever that lands. Also not wired: `C2SHitPlayerMessage`'s send
side - source's client-side attack-box collision-detection loop (checking the local player against `more-hitboxes`'
`AttackBoxData.getActiveBoxes()`/`clientTick(Level)` each client tick) isn't built in target either, same category.

**7 of 11 debug packets ported** (`com.github.fossils.network.debug`, gated behind `Version.debugEnabled()` in
`DebugPackets.register`): `C2SDisableAIMessage` (bidirectional - client→server to toggle AI, and server→client
re-broadcast via `Prehistoric.disableCustomAI`, restored via `PacketDistributor.sendToPlayersNear` at the exact
32-block radius source used), `C2SDiscardMessage`, `C2SForceAnimationMessage`, `C2SRotationMessage` (added a
`entity == null` guard at the top - source's own flag==0 branch would NPE on a null entity since `instanceof` on
null is false-but-falls-through to `entity.setYRot(...)`; flag==1 already null-checked in source, this is a genuine
latent source bug, judged not worth faithfully preserving since it's dev-tooling with no gameplay impact, unlike
the documented gameplay bugs in [[fossils-known-upstream-bugs]]), `C2SSlowMessage` (needed the `AttributeModifier`
rework - see drift note below), `C2STameMessage`, `S2CCancelAnimationMessage` (call site restored in
`AnimationLogic.cancelAnimation`'s server branch, but guarded behind `Version.debugEnabled()` since its only real
caller, source's `InstructionSystem`, isn't ported - sending an unregistered payload when debug is disabled would
otherwise be a live foot-gun for whoever ports `InstructionSystem` later).

**[STALE - see [[fossils-phase5-debug-gui-progress]]] 4 debug packets deliberately NOT ported at the time** -
`C2SStructureMessage`, `InstructionMessage`, `S2CMarkMessage`, `SyncDebugInfoMessage` all depended on source's
client-side debug HUD (`DebugScreen`/`AnimationTab`/`InstructionTab`) and/or the `Instruction`/`InstructionSystem`
execution framework, which didn't exist in target yet at the time this note was written. Phase 5 ported that
debug HUD and both `InstructionSystem` and all 4 of these packets now have real, wired handlers - 0 debug
packets remain deferred.

## New MC/NeoForge API drift discovered this session (append to the running catalogs in
[[fossils-phase4-entities-progress]]/[[fossils-combat-and-scaling-bugs-fixed]]/[[fossils-phase2-items-progress]])

- **`AttributeModifier` is now `record(ResourceLocation id, double amount, Operation operation)`** - the old
  `String name` identifier is gone, needs a real `ResourceLocation`. **`Operation.MULTIPLY_TOTAL` was renamed to
  `ADD_MULTIPLIED_TOTAL`** (the other two: `ADD_VALUE`, `ADD_MULTIPLIED_BASE`). `AttributeInstance.
  removeModifiers()`/`addPermanentModifier(AttributeModifier)` unchanged.
- **NeoForge Data Attachments** (`net.neoforged.neoforge.attachment`) are the 1.21.1-native replacement for
  per-entity/per-player Forge capabilities (`INBTSerializable` tokens + `AttachCapabilitiesEvent`). Register via
  `DeferredRegister<AttachmentType<?>>` against `NeoForgeRegistries.Keys.ATTACHMENT_TYPES`. Builder chain:
  `AttachmentType.serializable(ctor)` (for `INBTSerializable` payloads) or `.builder(default).serialize(Codec)`
  (for anything Codec-friendly, e.g. a boxed `Boolean`), then optionally `.sync(StreamCodec)` (auto-syncs on
  `holder.syncData(type)` and on new-player-tracking-start) and `.copyOnDeath()` (auto-copies across player
  respawn/dimension-change, requires a serializer). Access via `holder.getData(type)` (auto-creates+stores default
  on first access, returns the **same mutable instance** thereafter) / `holder.setData(type, value)` / `holder.
  syncData(type)`. This single API replaces three separate things source needed platform-specific code for:
  capability-provider registration, manual `PlayerEvent.Clone` copy logic, and a dedicated sync packet.
- **`RegisterCapabilitiesEvent` fires on the mod bus** (`IModBusEvent`) - don't confuse NeoForge's own
  `RegisterCapabilitiesEvent` (for `IEnergyStorage`/`IItemHandler`/`IFluidHandler` block/entity/item capability
  *exposure*) with Forge 1.20's `RegisterCapabilitiesEvent` (for registering `INBTSerializable` capability
  *tokens*) - same class name across versions, completely different purpose. `@EventBusSubscriber` with no
  explicit `bus=` still resolves correctly for mod-bus events (same mechanism `Config`'s `ModConfigEvent` listener
  already relied on) - no need to hunt down the `Bus` enum's import path.
- **`Capabilities.EnergyStorage.BLOCK`/`Capabilities.ItemHandler.BLOCK`** (`net.neoforged.neoforge.capabilities.
  Capabilities`) are the built-in `BlockCapability<IEnergyStorage/IItemHandler, @Nullable Direction>` constants to
  register against via `event.registerBlockEntity(capability, blockEntityType, (be, side) -> ...)` - this fully
  replaces Forge's old `ICapabilityProvider`/`getCapability` override pattern (which itself replaced Forge 1.20's
  even older `Capability<T>` + `LazyOptional` pattern).
- **`net.neoforged.neoforge.items.wrapper.SidedInvWrapper`/`InvWrapper`** are unchanged from the old Forge
  package path (just re-namespaced to `net.neoforged`) - directly usable as an `IItemHandler` adapter over any
  `WorldlyContainer`/`Container` with zero rework needed.
- **`CustomPacketPayload`/`PayloadRegistrar`/`RegisterPayloadHandlersEvent`** (`net.minecraft.network.protocol.
  common.custom.CustomPacketPayload` / `net.neoforged.neoforge.network.*`) is the full 1.21 networking rewrite
  replacing both old Forge `SimpleChannel` and Architectury's `NetworkChannel`. No named channels - one
  `PayloadRegistrar` per mod (via `event.registrar(version)`), each payload identified by its own `Type<T>`
  (a `record(ResourceLocation id)`). `PayloadRegistrar.playToClient/playToServer/playBidirectional(Type, StreamCodec,
  IPayloadHandler)` - handlers default to running on the main thread (wrapped in `MainThreadPayloadHandler`
  automatically) unless `.executesOn(HandlerThread.NETWORK)` is used. `PacketDistributor` (static methods) replaces
  manual `NetworkManager.PacketContext`/channel `sendTo*` calls: `sendToServer`, `sendToPlayer`,
  `sendToPlayersNear(level, excludedPlayer, x, y, z, radius, payload)`, `sendToPlayersTrackingEntity(entity,
  payload)`, `sendToAllPlayers`. `IPayloadContext.player()`/`.enqueueWork(...)` replace the old
  `Supplier<PacketContext>`/`.queue(...)` pattern.
