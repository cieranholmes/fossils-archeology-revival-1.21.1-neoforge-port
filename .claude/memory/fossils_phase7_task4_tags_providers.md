---
name: fossils-phase7-task4-tags-providers
description: "Phase 7 task 4 (block/item/entity-type tags datagen providers) COMPLETE as of 2026-08-14: ModBlockTagsProvider/ModItemTagsProvider/ModEntityTypeTagsProvider ported to 1.21.1 NeoForge, compile+runData+boot all verified"
metadata:
  type: project
---

Continuation of [[fossils-phase7-task3-blockstate-item-providers]] — task 4 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was ported

New files in `src/main/java/com/github/fossil/data/providers/`: `ModBlockTagsProvider.java`, `ModItemTagsProvider.java`, `ModEntityTypeTagsProvider.java` — mechanical port of the source repo's same-named classes under `forge/.../forge/data/providers/`. Modified `src/main/java/com/github/fossil/data/DataGenerators.java` to register all three as server-side providers (`event.includeServer()`), following the same explicit `IEventBus`-listener style established in [[fossils-phase7-task2-datagen-entry-point]]. Also added a missing `ModBlockTags.C_GLASS_BLOCKS` field to `src/main/java/com/github/fossil/tags/ModBlockTags.java` — the item-tags provider's `copy(ModBlockTags.C_GLASS_BLOCKS, ModItemTags.C_GLASS_BLOCKS)` needed it and only the item-side tag existed yet in this repo.

## Key 1.21.1/NeoForge API-drift decisions (extends [[fossils-phase7-recipes-datagen-research]])

- `net.minecraftforge.common.data.BlockTagsProvider`/vanilla `ItemTagsProvider`/`EntityTypeTagsProvider` constructors are unchanged in shape from source (mod-id + `ExistingFileHelper` overloads) — only `ExistingFileHelper`'s package moved to `net.neoforged.neoforge.common.data`, confirmed by reading the real NeoForge 21.1.248 / vanilla sources directly.
- `Blocks.GRASS` (source, 1.20.1) was renamed to `Blocks.SHORT_GRASS` in 1.21.1 (the "tall grass" replacement was already `Blocks.TALL_GRASS`, unaffected) — a real vanilla rename caught only at compile time, not by static analysis of the source file.
- Architectury's `RegistrySupplier<T>` → NeoForge's `DeferredBlock<T>`/`DeferredItem<T>`, both of which **implement `ItemLike` directly** (confirmed by reading NeoForge's `DeferredBlock`/`DeferredItem` sources — both override `asItem()` to resolve via `.get().asItem()`/`.get()`). This means the item-tags provider needs **no separate `RegistrySupplier`-style overload** the way source's Architectury version did — a single `addTag(TagKey<Item>, ItemLike... toAdd)` overload transparently accepts `DeferredBlock`/`DeferredItem` instances mixed with plain vanilla `ItemLike`s (e.g. `Items.EGG`) in the same varargs call. Adding a redundant `DeferredHolder<?, ? extends ItemLike>` overload alongside the `ItemLike...` one causes "reference to addTag is ambiguous" compile errors for every call site, since both overloads become simultaneously applicable — **do not add that extra overload if this pattern recurs elsewhere.**
- The block-tags provider still needs one `DeferredBlock<? extends Block>... toAdd` overload distinct from `Block... toAdd`, since `DeferredBlock` itself is not a `Block` — no ambiguity there since `Block` and `DeferredBlock` are unrelated types.

## Deviations forced by target-repo drift (not stylistic — verified independently, not just agent-claimed)

1. **`HOME_PORTAL`/`OBSIDIAN_SPIKES` block tags skipped** — neither exists in target `ModBlocks.java` yet (confirmed in [[fossils-phase7-task3-blockstate-item-providers]]). Dropped from `BlockTags.PORTALS`, `BlockTags.NEEDS_DIAMOND_TOOL`, and `BlockTags.MINEABLE_WITH_PICKAXE`'s lists; left as inline comments referencing the task-3 memory file.
2. **`FERN_SEED` item tag skipped** — `ModItems.java` has `FERN_SEED_FOSSIL` but not the restored `FERN_SEED` item (needs `ModBlocks.FERNS`/`FernsBlock`, not ported — see task 3's memory). `ModItemTags.RESTORED_SEEDS` tag is still created (empty save for the loop that adds `PrehistoricPlantInfo.plantsWithSeeds()` entries) but the explicit `FERN_SEED` add is dropped.
3. **Block-family stonecutting-variant loop skipped in `FILTER_TREES`** — source additionally tags each wood type's `BlockFamily.Variant` planks sourced from `ModRecipeProvider` (the *recipe datagen provider*, not the recipe framework from task 1 — that's a separate later task, likely task 6/7 in the 10-task order). Since that class doesn't exist in this repo yet, the whole `for (BlockFamily.Variant variant : ...)` loop is deferred with an inline comment; the non-family part of `FILTER_TREES` (the tree-part tag copies) is otherwise complete.
4. **`PFaunaTagsProvider`/`ModList.get().isLoaded(ModConstants.PREHISTORIC_FAUNA)` compat-mod blocks dropped entirely** — per [[fossils-phase7-recipes-datagen-research]]'s scoping, Prehistoric Fauna compat integration is explicitly Phase 9 scope, not Phase 7. All three source providers had one `if (ModList.get().isLoaded(...))` block each; none are ported here.

## Verification

`./gradlew compileJava` clean (the ambiguous-overload and `Blocks.GRASS`-rename errors above were both caught and fixed here, not left for a later check). `./gradlew runData` — `BUILD SUCCESSFUL`, log showed all 5 providers finish including `Fossil Block Tags`, `Tags for minecraft:item mod id fossil`, `Tags for minecraft:entity_type mod id fossil` with zero exceptions. Spot-checked generated JSON content directly (not just "it ran"): `fossil:dna` item tag correctly lists all 61 `PrehistoricEntityInfo` DNA items plus all mammal/fish/bird `VanillaEntityInfo` DNA items; `fossil:mammal` entity-type tag correctly lists only the mammal-typed vanilla mobs; `c:glass_blocks` correctly generated on **both** the block side and the item side (via the block→item `copy()`); `fossil:filter_park` correctly includes all 16 dye-colored toy balls and all wood-typed scratching posts/tethered logs. `./gradlew runServer` booted to `Done (1.246s)!` afterward with `Loaded 1290 recipes` and no new errors (the pre-existing `RegisterSpawnPlacementsEvent` warning is unrelated, noted in earlier phase memories). The `src/generated/` datagen output was deleted afterward since it's a build artifact, not a task deliverable — git status is clean of it.

## What's next

Task 5 per the 10-task order: loot providers (`ModBlockLootTables`/`ModEntityLootTables`/`ModGenericLootTables` implementing `LootTableSubProvider`, wired through vanilla's `LootTableProvider`/`SubProviderEntry`). **User's "do task N then stop" pattern applies again per [[feedback-stop-after-each-task]] — do not start task 5 without being asked.**
