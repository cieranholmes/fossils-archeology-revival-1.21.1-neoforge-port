---
name: fossils-phase7-task6-recipe-provider
description: "Phase 7 task 6 (recipe datagen provider, ModRecipeProvider) COMPLETE as of 2026-08-14: all crafting/cooking/machine JSON recipes ported to 1.21.1 RecipeOutput/CompletableFuture<HolderLookup.Provider> API, compile+runData+runServer all verified with exact recipe/advancement count deltas"
metadata: 
  node_type: memory
  type: project
  originSessionId: c00f5706-6e23-4427-9770-7bcf02c2d1bd
  modified: 2026-08-14T09:57:28.038Z
---

Continuation of [[fossils-phase7-task5-loot-providers]] - task 6 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was ported

New `src/main/java/com/github/fossil/data/providers/ModRecipeProvider.java` - mechanical port of the source repo's `forge/.../forge/data/providers/ModRecipeProvider.java`. Covers all vanilla-style crafting/cooking recipes (toys, tools, armor, vases, figurines, wood/stone building-block families, stonecutting) plus every machine recipe (Analyzer/Sifter/Worktable/CultureVat) using the [[fossils-phase7-task1-recipe-framework]] builders. Wired into `DataGenerators.java` alongside the existing providers. Explicitly excluded per [[fossils-phase7-recipes-datagen-research]] scoping: `FossilCreateRecipeProvider`/`FossilFarmersRecipeProvider` (Create-mod compat, Phase 9 scope) and `PFaunaTagsProvider`-style compat blocks.

## Key 1.21.1 API-drift decisions

