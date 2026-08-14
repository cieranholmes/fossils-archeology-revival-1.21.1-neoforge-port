---
name: fossils-phase7-task8-food-value-provider
description: "Phase 7 task 8 (food value datagen provider, FoodValueProvider/ModFoodValueProvider) COMPLETE as of 2026-08-14: hand-rolled DataProvider ported almost unchanged, compile+runData verified with spot-checked JSON content; no runServer count-check done since nothing in this port currently consumes the JSON at runtime"
metadata: 
  node_type: memory
  type: project
  originSessionId: 8a58d673-a358-4758-89a0-5239f58d76f8
  modified: 2026-08-14T10:24:34.894Z
---

Continuation of [[fossils-phase7-task7-advancement-provider]] - task 8 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was ported

Two new files, mechanical ports of the source repo's `common/.../food/FoodValueProvider.java` (abstract base, unchanged package `com.github.fossil.food`) and `forge/.../forge/data/providers/ModFoodValueProvider.java` (concrete data, moved to `com.github.fossil.data.providers` alongside the other Phase 7 providers). This is a hand-rolled `DataProvider` (not registry-ops-based) that writes 4 raw JSON files (`data/fossil/food/{egg,fish,meat,plant}.json`) listing item/block/entity food values with per-`FoodType` multipliers - it does NOT use `HolderLookup.Provider`/`CompletableFuture` at all, so it was almost entirely unaffected by the registry-ops threading that drove most of this phase's other API drift. Wired into `DataGenerators.java` via `generator.addProvider(event.includeServer(), (DataProvider.Factory<DataProvider>) ModFoodValueProvider::new)`.

## Key 1.21.1 API-drift decisions

- `DataProvider`/`PackOutput`/`CachedOutput`/`DataProvider.saveStable(CachedOutput, JsonElement, Path)` are all byte-for-byte unchanged from 1.20.1 - confirmed via decompiled source read. The abstract `FoodValueProvider` base needed literally zero API changes, only the package/MOD_ID rename (`FossilMod.MOD_ID` → `FossilsAndArcheologyRevival.MODID`).
- `FoodProperties.getNutrition()` → `.nutrition()`: `FoodProperties` became a record in 1.21.1 (confirmed via decompiled source), so all 13 `Foods.X.getNutrition()` call sites in `ModFoodValueProvider` needed the record-accessor rename. Not previously documented in this project's drift catalog - a new finding.
- `new ResourceLocation(String)` / `new ResourceLocation(String namespace, String path)` → `ResourceLocation.parse(String)` / `ResourceLocation.fromNamespaceAndPath(namespace, path)` (constructor is now private, confirmed via decompiled source) - same established pattern as every prior Phase 7 task, but this file had an unusually large number of call sites (~50, one per compat-mod entity) since the source hardcodes food values for entities from mods this project doesn't even depend on (rats, bewitchment, betteranimalsplus, totemic, quark, exoticbirds, twilightforest, naturalist) purely as `ResourceLocation` string data with no compile-time dependency on those mods' classes - ported as-is, unaffected by whether those mods are actually present at runtime.
- `Blocks.GRASS` → `Blocks.SHORT_GRASS`, same established 1.21.1 rename as [[fossils-phase7-task4-tags-providers]]/[[fossils-phase7-task6-recipe-provider]].

## Deviation forced by target-repo drift (not stylistic - verified independently)

`plant.block(ModBlocks.FERNS.get(), 3)` and `plant.item(ModItems.FERN_SEED.get(), 5)` dropped - same root gap as [[fossils-phase7-task4-tags-providers]] finding #2, [[fossils-phase7-task6-recipe-provider]] deviation #1, and [[fossils-phase7-task7-advancement-provider]]'s dropped `restoredSeed` advancement: `ModBlocks.FERNS`/`ModItems.FERN_SEED` need `FernsBlock`, which isn't ported yet (Phase 1/Blocks catch-up scope). This is now a 4-for-4 pattern across every Phase 7 task that touches fern-seed content.

## Verification

`./gradlew compileJava` - one real compile error (the `getNutrition()`→`nutrition()` rename, 13 call sites, fixed via `sed`), clean after that. `./gradlew runData` - `BUILD SUCCESSFUL`, `Fossil Food Values finished after 4 ms`, exactly the 4 expected JSON files generated (none skipped - every `FoodType` has both items and entities populated). Spot-checked all 4 files' content directly: `egg.json` (single `minecraft:egg` item, value 7, unmultiplied), `plant.json` (all 21 `PrehistoricPlantInfo` blocks + 24 vanilla blocks/tags/items present, values correctly `nutrition-formula * FoodType.PLANT.multiplier()` for blocks e.g. `carrots`: `(3*5)*5=75`, `crataegus`: `(2*3+1)*5=35` - and correctly *not* multiplied for items, matching the source code's `block()`/`item()` asymmetry), `meat.json` (87 entities across 9 namespaces correctly alphabetically sorted by namespace-then-path, `fossil:failuresaurus_flesh` item present at value 15 unmultiplied). No `fossil:fern_seed`/`fossil:ferns` entries anywhere, confirming the intentional drop.

**No `runServer` boot-count verification done for this task** (unlike tasks 6/7) - read `FoodMappings.java` (the runtime consumer this data is conceptually for) and found its own docstring: *"Simplified in-memory replacement for the source mod's JSON+tag-driven food value system... drops JSON loading, tag support and network sync"* - i.e. an earlier phase (Phase 2 Items) deliberately hardcoded food values in-memory instead of loading this JSON, so there is no boot-time reload listener that consumes `data/fossil/food/*.json` in this port yet, and thus no count to check. This is a known, pre-existing simplification from an earlier phase, not a gap introduced by this task - the datagen output is correct and matches the source's data shape for whenever/if that gets revisited.

## What's next

Task 9 per the 10-task order: worldgen provider, then finally `plant_loader`. **User's "do task N then stop" pattern applies again per [[feedback-stop-after-each-task]] - do not start task 9 without being asked.**
