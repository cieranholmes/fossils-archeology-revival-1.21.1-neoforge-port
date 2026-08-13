---
name: fossils-phase2-items-progress
description: "Phase 2 (Items) COMPLETE as of 2026-08-13 - all 13 tasks done (items, special items, enchantments, loot function, advancement triggers, villager trades, food mappings), full compile+build+runServer/RCON verified including live villager trade offers with a real enchanted book. Includes a large 1.21.1 item/enchantment/projectile/loot-function/criterion-trigger/villager-trade/NBT-component API-drift catalog."
metadata:
  node_type: memory
  type: project
  originSessionId: 78d0a1c6-3588-48a6-800c-16a36b318da9
  modified: 2026-08-13T12:10:00.000Z
---

Continuation of [[fossils-migration-plan]] Phase 2, entered per [[fossils-phase-sequencing]]'s reorder decision (Phase 4 completed first). This supersedes that memory's "Phase 2 is now the recommended next phase" framing - Phase 2 is now actively underway with a live TaskList (13 tasks, dependency-ordered).

## User decision on scope (2026-08-13): food system stays simple

Source's food-value system is a full datapack-JSON loader + datagen provider + client-sync networking
(`FoodMappingsManager`, ~360 LOC). **User explicitly chose NOT to port this** - keep the target's existing simple
in-memory `FoodMappings`/`Diet`/`FoodType` (already built in Phase 4), and just port `FossilFoodMappings.register()`
to populate it from each species' `foodItem`/`cookedFoodItem`/`eggItem` fields directly in Java, no JSON/networking.
**Why:** for playing the mod as shipped, this loses nothing gameplay-facing (every species' own food values, feeder
behavior, hunting-goal food lookups all end up identical) - what's lost is datapack customizability without a
recompile, tag-based auto-recognition of other mods' food items (Phase 9 compat territory anyway), and live
`/reload` support, none of which affect out-of-the-box play. **How to apply:** if a future session is tempted to
port `FoodMappingsManager`/`FoodValueProvider`, check this decision first - it was deliberate, not a shortcut taken
under time pressure.

## Progress as of this session (2026-08-13)

Live TaskList, 13 tasks, dependency-ordered:
1. **DONE** - Materials: `material/ModArmorMaterials.java`, `material/ModToolTiers.java`.
2. **DONE** - `tags/ModItemTags.java` (~65 constants, `COOKED_MEAT`/`UNCOOKED_MEAT` bug faithfully replicated).
3. **DONE** - Base entity-coupled item classes: `PrehistoricEntityItem`, `DNAItem`, `MammalEmbryoItem`, `DinoBoneItem`,
   `MeatItem`, `NautilusMeatItem`, `EggItem`, `FishEggItem`, `DinoEggItem`, `BirdEggItem`, `CustomSpawnEggItem`.
4. **DONE** - `PrehistoricEntityInfo.register()`/`VanillaEntityInfo.register()` - the master per-species item driver
   (~60 species: DNA/bone/egg/meat/spawn-egg/bucket items, dependency-ordering into `ModItems.java`).
5. **DONE** (2026-08-13 continuation session) - `ModItems.java` full item list rewritten from scratch, replacing all
   13 Phase-4-era placeholders: fossils/relic-gear/javelins/scarab-tools/bone-armor/toys/spawn-eggs/music-discs/
   fossil-saplings all registered, matching source's tab assignments exactly (including source's own quirks -
   `ANCIENT_SWORD`, all 6 javelins, and `THERIZINOSAURUS_DOWN` get **no** creative tab at all in source, faithfully
   replicated rather than "fixed"). **Found and fixed a real bug**: `VanillaEntityInfo.register()`/
   `PrehistoricEntityInfo.register()` (the ~60-species item driver from tasks 3-4, described as "DONE" in the prior
   session) were never actually being called from anywhere - `ModItems.register(IEventBus)` only called
   `ITEMS.register(modEventBus)`. This meant every species' DNA/bone/egg/meat/spawn-egg item silently never
   registered. Fixed by calling both `.register()` methods at the top of `ModItems.register(IEventBus)`, matching
   source's `register()` ordering (population calls before the `DeferredRegister` itself is handed the event bus -
   safe because `.register(name, supplier)` just populates an internal map; the actual `RegisterEvent` fires later).
   **One item deliberately not registered**: `FERN_SEED` (`ItemNameBlockItem` wrapping `ModBlocks.FERNS`) - the
   `FernsBlock`/`ModBlocks.FERNS` plant block was never ported in Phase 1 (confirmed via grep, doesn't exist in
   target at all). This is Phase 1/Blocks catch-up scope, not a Phase 2 oversight; flagged with a comment at the
   `FERN_SEED_FOSSIL` registration site in `ModItems.java` rather than silently skipped or worked around.