- `RecipeProvider` constructor now takes `(PackOutput, CompletableFuture<HolderLookup.Provider>)` - threaded through to `super(...)`, otherwise unused directly since `buildRecipes(RecipeOutput)` (not the 2-arg `buildRecipes(RecipeOutput, HolderLookup.Provider)` overload) is all this provider needs.
- `generateRecipes(RecipeOutput, BlockFamily)` gained a trailing `FeatureFlagSet` param - passed `FeatureFlagSet.of(FeatureFlags.VANILLA)` for every call (confirmed by reading vanilla's own `VanillaRecipeProvider.buildRecipes` calling `generateForEnabledBlockFamilies(output, FeatureFlagSet.of(FeatureFlags.VANILLA))`), since none of this mod's custom block families are gated behind an experimental flag.
- `Items.SCUTE` renamed to `Items.TURTLE_SCUTE` in 1.21.1 (armadillo scutes were added alongside it) - a real vanilla rename caught only at compile time, exactly like [[fossils-phase7-task4-tags-providers]]'s `Blocks.GRASS`→`SHORT_GRASS` finding. Also swapped the same `Blocks.GRASS`→`Blocks.SHORT_GRASS` rename into this file's plant-fossil analyzer output list (missed by source, present here).
- **`RegistrySupplier<? extends ItemLike>` overloads on `worktable`/`cultureVat` dropped entirely** - confirmed (again, per [[fossils-phase7-task4-tags-providers]]'s finding) that NeoForge's `DeferredBlock`/`DeferredItem` both implement `ItemLike` directly, so a single `ItemLike`-based helper method covers every call site (mixed `DeferredBlock<VaseBlock>` and `DeferredItem<Item>` call sites) with no `.get()` needed and no overload-ambiguity risk (only one overload shape exists, unlike the tags-provider's `addTag` gotcha).
- `Consumer<FinishedRecipe> consumer` → `RecipeOutput recipeOutput` throughout (mechanical, per [[fossils-phase7-recipes-datagen-research]]'s already-established finding); `new ResourceLocation("minecraft:...")` → `ResourceLocation.parse("minecraft:...")` for the two `BuiltInRegistries.BLOCK.getOptional(...)` toy-item lookups.
- Confirmed via direct read of the decompiled `RecipeProvider.java`/`RecipeBuilder.java`/`SimpleCookingRecipeBuilder.java`/`SingleItemRecipeBuilder.java`/`ShapedRecipeBuilder.java`/`ShapelessRecipeBuilder.java`/`SmithingTransformRecipeBuilder.java` in `~/.gradle/caches/neoformruntime` that every other builder call shape (static factories, `.save(RecipeOutput, ResourceLocation|String)`, `unlockedBy`/`unlocks`, `RecipeCategory` params) is byte-for-byte unchanged from 1.20.1 aside from the `Consumer<FinishedRecipe>`→`RecipeOutput` swap - no other signature drift found in this file.

## Deviations forced by target-repo drift (not stylistic - verified independently)

1. **`FERN_SEED` culture-vat recipe dropped** - source has `cultureVat(ModItems.FERN_SEED_FOSSIL, ModItems.FERN_SEED, consumer)`; confirmed via source read that `FERN_SEED` is `ITEMS.register("fern_seed", () -> new ItemNameBlockItem(ModBlocks.FERNS.get(), ...))`, i.e. a genuine `BlockItem` for a `FernsBlock` that doesn't exist in this repo yet (same root gap already flagged in [[fossils-phase7-task4-tags-providers]] finding #2 for the item-tags provider). Left as an inline comment citing both memories. `FERN_SEED_FOSSIL` itself (the plain fossil item, output of analyzing `PLANT_FOSSIL`) is unaffected and still wired normally.
2. Source's `PlANT_FOSSIL` (typo'd field name) is `PLANT_FOSSIL` in this repo (already fixed at the [[fossils-phase2-items-progress]] port) - just a naming fix, not a scope drop.

## Verification

`./gradlew compileJava` clean (one real compile error hit and fixed: the `TURTLE_SCUTE` rename above). `./gradlew runData` - `BUILD SUCCESSFUL`, `Recipes finished after ~109-309 ms`, 617 recipe JSON files generated (36 analyzer, 30 worktable, 121 culture_vat, 2 sifter, 428 top-level crafting/cooking/stonecutting) + 428 auto-generated recipe-unlock advancement JSONs. Spot-checked generated JSON content directly: `analyzer/fossil_plant.json` correctly serializes the full 19-plant weighted-output list including the computed even-split seed weights; `worktable/ancient_helmet_with_relic_scrap.json` correctly shapes fuel/input/result/duration; `sifter/sifter_inputs.json` correctly includes the `nothing()` empty-item weighted entry.

**Real `runServer` boot-count verification** (not just `runData`): booted a real dedicated server twice, once with `src/generated` absent (baseline) and once with only the `recipe`/`advancement` folders of a fresh `runData` output present (the other providers' generated output - blockstates/models/tags/loot/enchantment JSON - collide with hand-authored `src/main/resources` files by design, per [[fossils-phase7-task5-loot-providers]]'s "src/generated deleted after the check" convention, so they can't all coexist with a plain `runServer` boot at once). Recipe count went from baseline 1290 → 1907 with this provider's output present, an exact +617 matching the generated file count; advancement count went from 1399 → 1827, an exact +428 matching the generated recipe-advancement count. Only pre-existing `RegisterSpawnPlacementsEvent` warning appeared (documented in earlier phase memories, unrelated). `src/generated/` deleted after the check, per established convention.

**Process-hygiene note for future `runServer` verification loops**: a background `runServer` invocation's underlying Java process keeps running (RCON etc.) even after the *gradle* command returns/the background-task notification fires - it does not self-terminate. A second `runServer` attempt while the first is still alive fails with `DirectoryLock$LockException: session.lock: already locked`. Find and `kill` the stale PID (`ps aux | grep devlaunch.Main`) before retrying, and always kill the process explicitly once verification is done rather than assuming the backgrounded gradle task's completion means the server stopped.

## What's next

Task 7 per the 10-task order: advancement provider (`FossilAdvancements`, using NeoForge's `AdvancementProvider`/`AdvancementGenerator`, not vanilla's deprecated one - see [[fossils-phase7-recipes-datagen-research]] section 2), then food value provider, worldgen provider, and finally `plant_loader`. **User's "do task N then stop" pattern applies again per [[feedback-stop-after-each-task]] - do not start task 7 without being asked.**
