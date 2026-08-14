---
name: fossils-phase7-task1-recipe-framework
description: "Phase 7 task 1 (recipe framework) COMPLETE as of 2026-08-14: ModRecipes + 5 custom recipe types ported to 1.21.1 codec API, compile+boot verified, recipe-aware GUI slots restored"
metadata:
  type: project
---

Continuation of [[fossils-phase7-recipes-datagen-research]] — task 1 of [[fossils-migration-plan]] Phase 7 is now implemented and verified, per the user's "do task 1 then stop" instruction (2026-08-14). Tasks 2–10 (datagen module, plant_loader) are NOT started — see that file's task list for what's left, and [[feedback-plan-includes-research-stop-before-code]] / [[feedback-stop-after-each-task]] for why this is a deliberate stopping point, not an oversight.

## What was ported

New package `src/main/java/com/github/fossil/recipe/` (12 files): `ModRecipes.java` (registry + programmatic analyzer-recipe generation + fuel-value maps + `getXRecipeForItem`/`isXFuel`/`getXFuelValue` helpers), `MultiOutputAndSlotsRecipe.java`/`Builder.java` (Analyzer/Sifter base), `AnalyzerRecipe.java`/`Builder.java`, `SifterRecipe.java`/`Builder.java`, `WithFuelRecipe.java`/`Builder.java` (CultureVat/Worktable base), `CultureVatRecipe.java`/`Builder.java`, `WorktableRecipe.java`/`Builder.java`.

Modified: `FossilsAndArcheologyRevival.java` (main mod class — now calls `ModRecipes.register(modEventBus)` alongside other mod-bus registrations, and `ModRecipes.initRecipes()` from `FMLCommonSetupEvent`, same timing as `FossilFoodMappings.register()`), and the 5 machine menus (`AnalyzerMenu`, `SifterMenu`, `CultureVatMenu`, `WorktableMenu`, `FeederMenu` under `src/main/java/com/github/fossil/inventory/`) — recipe-aware slot filtering restored, closing the gap [[fossils-phase1-blocks-progress]] deliberately left open pending this.

## Key 1.21.1 API-drift decisions made (reusable knowledge, extends [[fossils-phase7-recipes-datagen-research]]'s research)

- `MultiOutputAndSlotsRecipe` now `implements Recipe<SingleRecipeInput>` (vanilla's simplest `RecipeInput`) instead of `Recipe<Container>`; drops the `ResourceLocation id` field entirely (moved to `RecipeHolder`, per research).
- `WithFuelRecipe` now `implements Recipe<WithFuelRecipe.WithFuelInput>` — `WithFuelInput` is a new `record ... implements RecipeInput` carrying `item`/`fuel`/`anyFuel`, replacing the source's `ContainerWithAnyFuel` (a `SimpleContainer` subclass, which doesn't fit the new `RecipeInput`-based world). Static factories `WithFuelInput.of(item, fuel)` / `WithFuelInput.anyFuel(item)`.
- **Weighted-output map codec** (the one shape with no vanilla codec equivalent): added `MultiOutputAndSlotsRecipe.WeightedOutput` — `record(double weight, ItemStack item)` with its own `Codec`/`StreamCodec` (`ItemStack.OPTIONAL_CODEC`/`OPTIONAL_STREAM_CODEC` so an empty item represents "nothing" outcome). JSON serializes as a `List<WeightedOutput>` of **per-output weight deltas** (human-authorable, e.g. `{"weight": 30, "item": {...}}`), converted to/from the runtime cumulative-weight `TreeMap<Double,ItemStack>` via `Codec.xmap`. The network `StreamCodec` round-trips the cumulative map directly (no delta conversion needed on that path). **Reuse this pattern if any later phase needs another weighted/random-output recipe shape.**
- Datagen builders (`*RecipeBuilder.java`, needed for the later recipe-provider task) were rewritten against the real 1.21.1 `RecipeBuilder`/`RecipeOutput` API — no more `FinishedRecipe`/`serializeRecipeData`; `save()` now constructs the real `Recipe<?>` object directly and calls `RecipeOutput.accept(id, recipe, null)`. Dropped the source's `modId` constructor param (NeoForge-only project, no multi-loader need) — **if writing the Phase-7 recipe provider itself, check these builders' actual constructor signatures before assuming they match source.**

## Deliberately simplified vs. source (documented in Javadoc at each site, not bugs)

1. `MultiOutputAndSlotsRecipe.matches()` tests only the single item in a `SingleRecipeInput`, not a full multi-slot container scan like source. Multi-slot block-entity logic (e.g. `AnalyzerBlockEntity.isAnalyzable`, still stubbed from Phase 1) needs to loop externally calling `ModRecipes.getAnalyzerRecipeForItem` per slot — **flagged as still-needed follow-up work, likely block-entity-logic scope, not yet done.**
2. `MultiOutputAndSlotsRecipe.assemble()` picks weighted output via a static `RandomSource` instead of the container's level-seeded random (1.21.1's `RecipeInput` carries no level/random access).
3. `WithFuelRecipe.assemble()` no longer copies input-stack NBT onto the result — 1.21.1 replaced tags with typed DataComponents and none of this mod's recipes relied on the old NBT-copy behavior.

## Feeder menu confirmation

`FeederMenu` uses `FoodMappings`/`Diet` (not `ModRecipes`) — restored `mayPlace` overrides + shift-click routing via `FoodMappings.getFoodAmount(item, Diet.X) > 0`, mapping meat/plant/fish slots to `Diet.CARNIVORE`/`HERBIVORE`/`PISCIVORE`.

## Verification

`./gradlew compileJava` clean (verified independently by the orchestrating session, not just the porting agent's own claim). `./gradlew runServer` booted to `Done (1.381s)!` with `[minecraft/RecipeManager]: Loaded 1290 recipes`, no recipe/registration errors or warnings.

## What's next

Per [[fossils-migration-plan]] Phase 7 and [[fossils-phase7-recipes-datagen-research]]'s 10-task order: task 2 is the datagen `GatherDataEvent` entry point. **User explicitly said "do task 1 then stop for further instructions" (2026-08-14) — do not start task 2 without being asked, same pattern as the Phase 1→2 boundary.**