6. **DONE** - Entity-coupled special items: `AncientSwordItem`, `ToyBallItem`, `ToyTetheredLogItem`,
   `ToyScratchingPostItem`, `ArtificialHoneycombItem`, `FossilItem`, `JavelinItem` (from the prior session) plus, new
   this session:
   - `AncientChestBlockItem`/`AnuStatueBlockItem`/`AnubiteStatueBlockItem`/`SarcophagusBlockItem`: turned out to
     **already be effectively done from Phase 1**, just not under these class names - `ModBlocks.java`'s
     `blockWithCustomBlockItem(...)` helper already registers all 4 blocks with a generic `CustomBlockItem`
     (plain `BlockItem` + a `getName()` override), which is exactly the "plain BlockItem, no renderer" simplification
     this task already planned to do. No new code needed; just noting these 4 are covered.
   - `AncientHelmetItem`: written as a concrete (non-abstract) `ArmorItem implements GeoItem` with
     `AnimatableInstanceCache`/no-op `registerControllers` (import from `software.bernie.geckolib.animation.
     AnimatableManager` and `software.bernie.geckolib.animatable.instance.AnimatableInstanceCache` - NOT the
     `software.bernie.geckolib.core.*` paths source used; those packages don't exist in this GeckoLib version,
     confirmed by matching the import paths already working in `Prehistoric.java`). Registered in `ModItems.java`
     with `ModArmorMaterials.ANCIENT` + `ArmorItem.Type.HELMET.getDurability(ModArmorMaterials.ANCIENT_DURABILITY_MULTIPLIER)`.
     Renderer wiring (GeoArmorRenderer/AncientHelmetRenderer) still deferred to Phase 5 as planned.
   - `CustomRecordItem`: **confirmed and resolved** the suspected `JukeboxSong` rework (see drift catalog below) -
     `RecordItem` is gone entirely; music discs are now plain `Item`s with `.jukeboxPlayable(ResourceKey<JukeboxSong>)`,
     and the song data (sound/description/length/comparator-output) lives in a datapack JSON registry entry, not
     Java. Implemented as 4 JSON files under `data/fossils/jukebox_song/{bones,anu,scarab,discovery}.json`
     referencing the existing `ModSounds` sound events and reusing the existing `item.fossils.music_disc_*.desc`
     lang keys for the tooltip (via a `{"translate": "..."}` text component in the JSON, instead of the new default
     `jukebox_song.<namespace>.<name>` key vanilla's own `JukeboxSongs.bootstrap()` uses) - `CustomRecordItem.java`
     itself was never created in target; no per-loader class was needed at all, a genuine simplification.
7. **DONE** - `LaserPointerItem` and `StoneTabletItem`, both boot-verified via a live `runServer` (clean
   `Done (1.447s)!`, no exceptions, "Loaded data for 59 entities"). Notable drift handled:
   - `ItemStack.getTag()`/`getOrCreateTag()` are **gone entirely** - arbitrary NBT on a stack now goes through the
     `DataComponents.CUSTOM_DATA` component: read via `stack.get(DataComponents.CUSTOM_DATA)` (nullable) +
     `.copyTag()` (returns a **copy** - mutating it does NOT write back to the stack), write/mutate via the static
     `CustomData.update(DataComponentType<CustomData>, ItemStack, Consumer<CompoundTag>)` helper (get+mutate+put-back
     in one call). Established pattern already existed in `PrehistoricSwimmingBucketable.saveToBucketTag` from
     Phase 4 - `LaserPointerItem`/`StoneTabletItem` are the first *item* (non-entity-bucket) uses of it.
   - `EntityType.updateCustomEntityTag(Level, Player, Entity, CompoundTag)`'s 4th param is now `CustomData`, not
     `CompoundTag` - pass the `CustomData` object straight from `stack.get(DataComponents.CUSTOM_DATA)`, no
     `.copyTag()` needed (same "drop/retype the trailing NBT param" family as the `finalizeSpawn` 5th-arg drift
     already documented, but this one *changed type* rather than being dropped).
   - `LivingEntity.broadcastBreakEvent(InteractionHand)` **no longer exists** - `ItemStack.hurtAndBreak(int amount,
     LivingEntity entity, EquipmentSlot slot)` now does the shrink-on-break AND the broadcast internally (calls
     `entity.onEquippedItemBroken(item, slot)`), so manual `stack.shrink(1)` + `broadcastBreakEvent(...)` call pairs
     collapse into a single `hurtAndBreak(amount, entity, slot)` call - a genuine simplification already established
     via `Mammoth`/`Therizinosaurus`/`Elasmotherium`/`WhipItem` in Phase 4, now also applied to `LaserPointerItem`.
   - `StoneTablet.dropItem` was ported in Phase 4 with a flagged fallback (`Items.STONE` instead of the real item,
     since `ModItems` didn't exist yet) - updated it now to `ModItems.STONE_TABLET.get()` per that flag's own note.
   - Similarly, `ToyBall`/`ToyTetheredLog`/`ToyScratchingPost`'s `getPickResult()` fallbacks (`Items.SLIME_BALL`/
     `Items.OAK_LOG`, flagged in Phase 4 pending this exact phase) were swapped to the real
     `ModItems.TOY_BALLS.get(getColor()).get()` / `ModItems.TOY_TETHERED_LOGS.get(getWoodTypeName()).get()` /
     `ModItems.TOY_SCRATCHING_POSTS.get(getWoodTypeName()).get()` now that those maps exist.
   - `LaserPointerItem`'s particle-spawning call (`ModParticles.LASER_PARTICLE`) needed `ModParticles.java` itself,
     which didn't exist in target at all (source's whole `client/particle/ModParticles` class was unported). Ported
     it now (registration only, 5 particle types) since it's small and has no rendering-code dependency - the actual
     `RegisterParticleProvidersEvent` client factory wiring is still Phase 5 scope; referencing an unregistered-
     provider particle type from server code is safe (vanilla just fails to render it client-side, doesn't crash).
8. **DONE** (2026-08-13, third continuation session) - `CustomizeToDinoFunction`/`ModLootItemFunctionTypes` ported
   to the `MapCodec` model (`com.github.fossils.loot` package, new). `CustomizeToDinoFunction.CODEC` built via
   `RecordCodecBuilder.mapCodec(commonFields(...).and(LootContext.EntityTarget.CODEC.fieldOf("entity")...))`
   (`LootItemFunctionType<T>` is now `record LootItemFunctionType<T extends LootItemFunction>(MapCodec<T> codec)`,
   confirmed against decompiled vanilla `SetNameFunction`/`SmeltItemFunction`). `ModLootItemFunctionTypes` uses a
   plain `DeferredRegister<LootItemFunctionType<?>>` against `Registries.LOOT_FUNCTION_TYPE`, same shape as every
   other registry in this codebase. **Scope note**: only the Java mechanism was ported, not the ~60 entity loot
   table JSONs that would reference `"function": "fossils:customize_to_dinosaur"` - authoring those is Phase
   7/datagen-adjacent territory (same category as the fossil-block loot tables flagged in task 10 below), not
   Items-phase scope; no entity loot tables exist in target yet at all.
9. **DONE** (2026-08-13, third continuation session) - 4 real advancement triggers (`OpenSarcophagusTrigger`,
   `ScarabTameTrigger`, `ImplantEmbryoTrigger`, `IncubateEggTrigger`) replacing the no-op `ModTriggers` stub, using
   1.21.1's Codec-based `SimpleCriterionTrigger.SimpleInstance` (a `record` implementing that interface, not the
   old `AbstractCriterionTriggerInstance` subclass) - see the new drift catalog entry below for the full shape.
   `ModTriggers` is now a `DeferredRegister<CriterionTrigger<?>>` against `Registries.TRIGGER_TYPE`, with each
   constant a `DeferredHolder<CriterionTrigger<?>, ConcreteTriggerType>`. The 4 existing call sites
   (`ThrownBirdEgg`/`DinosaurEgg`/`FishEggItem`'s `INCUBATE_EGG_TRIGGER.trigger(player, entity)`,
   `Prehistoric`'s `SCARAB_TAME_TRIGGER.trigger((ServerPlayer) player)`) were updated to add `.get()` before
   `.trigger(...)` - the only call-site change, method name/args stayed identical as intended.
   **Found two real call sites that don't fire the new triggers yet, deliberately left as-is (out of this task's
   scope, flagged for whoever does the underlying feature)**:
   - `OPEN_SARCOPHAGUS_TRIGGER`'s natural call site is `SarcophagusBlock`, but target's `SarcophagusBlock` only
     overrides `useWithoutItem` (empty-hand open/close) - it's **missing the entire scarab-gem-unlock branch**
     source had in its single old `use(...)` method (checking `ItemStack.is(ModItems.SCARAB_GEM)`, setting
     `STATE_UNLOCKED`, lighting the 3 pillars, consuming the gem). This is a real Phase-1/Blocks gap (the branch
     was dropped during Phase 1 porting, plausibly because `ModItems.SCARAB_GEM` didn't exist yet at that time),
     not an advancement-triggers problem - 1.21+ also splits `Block.use` into `useItemOn(ItemStack, ...)` +
     `useWithoutItem(...)`, so re-adding this branch means a new `useItemOn` override, not just calling the
     trigger. `ModItems.SCARAB_GEM` exists now (Phase 2 done), so this is unblocked whenever someone picks it up.
   - `IMPLANT_EMBRYO_TRIGGER`'s natural call site is source's `ModEvents.java` `InteractionEvent.INTERACT_ENTITY`
     listener (embryo-item-on-animal interaction triggering mammal pregnancy via Architectury's `ModCapabilities`).
     Target has the NeoForge-attachment successor (`com.github.fossils.capabilities.ModCapabilities`) and
     `MammalEmbryoItem` already, but **no interaction listener/override wires them together at all** - the whole
     "use embryo item on an adult mammal to start pregnancy" mechanic is unported. This is Entities/gameplay-event
     scope (arguably a Phase 4 gap, since Phase 4 is marked complete), not Items/advancements scope.
10. **DONE** (2026-08-13, second continuation session) - `ArcheologyEnchantment`/`PaleontologyEnchantment` ported as
    pure datapack JSON, same treatment as `JukeboxSong` - no `Enchantment` subclass exists in target at all (matches
    the task's own framing: the `checkCompatibility(Enchantment other)` override point is simply gone in 1.21.1).
    - `data/fossils/enchantment/{archeology,paleontology}.json`: `description` reuses the existing
      `enchantment.fossils.{archeology,paleontology}` lang keys (already present from an earlier phase) via
      `{"translate": "..."}`; `supported_items` is the tag `#minecraft:enchantable/mining_loot` (the same
      applicability tag vanilla's own Fortune/Silk Touch use - a solid match since source's exclusion list already
      named Silk Touch specifically); `slots: ["mainhand"]` (`EquipmentSlotGroup`'s `StringRepresentable` id,
      confirmed via `EquipmentSlotGroup.java`); `min_cost`/`max_cost` reproduce source's exact formula
      (`minCost=1+10*(level-1)` → `{"base": 1, "per_level_above_first": 10}`, `maxCost=minCost+50` →
      `{"base": 51, "per_level_above_first": 10}`); `max_level: 3` (unchanged from source); `exclusive_set` is a
      plain JSON array of two enchantment IDs (`minecraft:silk_touch` + the other one) rather than a custom tag -
      `RegistryCodecs.homogeneousList` accepts either a `"#tag"` string or a direct id list, and `Enchantment
      .areCompatible` checks both holders' exclusive sets, so listing the exclusion from just one side would even
      have sufficed, but both sides declare it for clarity/symmetry with source's two mirrored `checkCompatibility`
      overrides.
    - `weight: 1` and `anvil_cost: 8` for both: 1.20.1's `Enchantment.Rarity.VERY_RARE` (what source's constructor
      passed) no longer exists, but its `getWeight()` value (1) carries over directly to the new `weight` field: 1.21
      split what was one `Rarity` enum into two explicit numeric fields, `weight` (table-roll weight, same numbers
      as the old rarity tiers: COMMON=10/UNCOMMON=5/RARE=2/VERY_RARE=1) and the new `anvil_cost` (COMMON=1/
      UNCOMMON=2/RARE=4/VERY_RARE=8, doubling per tier) - cross-checked against real vanilla values (Silk Touch,
      itself VERY_RARE and in the same exclusion group as these two, has anvil_cost 8; Fortune/Mending, RARE, have
      anvil_cost 4; Unbreaking, UNCOMMON, has anvil_cost 2) so `8` for a VERY_RARE-equivalent custom enchantment is
      a faithful carry-over, not a guess made up from nothing.
    - `ModEnchantments.java` still exists in target, but as a `ResourceKey<Enchantment>`-holding `interface` (mirrors
      vanilla's own `Enchantments.java`/the `JukeboxSongs`-style pattern already used for the music discs above) -
      **not** a `DeferredRegister<Enchantment>`, since Enchantment is a dynamic/datapack registry with no compile-
      time Java registration path at all (same category as `JukeboxSong`, confirmed via `VanillaRegistries.java`).
    - Verified via a live `runServer` boot (`Done (1.740s)!`, clean, no datapack/registry errors) - a broken
      enchantment JSON (bad tag reference, malformed cost object, etc.) would have surfaced as a datapack loading
      failure during world/registry setup, not a silent no-op.
    - **Found while investigating (out of this task's scope, noted for whoever ports Blocks-phase loot tables)**:
      source's fossil-bearing stone block loot tables (`common/src/generated/resources/data/fossil/loot_tables/
      blocks/fossil_*.json`) gate which "how many fossils" sub-loot-table gets picked via `minecraft:match_tool`
      conditions checking `fossil:archeology`/`fossil:paleontology` enchantment levels 1-3 on the mining tool - this
      is the actual gameplay payoff of these two enchantments (better fossil yields from stone), not anything read
      from Java. Not touched this session - it's Phase 1/Blocks loot-table-porting territory, not Phase 2/Items.
11. **DONE** (2026-08-13, third continuation session) - `ModVillagers`/`ModTrades` ported to the `ItemCost`-based
    `MerchantOffer` model - see the new drift catalog entry below for the full API shape. `ModVillagers` is now two
    `DeferredRegister`s (`Registries.POINT_OF_INTEREST_TYPE`/`Registries.VILLAGER_PROFESSION`) instead of
    Architectury's; **the old `LifecycleEvent.SETUP`/`registerBlockStates` step is gone entirely, not just
    reworked** - NeoForge's `NeoForgeRegistryCallbacks.PoiTypeCallbacks` auto-registers every `PoiType`'s
    `matchingStates()` into the blockstate→PoiType map via a registry `AddCallback`, for modded PoiTypes exactly
    like vanilla ones, so the manual step source needed is now redundant (confirmed by reading
    `NeoForgeRegistryCallbacks.java` - trying to call it manually would likely double-register and throw). The
    actual trade-list injection (`VillagerTradesEvent`) is a new `VillagerTradeEvents` class
    (`@EventBusSubscriber(modid=...)`, game-bus `@SubscribeEvent`) - NeoForge's own event (not Forge's, but same
    shape: `event.getTrades()`/`event.getType()`) replacing source's Forge-side-only `ForgeModEvents.addCustomTrades`
    (Architectury doesn't have a common-code version of this event at all, so source only had it in the `forge`
    subproject - one of the few places the single-module port had no direct "common" source to copy from).
    `EnchantBookForEmeralds` resolves `ModEnchantments.ARCHEOLOGY`/`PALEONTOLOGY`'s `ResourceKey<Enchantment>` to a
    `Holder<Enchantment>` via `trader.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
    .getHolderOrThrow(key)` inside `getOffer(...)` (the `Entity trader` param is the villager itself, `.level()`
    already exposes a `Level` with registry access - no extra plumbing needed). **`Enchantment.isTreasureOnly()` is
    gone entirely in 1.21.1** (see drift catalog) with no direct replacement API; dropped the "double cost for
    treasure enchantments" branch outright since it's genuinely dead code for this trade's only two actual callers
    (`ARCHEOLOGY`/`PALEONTOLOGY`, neither treasure-only) - not a partial port, a clean removal of unreachable logic.
    **Runtime-verified, not just compiled**: booted a live server, summoned a level-5 Archeologist villager via
    RCON, and read its `Offers` NBT back - it had the real level-5 trades, including a genuine
    `minecraft:enchanted_book` with `stored_enchantments: {"fossils:archeology": 2}` costing `18` emeralds, proving
    the whole chain (profession → POI → trade injection → `ItemCost` → enchantment-holder resolution →
    `EnchantedBookItem.createForEnchantment`) works end-to-end, not just that it compiles.
12. **DONE** (2026-08-13, third continuation session) - `FossilFoodMappings` ported as planned: a plain static
    `register()` method (`com.github.fossils.food` package) iterating `PrehistoricEntityInfo.values()`/
    `VanillaEntityInfo.values()` and calling the target's existing `FoodMappings.addFish`/`addMeat`/`addEgg`
    directly - no new abstraction, matches source 1:1 minus the JSON/networking layer per the standing decision.
    **Timing matters**: this can't run at mod-construction time (when `ModItems.register(modEventBus)` etc. are
    called) because `PrehistoricEntityInfo`/`VanillaEntityInfo`'s `foodItem`/`cookedFoodItem`/`eggItem`/
    `cultivatedBirdEggItem`/`birdEggItem` fields are populated as a side effect *inside* each item's `DeferredRegister`
    factory `Supplier` (see the "NeoForge has no `DeferredRegister#listen` equivalent" pattern below) - those
    Suppliers only run when the `Item` `RegisterEvent` actually fires, which is *after* mod construction returns.
    Wired via a second `modEventBus.addListener((FMLCommonSetupEvent event) -> FossilFoodMappings.register())` in
    `FossilsAndArcheologyRevival`'s constructor (common setup fires after all registry events, guaranteeing the
    fields are populated) - reuses the same event already used for `FlammableRotatedPillarBlock.registerAllStripped()`.
13. **DONE** (2026-08-13, third continuation session) - Full `./gradlew build` (compile + tests, if any) passes
    clean, plus a live `runServer` boot (`Done (1.897s)!`, RCON up, zero exceptions/errors in the full log) with the
    villager-trade RCON verification described in task 11 above. **Phase 2 (Items) is now COMPLETE.**

## Reusable pattern: NeoForge has no `DeferredRegister#listen` equivalent

Architectury's `DeferredRegister.register(name, factory).listen(consumer)` (used throughout source's
`PrehistoricEntityInfo.register()` to populate `info.dnaItem`/etc. once the real item exists) has **no NeoForge
equivalent**. Fix: invoke the "listener" **inside the factory `Supplier` itself**, as a side effect at the exact
moment the concrete object is constructed (which happens during that registry's `RegisterEvent`, not at classload
time):
```java
DeferredItem<Item> holder = ITEMS.register(name, () -> {
    SomeItem item = new SomeItem(info);
    info.someField = item;   // side-effect assignment, same timing guarantee as .listen(...)
    return item;
});
```
Applied throughout `DinoBoneItem.registerItem`, `PrehistoricEntityInfo.register()`'s private `registerItem` helper,
and `VanillaEntityInfo.register()`'s equivalent. Also: since `PrehistoricEntityItem`'s constructor no longer calls
Architectury's `arch$tab(...)` mixin trick, tab membership (`ModTabs.addToMobItemTab(...)`) is called explicitly at
each of these registration call sites instead of being baked into the item base class - matches the pattern already
established in `ModItems.java`'s own `register(name, factory, tabConsumer)` helper.

## Reusable pattern: cross-registry references inside a DeferredRegister factory are safe

`BuiltInRegistries.java`'s field declaration order fixes `RegisterEvent` firing order (confirmed by reading the
decompiled source: `ENTITY_TYPE` is declared before `ITEM`). Since a `DeferredRegister.Items.register(name,
supplier)` factory only runs **during** the `Item` `RegisterEvent`, referencing an already-registered
`ModEntities.X.get()` inside that supplier is always safe, with **no need for reflection/deferred-wrapper tricks** -
this is how `MobBucketItem` (which needs a raw `EntityType<? extends Mob>` eagerly at construction) gets ported
directly: `new MobBucketItem(info.entitySupplier.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties)`
works fine inside the lazy registration factory. Contrast with `SpawnEggItem`, where NeoForge ships a **purpose-built**
`net.neoforged.neoforge.common.DeferredSpawnEggItem` (extends `SpawnEggItem`, takes a `Supplier<? extends
EntityType<? extends Mob>>`, overrides `getDefaultType()`) - prefer that over the manual-ordering-safety argument
when a "Deferred" variant already exists, since it also gets extra wiring for free (dispense behavior, spawn-egg
color registration via its own `FMLCommonSetupEvent`/`RegisterColorHandlersEvent` subscribers). `CustomSpawnEggItem`
now `extends DeferredSpawnEggItem` instead of Architectury's `ArchitecturySpawnEggItem`.

## New MC 1.20.1->1.21.1 API drift discovered this session (append to the running catalogs in
[[fossils-phase4-entities-progress]]/[[fossils-combat-and-scaling-bugs-fixed]] - verified against decompiled
sources, not guessed)

