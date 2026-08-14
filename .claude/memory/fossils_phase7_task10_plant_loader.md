---
name: fossils-phase7-task10-plant-loader
description: "Phase 7 task 10 (plant_loader) COMPLETE as of 2026-08-14: PlantModelLoader/PlantModelGeometry/PlantBakedModel ported to NeoForge 1.21.1, registered via ModelEvent.RegisterGeometryLoaders; also fixed an unrelated pre-existing particle-crash bug found live during verification"
metadata: 
  node_type: memory
  type: project
  originSessionId: 5a1274e9-916b-41f4-89dd-97a9cc4087b3
  modified: 2026-08-14T12:49:34.151Z
---

Final task of the Phase 7 10-task order from [[fossils-phase7-recipes-datagen-research]]. Ports the custom `fossil:plant_loader` block-model geometry loader that the 23 organic/branching models (`figurine_*` ×18, `zamites`, `dipteris`, `tempskya_leaf`, `tempskya_top`, `mutant_tree_tumor`) depend on for arbitrary 3-axis rotation + out-of-bounds coordinates vanilla `BlockElement` can't express.

## What was already done (discovered before starting, not new work)

`PlantBlockModel.java` and `PlantModelBakery.java` (`com.github.fossil.client.model.block`) were **already fully and correctly ported** — these are the loader-agnostic JSON-parsing/quad-baking logic, pure vanilla API (`Direction`, `GsonHelper`, `Material`, `FaceInfo`, `BakedQuad`, `Mth`, `Vector3f`/`Vector4f`), no Forge/NeoForge dependency at all, so nothing needed to change. Confirmed via direct diff against source — byte-for-byte matching logic.

## What was actually ported this task

Three new files under `com.github.fossil.client.model.block`, the NeoForge-specific glue the source mod kept in its `forge/` module:
- **`PlantModelLoader`** — `implements IGeometryLoader<PlantModelGeometry>`, 1:1 port (only package rename `net.minecraftforge.client.model.geometry` → `net.neoforged.neoforge.client.model.geometry`).
- **`PlantModelGeometry`** — `implements IUnbakedGeometry<PlantModelGeometry>`. Real API change: NeoForge 1.21.1's `bake(...)` dropped the trailing `ResourceLocation location` param (5 params now, was 6) — confirmed via the real NeoForge 21.1.248 sources jar, not guessed.
- **`PlantBakedModel`** — `implements IDynamicBakedModel` (NeoForge's, confirmed to just narrow/default `getQuads` to a 5-arg `ModelData`/`RenderType` overload, same as old Forge's version, nothing else added). **One required addition not present in the 1.20.1 source**: `getTransforms()` — NeoForge 1.21.1's `BakedModel`/`IBakedModelExtension` gives no default for it (Forge's `IForgeBakedModel` apparently did), so it must now be implemented explicitly (`return ItemTransforms.NO_TRANSFORMS`) or every plant-loader model would NPE on inventory/GUI render.

