---
name: fossils-phase7-task7-advancement-provider
description: "Phase 7 task 7 (advancement datagen provider, FossilAdvancements) COMPLETE as of 2026-08-14: ported to NeoForge's AdvancementProvider/AdvancementGenerator + AdvancementHolder API, compile+runData+runServer all verified with exact +27 advancement count delta"
metadata: 
  node_type: memory
  type: project
  originSessionId: 8a58d673-a358-4758-89a0-5239f58d76f8
  modified: 2026-08-14T10:14:51.626Z
---

Continuation of [[fossils-phase7-task6-recipe-provider]] - task 7 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was ported

New `src/main/java/com/github/fossil/data/providers/FossilAdvancements.java` - mechanical port of the source repo's `forge/.../forge/data/advancements/FossilAdvancements.java`. Covers the full fossil advancement tree (root → break_fossil → figurines/anu-lair/anubite/sarcophagus/kill_anu/ancient_clock, scarab_gem/scarab_tame, stone_tablet, tar_drop/tar_fossil, broken_sword→worktable→ancient_sword, dna→culture_vat→failuresaurus/embryos/dino_eggs→dinopedia, plus the two "all_eggs"/"all_embryos" challenge advancements looping every `PrehistoricEntityInfo`/`VanillaEntityInfo` species). Wired into `DataGenerators.java` via `new AdvancementProvider(generator.getPackOutput(), lookup, efh, List.of(new FossilAdvancements()))`.

## Key 1.21.1 API-drift decisions

- Implements `net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator` (not vanilla's deprecated `AdvancementProvider`), per [[fossils-phase7-recipes-datagen-research]] section 2 - confirmed by reading the NeoForge sources jar. `generate(HolderLookup.Provider, Consumer<AdvancementHolder>, ExistingFileHelper)` replaces the source's `Consumer<Consumer<Advancement>>` shape entirely.
- `Advancement.Builder.save(...)` now returns `AdvancementHolder` (not `Advancement`), and `.parent(...)` takes an `AdvancementHolder` overload directly - confirmed via decompiled `Advancement.java`. Used the NeoForge-added `IAdvancementBuilderExtension#save(Consumer<AdvancementHolder>, ResourceLocation, ExistingFileHelper)` overload (not vanilla's bare `save(consumer, String)`) so the `ExistingFileHelper` validates parent-before-child save ordering and tracks generated files, matching the pattern NeoForge's own javadoc recommends.
- `FrameType` renamed to `AdvancementType` (enum values TASK/CHALLENGE/GOAL unchanged) - a real vanilla rename, confirmed via decompiled source, not previously documented in this project's drift catalog.
- All the mod's own custom `SimpleCriterionTrigger`s (`ImplantEmbryoTrigger`/`IncubateEggTrigger`/`OpenSarcophagusTrigger`/`ScarabTameTrigger`) were already ported in an earlier phase with the correct `Criterion<TriggerInstance>`-returning codec API - **but two of their static factory method names were renamed from the source during that earlier port**: `OpenSarcophagusTrigger.TriggerInstance.useScarab()` → `.openSarcophagus()`, and `ScarabTameTrigger.TriggerInstance.useScarab()` → `.tameWithScarab()`. Not a 1.21.1 API-drift issue - just a naming choice made whenever those trigger classes were ported; call sites here use the current names.
- `PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(...)` in 1.21.1 has no overload taking a bare `EntityPredicate`/`.Builder` for the entity param - only `Optional<EntityPredicate>`. Source passed `EntityPredicate.Builder.entity().of(...).build()` directly; ported as `playerHurtEntity(Optional.of(EntityPredicate.Builder.entity().of(ModEntities.FAILURESAURUS.get()).build()))`. `KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder)` by contrast is unchanged and takes the Builder directly - confirmed both signatures by reading the decompiled criterion classes rather than assuming a uniform pattern.
- Per the now-established finding (first surfaced in [[fossils-phase7-task4-tags-providers]], reused again in [[fossils-phase7-task6-recipe-provider]]): NeoForge's `DeferredBlock`/`DeferredItem` implement `ItemLike` directly, so the source's separate `RegistrySupplier<? extends ItemLike>...` varargs overloads of the `simple(...)` helper were dropped entirely (would have been duplicate erasure against the `ItemLike...` overloads anyway) - callers just pass `.get()` results as plain `ItemLike` varargs.
- `ForgeRegistries.ITEMS.getKey(item)` / `ForgeRegistries.ENTITY_TYPES.getKey(entityType)` → `BuiltInRegistries.ITEM.getKey(item)` / `BuiltInRegistries.ENTITY_TYPE.getKey(entityType)` (mechanical, same pattern already used in [[fossils-phase7-task6-recipe-provider]]).

## Deviation forced by target-repo drift (not stylistic - verified independently)

`restoredSeed` advancement (`tag(FERN_SEED, RESTORED_SEEDS, fossilSeed, consumer)`, chained off `fossilSeed`) dropped entirely - same root gap as [[fossils-phase7-task4-tags-providers]] finding #2 and [[fossils-phase7-task6-recipe-provider]] deviation #1: `FERN_SEED` needs `ModBlocks.FERNS` (`FernsBlock`), which isn't ported yet (Phase 1/Blocks catch-up scope). `FERN_SEED_FOSSIL` itself (the `fossilSeed` advancement, output of analyzing `PLANT_FOSSIL`) is unaffected.

## Verification

`./gradlew compileJava` clean on first try - no compile errors at all (unusual for this phase's tasks; every API-drift point above was pre-confirmed via decompiled-source reads before writing the file, per this session's research-first approach). `./gradlew runData` - `BUILD SUCCESSFUL`, `Advancements finished after 30 ms`, 27 new JSON files generated under `data/fossil/advancement/fossil/*.json` (folder is singular `advancement`, same 1.21.1 datapack-format convention already seen for `recipe`/other providers). Spot-checked `root.json` (background texture path, `sends_telemetry_event: true` from `Advancement.Builder.advancement()`'s default, single crafting-table criterion), `all_embryos.json` (26 `implant_embryo` criteria across every species with an `embryoItem`, correct default AND `requirements` nesting, `experience: 500` reward), and `scarab_tame.json` (custom-trigger criterion, correct parent chain to `scarab_gem`).

**Real `runServer` boot-count verification**: booted twice per the established convention from [[fossils-phase7-task6-recipe-provider]] - baseline with `src/generated` entirely absent (1399 advancements, matching the exact figure recorded in that memory) vs. with only the generated `data/fossil/advancement/` folder present (1854 advancements). The delta (455) is *not* 27 alone - it also includes 428 pre-existing recipe-unlock advancement JSONs that vanilla's `RecipeOutput.accept(id, recipe, advancement)` already writes into the same `data/fossil/advancement/recipes/` subtree (a side effect of the recipe provider from task 6, not new in this task). Confirmed the math explicitly: 1399 + 428 + 27 = 1854 exactly. One incidental finding: the same boot logged `TagLoader` errors for several vanilla tags (`logs`, `logs_that_burn`, etc.) because the generated `tags/` output was deliberately excluded from this run (only `advancement/` was restored, per the "avoid asset/data collisions with hand-authored files" convention) - this is an artifact of the verification method itself, not a real bug; a real boot always has both the tags provider's output and the hand-authored data present together. `src/generated/` deleted after the check.

## What's next

Task 8 per the 10-task order: food value provider, then worldgen provider, then finally `plant_loader`. **User's "do task N then stop" pattern applies again per [[feedback-stop-after-each-task]] - do not start task 8 without being asked.**