**Armor/materials:**
- **`ArmorMaterial` became a registry record**, not an interface an enum implements: `record ArmorMaterial(Map<Type,
  Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
  List<Layer> layers, float toughness, float knockbackResistance)`, registered via `BuiltInRegistries.ARMOR_MATERIAL`
  (`Registries.ARMOR_MATERIAL` for a `DeferredRegister<ArmorMaterial>`). **The old per-material "durability
  multiplier" field is gone** - `ArmorItem.Type` now carries a *fixed* base durability per slot
  (`HELMET`=11,`CHESTPLATE`=16,`LEGGINGS`=15,`BOOTS`=13,`BODY`=16 - matches vanilla's old hardcoded
  `HEALTH_FUNCTION_FOR_TYPE` values exactly) and exposes `Type.getDurability(multiplier)`; the *caller* building each
  armor **item** must multiply it in explicitly: `new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(9))`,
  mirroring vanilla's own `Items.java` (`IRON_HELMET` = `... .durability(ArmorItem.Type.HELMET.getDurability(15))`).
  Store the old multiplier as a plain constant on your `ModArmorMaterials` class for later use at each armor item's
  registration site.
- **A fully GeckoLib-rendered custom armor item (no flat vanilla-style texture) can register with an empty
  `layers` list** (`List.of()`) - GeckoLib's `GeoArmorRenderer`/`IClientItemExtensions.getHumanoidArmorModel` bypasses
  the vanilla layer-texture system entirely, so there's nothing to declare. Only give a real `ArmorMaterial.Layer`
  (`ResourceLocation.fromNamespaceAndPath(modid, name)`) for armor that DOES use vanilla-style flat 2D layer textures.