Registration: added `registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event)` to `ClientModEvents` (mod-bus `@SubscribeEvent`, matching the class's existing pattern) calling `event.register(PlantBlockModel.LOADER, new PlantModelLoader())`.

## Verification

- `compileJava` clean.
- `runClient` boot: **zero "Model loader 'fossil:plant_loader' not found" errors** (this exact error was previously seen and documented in [[fossils-phase1-blocks-progress]] before this task existed — now gone). Zero missing-model spam for any plant-loader-driven block/item.
- Benign, expected residual: `ModelManager` logs `Missing textures in model fossil:X#inventory` WARN (not ERROR) for all 23 plant-loader items — this is because the generic missing-texture validator walks `getMaterials()`/`resolveParents()`, which `IUnbakedGeometry` defaults to a no-op/empty set for custom geometry loaders that don't override them (ours doesn't, matching source's own scope — it was never overridden there either). Cosmetic only, doesn't affect actual baking (confirmed by the earlier point — no missing-model/crash errors), same category of quirk any custom-geometry-loader mod exhibits. Not something to "fix" — there's no vanilla API for a loader to declare per-part textures to that specific validator pass short of implementing `getMaterials()` fully, which the source mod never did either.

## Unrelated bug found + fixed during verification (real user crash)

While `runClient` was running live on the user's screen for verification, they hit a real crash going into the creative "fossil items" tab: `NullPointerException: Cannot invoke "java.util.List.size()" because "this.sprites" is null` in `ParticleEngine$MutableSpriteSet.get` ← `TextureSheetParticle.pickSprite` ← `VolcanoVentAshParticle$Provider.createParticle`. **Root cause confirmed unrelated to plant_loader**: `src/main/resources/assets/fossil/particles/` didn't exist at all in the target repo — the 5 particle-definition JSON files (`bubble.json`, `tar_bubble.json`, `volcano_vent_ash.json`, `volcano_vent_ash_emitter.json`, `laser_particle.json`) that `RegisterParticleProvidersEvent`'s `registerSpriteSet` calls need to populate each `SpriteSet`'s sprite list were simply never copied over in whichever earlier phase ported the particle Java classes (`ModParticles`/`ClientModEvents.registerParticleProviders` — likely Phase 5, client rendering). The crash was triggered by routine ambient particle ticking in the already-loaded world (a volcano vent block emitting ash), coincidentally timed with the user having the inventory tab open — not caused by opening the tab itself.

**Fix**: copied the 5 JSON files verbatim from the source repo (`common/src/main/resources/assets/fossil/particles/`) plus the 2 custom textures they reference that didn't exist yet either (`textures/particle/tar_bubble.png`, `tar_bubble_popped.png` — the other referenced textures, `minecraft:generic_0`-`7` and `minecraft:bubble`, are vanilla-bundled, no copy needed). Re-ran `runClient` — confirmed the same NPE no longer occurs, client stayed up stable with no crash.

This is a **retroactive gap in Phase 5** (client rendering/particles), not Phase 7 — flagging here since it was found in this session, but it's a separate historical gap, not part of task 10's actual scope. Worth a mental note that Phase 5's asset-copy step for particles may have been incomplete; no other particle-adjacent gaps were searched for beyond what this specific crash surfaced.

## Two more real bugs found + fixed via live user testing (after initial "done" claim)

The user tested the live `runClient` session further and found the plant_loader work wasn't actually fully correct yet — a good reminder that "compiles + boots clean" isn't the same as "renders correctly," matching [[feedback-phase-completion-includes-assets]]'s spirit but for rendering correctness specifically.

1. **Held-item icon missing entirely for zamites/dipteris.** `assets/fossil/textures/item/plants/plant_{zamites,dipteris}.png` were never copied to the target repo (their item models at `models/item/{zamites,dipteris}.json` reference `fossil:item/plants/plant_zamites`/`plant_dipteris`, distinct from the block textures). Copied both files from source. **This is one small corner of a MUCH bigger, previously-unnoticed gap**: `assets/fossil/textures/item/` in the target repo has exactly 1 file total (an armor texture) vs **989** in the source repo — meaning most custom items in this mod likely show missing/checkerboard held-item textures right now. Flagged to the user as a separate, deferred, much larger asset-backfill task — not yet started beyond the 2 files needed to unblock this specific bug.

2. **Placed zamites/dipteris textures looked like "the full minecraft texture map... a lot of different block textures" (real bug, root-caused and fixed).** `PlantModelBakery.fillVertex()` was passing raw UV values (from `PlantBlockFaceUV.getU/getV()`, JSON-authored in a standard-Minecraft "0-16 unit" convention per the model's `texture_size`) directly into `TextureAtlasSprite.getU(float)/getV(float)`, which expect an **already-normalized 0-1 fraction** of the sprite's atlas region (confirmed directly from decompiled 1.21.1 vanilla source: `getU(float p) { return u0 + (u1-u0)*p; }` — no internal /16 division). Vanilla's own `FaceBakery.fillVertex()` always does `sprite.getU(blockFaceUV.getU(idx) / 16.0F)` before calling in — our ported `PlantModelBakery` never divided by 16, so every UV coordinate sampled wildly outside the intended sprite region and wrapped into unrelated regions of the shared block texture atlas. Fixed by adding `/ 16.0F` to both `getU`/`getV` calls, matching vanilla's exact convention.

   **Notable**: read the *source* 1.20.1 mod's `PlantModelBakery.java` directly and confirmed it has this exact same missing `/16.0F` — meaning either (a) this is a genuine pre-existing upstream bug in the 1.20.1 mod that was faithfully (if unfortunately) carried over by this port, or (b) 1.20.1's Forge-patched `TextureAtlasSprite.getU/getV` had different, auto-dividing internals that 1.21.1 vanilla no longer has (couldn't fully confirm — no 1.20.1 decompiled vanilla source was locally available to check definitively, only a Forge-patch diff that didn't touch these methods, which is suggestive but not conclusive). Either way, the fix here is unambiguously correct for 1.21.1: matches vanilla's own current `FaceBakery` convention exactly, and the user confirmed the visual fix live ("it all works!").

**Root-cause technique note for future similar bugs**: when a custom-geometry-loader's baked output looks visually "scrambled across the whole texture atlas" (as opposed to just wrong-colored or offset), suspect a missing UV normalization step (raw-pixel-or-16-unit UV passed where a 0-1 fraction was expected, or vice versa) before anything more exotic — this is a very recognizable symptom once you've seen it once.

## Status

**Phase 7 (Recipes & datagen) is now fully COMPLETE and live-verified** — all 10 tasks from [[fossils-phase7-recipes-datagen-research]]'s original order are done: recipe framework, datagen entry point, block state/item providers, tags, loot, recipes, advancements, food values, worldgen Java rewrite (done as [[fossils-phase7-task9-progress]], expanded significantly beyond the original "port ModWorldGenProvider" scope), plant_loader (including the 2 real bugs above, confirmed fixed via live client testing with the user, not just compile/boot checks).

**Committed to git**: this task's plant_loader files, the particle asset fix, the block-render-type registration fix, and the UV-divide fix are all committed in commit `382f345` ("phase 7 task 10: plant_loader geometry loader + 3 rendering bugs fixed"), on top of `d4b6dab` (task 9 + tasks 3-8 backfill).

**Deferred, not started**: the ~987-file item-texture asset-copy backfill (`assets/fossil/textures/item/`) surfaced above — likely affects most custom items' held/inventory appearance mod-wide. Separate scope from Phase 7, probably belongs with whichever earlier phase (Items, likely Phase 2) originally should have copied these.
