---
name: fossils-phase7-tasks3to8-backfill
description: "COMPLETE as of 2026-08-14: backfilled tasks 3-8's datagen output (block states/item models/tags/loot/recipes/advancements/food values) into src/main/resources, since it had only ever been verified-then-deleted, never actually committed"
metadata: 
  node_type: memory
  type: project
  originSessionId: 5a1274e9-916b-41f4-89dd-97a9cc4087b3
  modified: 2026-08-14T12:50:04.613Z
---

Fixes the process gap discovered during [[fossils-phase7-task9-progress]]: Phase 7 tasks 3-8 ([[fossils-phase7-task3-blockstate-item-providers]] through [[fossils-phase7-task8-food-value-provider]]) each verified their datagen provider by running `runData`, checking the output, then **deleting `src/generated/`** — treating it as a build artifact rather than a deliverable. Since nothing in `build.gradle` runs `runData` automatically, this meant almost none of that generated content ever actually shipped; only 21 blockstate + 30 item-model files (Phase 1 stopgap hand-authoring) were ever committed.

## What was done

1. `rm -rf src/generated && ./gradlew runData` — clean run, all 9 providers succeeded (Registries, Block States, Item Models, Fossil Block Tags, item tags, entity_type tags, Loot Tables, Recipes, Advancements, Fossil Food Values), 3441 files generated, zero exceptions.
2. Diffed the 21/30 existing hand-authored blockstate/item-model files against the generated set first, to check for regressions: only `tar.json` blockstate had a generated counterpart (byte-identical). The other 20 blockstates + 29 item models (`analyzer`, `sifter`, `feeder`, `worktable`, `comfy_bed`, `culture_vat`, etc.) are custom block-entity/GUI-driven blocks the datagen providers deliberately don't cover — confirmed these are a **separate, still-open gap** (the "~197 blocks need Phase 7 datagen models" / "23 models need a Phase 5 plant_loader" notes from [[fossils-phase1-blocks-progress]]), not something this backfill regresses or fixes.
3. Non-destructive merge: `cp -r src/generated/resources/. src/main/resources/` — this only adds new files and overwrites files with matching names (safe, since the only overlap was the identical `tar.json`); it cannot delete anything, so the hand-authored special-block files were verified untouched afterward.
4. `rm -rf src/generated` (now safe to discard — its content is committed at its real home in `src/main/resources`).
5. Verified: `compileJava` clean; fresh `runServer` boot via RCON — **zero exceptions**, and the log confirms real functional pickup: `Loaded 1907 recipes` (up from the pre-backfill 1290 baseline — exactly the +617 recipe JSON files task 6 generates) and `Loaded 1854 advancements` (task 6 + task 7's 428+27=455 delta, consistent with their documented counts). No new missing-texture or other warnings beyond pre-existing unrelated ones (mixin refmap notices, command-ambiguity notices, offline-mode notice — all predate this work).

## Final file counts added (3398 total, non-`.claude/memory` files)

- `assets/fossil/models/item/`: 1174
- `data/fossil/advancement/` (incl. `recipes/` subfolder): 455
- `assets/fossil/models/block/`: 399
- `data/fossil/loot_table/` (`blocks/` + `entities/`): 316
- `data/fossil/recipe/` (incl. machine-specific subfolders `culture_vat/`, `analyzer/`, `worktable/`, `sifter/`): 617
- `assets/fossil/blockstates/`: 228 (new; the 21 pre-existing hand-authored ones untouched except `tar.json`, a no-op overwrite)
- Tags: 52 fossil item tags, 29 vanilla `minecraft:` block-tag overrides, 24 vanilla `minecraft:` item-tag overrides, 12 fossil block tags, 8 `c:` (common) item tags, 2 fossil entity_type tags, 1 vanilla entity_type override, 1 `c:` block tag.

## What's NOT done / still open

- The ~197-blocks-need-models / 23-models-need-plant_loader gap from Phase 1 is unchanged by this backfill — those blocks were never in scope for `ModBlockStateProvider`/`ModItemProvider` to begin with (custom multi-part models or plant-geometry-loader-dependent), and remain task 10 (`plant_loader`) territory per [[fossils-phase7-recipes-datagen-research]].
- These 3398 files were committed in `d4b6dab` ("phase 7: worldgen datagen rewrite + backfill generated resources") alongside task 9's worldgen work.
- Visual verification (do the new block/item models actually render correctly) turned out to matter: [[fossils-phase7-task10-plant-loader]]'s live `runClient` testing (this same machine DOES have a real display, contrary to what this memory originally assumed) found 3 real rendering bugs downstream of this and task 9/10's work — missing particle assets, missing block render-type registrations, and a UV-normalization bug in the plant_loader baking code. None of those were caused by this backfill specifically, but the general lesson holds: compile+boot+count checks don't prove correct rendering, only actual client testing does.