- **Armor layer texture path convention changed namespace expectations**: source (1.20.1) stored a shared/reused
  armor texture under `assets/minecraft/textures/models/armor/<name>_layer_{1,2}.png` (old hardcoded-vanilla-path
  convention) even for a modded material; 1.21.1's `ArmorMaterial.Layer` resolves its texture from the
  `ResourceLocation` you pass, so copy the texture files into the **mod's own** namespace/path
  (`assets/<modid>/textures/models/armor/`) instead of leaving them under `minecraft:`.

**Tools/tiers:**
- **`Tier` interface dropped `getLevel()`** (the old numeric mining level 0-4) in favor of
  `getIncorrectBlocksForDrops(): TagKey<Block>` (a default `createToolProperties(TagKey<Block> minesAndDropsTag)`
  method builds the actual `Tool` data component from it). Map old numeric levels to vanilla's own
  `BlockTags.INCORRECT_FOR_X_TOOL` tags by equivalent tier (`0`=WOOD, `1`=STONE, `2`=IRON, `3`=DIAMOND, `4`=NETHERITE
  - level 4 = "can mine everything" = the tag vanilla itself leaves empty). `Tier` itself is still a plain interface
  an enum can implement directly (unlike `ArmorMaterial`) - no registry involved, ports almost mechanically otherwise.
