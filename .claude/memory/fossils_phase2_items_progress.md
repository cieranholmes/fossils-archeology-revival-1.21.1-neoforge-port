---
name: fossils-phase2-items-progress
description: "Phase 2 (Items) IN PROGRESS as of 2026-08-13 - materials/tags/base item classes/master per-species registration driver done; entity-coupled special items mostly done; ModItems.java full list, heavy NBT items, loot/advancements/enchantments/villager/food-mappings not yet started. Includes a large 1.21.1 item/enchantment/projectile API-drift catalog."
metadata:
  node_type: memory
  type: project
  originSessionId: 78d0a1c6-3588-48a6-800c-16a36b318da9
  modified: 2026-08-13T08:53:51.871Z
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
5. **IN PROGRESS** - `ModItems.java` full item list (still just the Phase 4-era 13 placeholders; needs the full
   fossils/relic-gear/javelins/scarab-tools/bone-armor/toys/spawn-eggs list, and must call
   `VanillaEntityInfo.register()`/`PrehistoricEntityInfo.register()` before `ITEMS.register(modEventBus)` fires).
6. **IN PROGRESS** - Entity-coupled special items: `AncientSwordItem`, `ToyBallItem`, `ToyTetheredLogItem`,
   `ToyScratchingPostItem`, `ArtificialHoneycombItem`, `FossilItem`, `JavelinItem` all done. **Still needed**: the 6
   collapsed forge/fabric-paired classes (`AncientHelmetItem`, `CustomRecordItem`, `AncientChestBlockItem`,
   `AnuStatueBlockItem`, `AnubiteStatueBlockItem`, `SarcophagusBlockItem`) - each currently an `@ExpectPlatform` shell
   in source with real logic split across `item/forge/*Impl.java`/`item/fabric/*Impl.java`; collapse each pair into
   one concrete NeoForge class (the Forge side is almost always the closer seed, per [[project-neoforge-port]]'s
   established convention) - **except `CustomRecordItemImpl`, where Forge/Fabric actually disagree** (Forge skips
   `.get()` on the sound supplier and multiplies seconds×20; Fabric calls `.get()` and passes seconds unmodified) -
   verify against the actual `RecordItem` constructor in this MC version before picking one, don't copy either blindly.
7. **NOT STARTED** - `LaserPointerItem` (heaviest raw-NBT/UUID/tick-lifecycle item in the mod) + `StoneTabletItem`
   (`EntityType.updateCustomEntityTag` NBT plumbing).
8. **NOT STARTED** - `CustomizeToDinoFunction`/`ModLootItemFunctionTypes` (needs `MapCodec` rewrite, old GSON
   `Serializer` pattern is gone in 1.21.1).
9. **NOT STARTED** - 4 real advancement triggers (`OpenSarcophagusTrigger`, `ScarabTameTrigger`,
   `ImplantEmbryoTrigger`, `IncubateEggTrigger`) replacing the current no-op `ModTriggers` stub - needs
   `SimpleCriterionTrigger`'s Codec-based `SimpleInstance` rework, not the old manual `JsonObject`/`GsonHelper`
   (de)serialization. `ModTriggers.INCUBATE_EGG_TRIGGER.trigger(player, entity)`'s call-site API (already used by
   `FishEggItem`, written in task 3) must stay stable when this lands for real.
10. **NOT STARTED** - `ArcheologyEnchantment`/`PaleontologyEnchantment` as 1.21.1 data-driven enchantments (datapack
    JSON + exclusive-set tag), not Java `Enchantment` subclasses with `checkCompatibility` overrides (that override
    point no longer exists - enchantments are registry entries now, see drift catalog below).
11. **NOT STARTED** - `ModVillagers`/`ModTrades` (trivial once referenced items exist - do last among "easy" pieces).
12. **NOT STARTED** - `FossilFoodMappings` (simplified per the decision above - depends on task 4's fields already
    being populated, which they are).
13. **NOT STARTED** - Full compile + boot/RCON verification pass + memory update with final Phase 2 completion state.

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
