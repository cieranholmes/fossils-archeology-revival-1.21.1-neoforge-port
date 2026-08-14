---
name: fossils-phase7-task3-blockstate-item-providers
description: "Phase 7 task 3 (block state/model + item model datagen providers) COMPLETE as of 2026-08-14: ModBlockStateProvider/ModItemProvider/ModBlockModelProvider ported to 1.21.1 NeoForge, compile+runData+boot verified"
metadata:
  type: project
---

Continuation of [[fossils-phase7-task2-datagen-entry-point]] — task 3 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was ported

New package `src/main/java/com/github/fossil/data/providers/` (3 files): `ModBlockStateProvider.java` (505 lines), `ModItemProvider.java` (310 lines), `ModBlockModelProvider.java` (44 lines) — mechanical port of the source repo's same-named classes under `forge/.../forge/data/providers/`. Modified `src/main/java/com/github/fossil/data/DataGenerators.java`: `gatherData(GatherDataEvent)` now registers these two top-level providers via `generator.addProvider(event.includeClient(), (DataProvider.Factory<DataProvider>) output -> new ModBlockStateProvider(output, efh))` and the same for `ModItemProvider`, matching source's two-provider wiring but through this project's explicit `IEventBus`-listener style (no `@Mod.EventBusSubscriber`/`@SubscribeEvent`, consistent with [[fossils-phase7-task2-datagen-entry-point]]'s established convention).

## Key 1.21.1/NeoForge API-drift decisions (extends [[fossils-phase7-recipes-datagen-research]])

- `net.minecraftforge.client.model.generators.*` (BlockStateProvider/ItemModelProvider/BlockModelProvider/ModelProvider/ConfiguredModel/ModelFile/ItemModelBuilder/BlockModelBuilder) → `net.neoforged.neoforge.client.model.generators.*`, a near-1:1 package rename with **identical method signatures**, confirmed by reading the real NeoForge 21.1.248 sources jar directly (not guessed). `ExistingFileHelper` moved to `net.neoforged.neoforge.common.data.ExistingFileHelper`. Constructors (`BlockStateProvider(PackOutput, String modid, ExistingFileHelper)` etc.) unchanged.
- `ForgeRegistries.BLOCKS.getKey(...)`/`ForgeRegistries.ITEMS.getKey(...)` → `BuiltInRegistries.BLOCK.getKey(...)`/`BuiltInRegistries.ITEM.getKey(...)` (already this project's established convention, e.g. `recipe/AnalyzerRecipeBuilder.java`).
- `new ResourceLocation(ns, path)` (source) → `ResourceLocation.fromNamespaceAndPath(ns, path)`; bare `new ResourceLocation("modid:path")` → `ResourceLocation.withDefaultNamespace(...)`/`ResourceLocation.parse(...)` as appropriate — 1.21.1 has no public `ResourceLocation` constructor.
- Architectury's `RegistrySupplier<T>` → NeoForge's `DeferredBlock<T>`/`DeferredItem<T>` — same `.get()` accessor, ported mechanically unchanged everywhere.
- **Real signature addition, not just a rename**: NeoForge 1.21.1's `ItemModelProvider` base class now declares its own `spawnEggItem(Item)`/`spawnEggItem(ResourceLocation)` returning `ItemModelBuilder` (Forge 1.20.1's didn't have this method at all). Source's custom `void`-returning `spawnEggItem` override no longer compiles as an override — dropped the override entirely; all call sites now bind to the inherited method, behaviorally identical.
- Source's `ModBlocks.BLOCKS.forEach(supplier -> supplier.ifPresent(...))` (Architectury `RegistrySupplier` list idiom) → `for (var entry : ModBlocks.BLOCKS.getEntries()) { registerExistingTextures(entry.get()); }`, using NeoForge's `DeferredRegister.Blocks.getEntries()` (confirmed present via sources jar) — **reusable pattern if any later provider needs to bulk-iterate a `DeferredRegister`.**

## Deviations forced by target-repo drift (not stylistic — verified independently, not just agent-claimed)

1. **Ferns block-state/model registration skipped** — source generates states for `ModBlocks.FERNS`/`FernsBlock`, but target's `ModBlocks.java` has no `FERNS` field registered yet (`FernsBlock` class exists, unused). Left as a code comment for whenever Blocks catches up.
2. **`HOME_PORTAL` and `OBSIDIAN_SPIKES` block-state calls skipped** — neither field exists in target `ModBlocks.java` (confirmed via grep — zero matches). Left as comments.
3. **`ModItems.PlANT_FOSSIL` (source has a typo'd field name)** → target's correctly-spelled `ModItems.PLANT_FOSSIL` (confirmed present, registered as `"fossil_plant"`).

## Verification

`./gradlew compileJava` clean (verified independently, not just the porting agent's claim). `./gradlew runData` — `BUILD SUCCESSFUL`, log showed `Block States: fossil finished after 163 ms` and `Item Models: fossil finished after 122 ms` with zero exceptions, confirming `registerStatesAndModels()`/`registerModels()` actually execute end-to-end against real target-repo block/item registries (not just compile-clean). `./gradlew runServer` booted clean afterward. The `src/generated/` datagen output from the `runData` run was deleted afterward since it's a build artifact, not a task deliverable — git status is clean of it.

## What's next

Task 4 per the 10-task order: item model + tag providers is actually already partially covered by task 3's `ModItemProvider` — re-check [[fossils-phase7-recipes-datagen-research]]'s task list; next unstarted work is likely block/item **tag** providers (`ModBlockTagsProvider`/`ModItemTagsProvider`/`ModEntityTypeTagsProvider`), then loot providers, recipe provider, advancement provider, food value provider, worldgen provider, and finally `plant_loader`. **User's "do task N then stop" pattern applies again per [[feedback-stop-after-each-task]] — do not start the next task without being asked.**