- **`SwordItem` constructor dropped `(int damage, float attackSpeed)`** entirely - now just `(Tier, Properties)` (or
  a 3-arg overload taking an explicit `Tool` component). Damage/attack-speed are baked into the item's
  `Item.Properties.attributes(SwordItem.createAttributes(tier, damage, attackSpeed))` call instead, matching the
  pattern already used for the `TOOTH_DAGGER` placeholder in `ModItems.java`.
- **`Item.Properties.maxStackSize` is no longer a directly-mutable field** - everything routes through
  `DataComponents` now (`stacksTo(int)` → `component(DataComponents.MAX_STACK_SIZE, n)`). **`durability(int)` still
  forces max-stack-size to 1 as an internal side effect** (same as 1.20.1) - the old "stack size + durability
  together" hack (directly poking the private field after calling `durability()`) is no longer possible, but is
  also **no longer needed**: just call `.stacksTo(n)` *after* `.durability(d)` and the component system naturally
  ends up with both set correctly, since each call is just an independent component-map `put`.
- **`Item.getUseDuration(ItemStack)` is now `getUseDuration(ItemStack, LivingEntity)`** (2-arg) - update both the
  override and any internal call sites (`this.getUseDuration(stack, shooter)`).

**Enchantments (affects any item reading enchantment levels, not just the two custom Enchantment classes):**
- **Enchantment constants moved from `Enchantment` instances to `ResourceKey<Enchantment>`, and several were
  renamed** - `Enchantments.POWER_ARROWS`→`POWER`, `PUNCH_ARROWS`→`PUNCH`, `FLAMING_ARROWS`→`FLAME`,
  `INFINITY_ARROWS`→`INFINITY` (the "_ARROWS" suffix was dropped entirely, not just the type). Enchantments are now
  data-driven registry entries (`Registries.ENCHANTMENT`), not compile-time `Enchantment` subclass instances.
- **`EnchantmentHelper.getItemEnchantmentLevel(Holder<Enchantment>, ItemStack)` still compiles but is
  `@Deprecated`** (just delegates to `stack.getEnchantmentLevel(enchantment)` now) - prefer
  `EnchantmentHelper.getTagEnchantmentLevel(Holder<Enchantment>, ItemStack)` for reading NBT/component-stored
  enchantment data directly off a stack (what this mod's item-side enchantment checks actually want).
- **Resolving a `Holder<Enchantment>` from a `ResourceKey<Enchantment>` constant needs a registry lookup**: `Holder<Enchantment>
  holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);`
  (`Registry<T>.getHolderOrThrow(ResourceKey<T>)` returns a `Holder.Reference<T>`, unchanged API, just needs the
  extra one-liner since the constant itself is no longer directly usable as a `Holder`).
- **This affects the still-unported Task 10** (`ArcheologyEnchantment`/`PaleontologyEnchantment`): the whole
  "`extends Enchantment`, override `checkCompatibility(Enchantment other)`" pattern from 1.20.1 has **no direct
  equivalent** in 1.21.1 - enchantments are pure data (a datapack JSON `data/<modid>/enchantment/<name>.json` +
  registry entry), and "these two enchantments are mutually exclusive" is now expressed via an **exclusive-set
  tag** (`#minecraft:exclusive_set/...`-style, or a custom one) referenced from each enchantment's own JSON
  definition, not a Java method override. Don't try to port the Java class shape at all for this one - author the
  datapack JSON + tag directly.

**Projectiles/arrows (`AbstractArrow` and anything extending it, e.g. this mod's `Javelin`):**
- **`AbstractArrow`'s constructor accepting a shooter now requires 5 args**: `(EntityType, LivingEntity shooter,
  Level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon)` - already known from
  [[fossils-phase4-entities-progress]], but this session found the *consequence* of what you pass for
  `firedFromWeapon` matters a lot, see below.
- **`AbstractArrow.setKnockback(int)` no longer exists at all.** Knockback bonus from the Punch enchantment is now
  applied **automatically and generically** by `AbstractArrow.doKnockback()` via
  `EnchantmentHelper.modifyKnockback(serverLevel, this.firedFromWeapon, target, damageSource, baseKnockback)` -
  driven entirely by whatever `ItemStack` was passed as `firedFromWeapon` at construction. Similarly, **damage bonus
  (Power) is applied automatically** via `EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), ...)`
  inside the arrow's own hit-processing (`getWeaponItem()` just returns `firedFromWeapon`). **Confirmed by reading
  vanilla's own `BowItem.releaseUsing` in this MC version: it contains zero enchantment-reading code at all** -
  every bit of Power/Punch handling for a real vanilla bow-fired arrow happens through this generic
  `firedFromWeapon`-driven mechanism, not manual per-shot enchantment checks like 1.20.1 used.
- **Practical rule**: if your custom `AbstractArrow` subclass wants to reuse vanilla's Power/Punch mechanics for
  free, pass the actual (enchanted) item stack as `firedFromWeapon` in the constructor instead of `null` - do **not**
  manually re-derive `EnchantmentHelper.getItemEnchantmentLevel(POWER/PUNCH, stack)` and apply bonuses by hand, that
  whole mechanism is gone (`setBaseDamage`/`setKnockback`-style manual adjustment based on a manual enchantment read
  is the *old*, no-longer-idiomatic pattern). Applied this to `Javelin`: its constructor now takes the thrown
  `ItemStack` and forwards it as `firedFromWeapon` (previously hardcoded `null`); `JavelinItem.releaseUsing` dropped
  its manual Power/Punch reads entirely as a result - a genuine simplification, not just a rename.
- **Caveat - not every self-thrown weapon should do this.** `ThrownTrident` (vanilla's own closest analog to a
  self-contained thrown weapon, as opposed to ammunition fired from a separate launcher) **deliberately passes
  `null` for `firedFromWeapon`** even for a real player-thrown trident, and instead reads its own enchantments
  (Loyalty/Riptide/Channeling) directly and manually off its stored `pickupItemStack` wherever needed. The
  `firedFromWeapon`-driven generic mechanism specifically piggybacks on `AbstractArrow`'s own internals - it's the
  right choice for `Javelin` because `Javelin extends AbstractArrow` directly and specifically wants to reuse the
  same Power/Punch mechanics real arrows get, not because "self-thrown weapon" implies it generally. Flame
  (ignite-on-hit) has **no generic hook at all** in `AbstractArrow` - that still needs a manual enchantment read
  (`EnchantmentHelper.getTagEnchantmentLevel(FLAME holder, stack)` → `entity.igniteForSeconds(100)` on the arrow
  once constructed) as a deliberate, reasonable simplification, not an oversight.
- **`EntityType.create(...)`'s 7-arg overload (with a nullable `CompoundTag` AND a nullable `Consumer<T>`) is now
  6-arg**: `create(ServerLevel, @Nullable Consumer<T>, BlockPos, MobSpawnType, boolean, boolean)` - the `CompoundTag`
  param was dropped entirely (not merged into the Consumer), so a source call like `type.create(level, null, null,
  pos, spawnType, false, false)` (7 args after the level) needs one `null` dropped, not just renamed.

**Recurring drop-the-5th-arg reminder:** `Mob.finalizeSpawn`'s dropped `@Nullable CompoundTag dataTag` 5th param
(documented in [[fossils-phase4-entities-progress]]) came up again at two more call sites this session
(`FishEggItem.spawnMob`, `ArtificialHoneycombItem.useOn`) - if a source file calls `finalizeSpawn(level, difficulty,
spawnType, groupData, someNbtOrNull)`, just drop the trailing arg; there is still no replacement hook for the
NBT-based use case (bee-spawn-from-honeycomb's own use of it was dropped with a flag comment, matching the
established "no replacement found, drop and flag" resolution pattern).

**New drift discovered in the 2026-08-13 continuation session (ModItems.java full rewrite + LaserPointer/StoneTablet):**

- **`RecordItem` was deleted entirely; music discs are now a datapack-JSON-driven registry (`JukeboxSong`), same
  category as `Enchantment`.** Confirmed via `VanillaRegistries.java`: both `Registries.ENCHANTMENT` and
  `Registries.JUKEBOX_SONG` are registered with `.add(key, SomeClass::bootstrap)` - i.e. dynamic/datapack registries,
  not code registries like Item/Block. A disc item is now just `new Item(new Item.Properties().stacksTo(1)
  .rarity(Rarity.RARE).jukeboxPlayable(ResourceKey<JukeboxSong>))`; `JukeboxSong` itself (`record(Holder<SoundEvent>
  soundEvent, Component description, float lengthInSeconds, int comparatorOutput)`) is defined as a JSON file at
  `data/<modid>/jukebox_song/<name>.json` with fields `sound_event` (a sound event ID string), `description` (a text
  component, e.g. `{"translate": "..."}` to reuse an existing lang key), `length_in_seconds`, `comparator_output`
  (int 0-15). No Java class/registration needed for the song data itself - only the JSON + the `jukeboxPlayable(...)`
  call on the item. `Item.Properties.jukeboxPlayable(ResourceKey<JukeboxSong>)` is the new builder method (replaces
  the old constructor args entirely).
- **`DiggerItem` subclasses (`PickaxeItem`/`AxeItem`/`ShovelItem`/`HoeItem`) dropped their `(float attackDamage,
  float attackSpeed)` constructor params**, same rework as `SwordItem` (already documented above) - now just
  `(Tier, Properties)`, with each subclass exposing its own static `createAttributes(Tier, float damage, float
  speed)` (all four delegate to `DiggerItem.createAttributes` under the hood, callable via the subclass name per
  vanilla's own `Items.java` convention: `PickaxeItem.createAttributes(Tiers.IRON, 1.0F, -2.8F)`, etc.) - build via
  `new Item.Properties().attributes(PickaxeItem.createAttributes(tier, damage, speed))`, mirroring the `SwordItem`/
  `TOOTH_DAGGER` pattern already established.
- **A `DeferredRegister.Items`/`DeferredRegister<T>` field typed to a specific item subclass (e.g. `DeferredItem<
  JavelinItem>`, `DeferredItem<ToyBallItem>`) must be assigned from `ITEMS.register(name, supplier)` called
  directly** (its generic signature is `<I extends Item> DeferredItem<I> register(String, Supplier<? extends I>)`,
  which infers `I` from the lambda's return type) - NOT from a local wrapper helper whose parameter is declared as
  the bland `Supplier<Item>` (like this file's own `register(String, Supplier<Item>, Consumer<DeferredItem<Item>>)`
  convenience overload), because passing a `Supplier<SpecificType>` where `Supplier<Item>` is expected fails
  generics invariance even though a **lambda literal** would compile fine there (lambdas are target-typed per call
  site, so `() -> new JavelinItem(...)` type-checks against a `Supplier<Item>` parameter - the mismatch is the
  wrapper's `DeferredItem<Item>` *return* type failing to satisfy a `DeferredItem<JavelinItem>` field, not the
  argument itself). Affected this session: the 6 `JavelinItem` fields and the three toy-item registration helpers
  (`registerBall`/`registerTetheredLog`/`registerScratchingPost`) all call `ITEMS.register(...)` directly instead of
  going through the generic-losing wrapper.
- **Source deliberately omits a creative-tab call for some items** (`ANCIENT_SWORD`, all 6 javelins,
  `THERIZINOSAURUS_DOWN`, the 4 `FossilItem`s) - confirmed by reading source's `ModItems.java` line-by-line rather
  than assuming every item needs `ModTabs::addToXItemTab`. Faithfully replicated (these items are registered but not
  creative-tab-discoverable), not "fixed" - matches the project's general faithful-port-over-silent-improvement
  policy (see [[fossils-known-upstream-bugs]]).
