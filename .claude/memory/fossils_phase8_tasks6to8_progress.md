---
name: fossils-phase8-tasks6to8-progress
description: "Phase 8 tasks 6-8 COMPLETE as of 2026-08-14 - Phase 8 (mixins) is now FULLY DONE (all 8 tasks): SyncedEntityDataMixin superseded by a vanilla API addition, the GeckoLib additive-animation group fully reimplemented for 4.9.2's restructured AnimationProcessor, and ChunkStatusMixin/JigsawPlacementMixin ported despite a total rewrite of vanilla's structure-generation pipeline; JigsawPlacementMixin needed 3 live-testing iterations to get right"
metadata:
  type: project
---

Completes Phase 8. See [[fossils-phase8-mixins-progress]] (tasks 1-4) and [[fossils-phase8-task5-progress]] (task 5) for the earlier tasks. **All 8 tasks of the agreed breakdown are now done — Phase 8 is complete.**

## Task 6: SyncedEntityDataMixin — not ported at all, superseded by a vanilla API addition

The source mixin existed purely to force `SynchedEntityData` to mark a field dirty even when `set()` wouldn't otherwise flag it (needed by `LeapSystem.setLeapTarget()` to force a resync when re-targeting the same entity ID). **1.21.1's vanilla `SynchedEntityData` already has this**: `set(EntityDataAccessor<T> key, T value, boolean force)` — a 3-arg overload with an explicit force-dirty flag, sitting right next to the 2-arg `set(key, value)` old mixin behavior replicated by hand. Fixed by changing the one call site (`LeapSystem.java:207`) to `entityData.set(LEAP_TARGET_ID, target == null ? -1 : target.getId(), true)`. Zero mixin code needed. (The other half of the source mixin, `markNonDefaultAsDirty`, was already correctly identified as dead Fabric/Architectury-race-condition code in the original task list and stays unported.)

## Task 7: GeckoLib additive-animation group — full reimplementation, not a mechanical port

This is the group the migration plan and [[fossils-phase8-mixins-progress]] both flagged as highest-risk, and it undersold it: GeckoLib didn't just rename its `core.*` package prefix between 4.7.2 and 4.9.2 (true, but the smaller part) — **`AnimationProcessor.tickAnimation` was completely restructured**. In 4.7.2, controller-processing and per-bone value application were two separable steps (matching the source mixin's clean 2-injection design: process all controllers, then a second pass applies bone values additively). In 4.9.2, vanilla inlined per-bone value application *inside* the per-controller loop itself (last-processed controller simply overwrites earlier ones' bone values for shared bones) - there's no longer a clean seam to hook.

**Confirmed this isn't cosmetic**: every `Prehistoric` entity registers exactly two simultaneous `AnimationController`s (`IDLE_CTRL` + `ATTACK_CTRL`, see `Prehistoric.java:1558`), and target's `PrehistoricGeoModel`/`PrehistoricFishGeoModel` (used by ~65 dinosaur/fish entities) already implement the `AdditiveAnimationModel` marker interface - so this system is live-relevant to the whole entity roster right now, not a dormant future feature.

**Solution**: `AnimationProcessorMixin` now does a single `@Inject(at = @At("HEAD"), cancellable = true)` on `tickAnimation` - for `AdditiveAnimationModel` instances only, it delegates to `AnimationProcessorOverride.tickAdditiveAnimations(...)` (a from-scratch reimplementation of vanilla's *entire* `tickAnimation` body, restructured back into the old two-pass shape so bone values combine additively) and cancels vanilla's own body. Non-additive models are completely untouched - zero behavior change for anything that doesn't opt in.

**Other findings in this group**:
- `AnimatableManager.isFirstTick()` is now a public method (was private in 4.7.2) - one fewer accessor needed than planned. Still needed `AnimatableManagerAccessor` for `finishFirstTick()` (still protected) via `@Invoker`.
- `AnimationController`'s `boneAnimationQueues`/`getCurrentAnimation()`/`getBoneAnimationQueues()` are already public in 4.9.2; only `isJustStarting` (setter) and `overrideEasingTypeFunction` (getter) still needed `AnimationControllerAccessor`.
- Added a new `AnimationProcessorAccessor` (not in the original plan) to expose `AnimationProcessor`'s private `bones` map by reference - avoids rebuilding a `Map<String, GeoBone>` from `getRegisteredBones()` every tick for every animated entity.
- GeckoLib's own math-value types moved too: `com.eliotlash.mclib.math.{IValue,Constant}` (external library) → `software.bernie.geckolib.loading.math.{MathValue,value.Constant}` (now an internal GeckoLib type) - same shapes, different package.
- `AnimationControllerMixin`'s Molang injection target confirmed exactly as the migration plan predicted: `MolangParser.setValue(...)` → `MathParser.setVariable(MolangQueries.ANIM_TIME, ...)`.
- `GeoEntityRendererMixin` (the forge-source variant, since this project is NeoForge-only) needed one signature update: `GeoEntityRenderer.actuallyRender`'s 4 trailing `float` RGBA-color params collapsed into a single packed `int colour` in 4.9.2. Used bare method-name targeting (no explicit descriptor) for both `@ModifyExpressionValue`/`@WrapOperation` injections instead of hand-transcribing the new descriptor, since `actuallyRender` isn't overloaded - lower risk than getting a hand-computed descriptor wrong.
- `more-hitboxes`' own mods.toml-vendored mixins register their *own* separate `GeoEntityRendererMixin` (different package, same simple class name) targeting the same GeckoLib class - confirmed via boot log this coexists fine, both apply independently.

**Not exercised interactively**: the additive-animation combination itself (does an idle+attack blend actually look smoother now vs. before) needs a human playtest riding/watching a Prehistoric entity - boot-clean + mixin-applied only proves the bytecode wired up without an immediate crash.

## Task 8: ChunkStatusMixin + JigsawPlacementMixin — needed 3 live-test iterations, not just a rename

**`ChunkStatusMixin`**: the Yarn intermediary name `method_39464` from source's multi-loader build turned out to be moot - this project's toolchain uses real Mojang names directly, and the whole method moved from `ChunkStatus` itself into a new `net.minecraft.world.level.chunk.status.ChunkStatusTasks.generateStructureStarts(WorldGenContext, ChunkStep, StaticCache2D<GenerationChunkHolder>, ChunkAccess)` (totally different signature, `ChunkGenerator`/`ServerLevel`/`StructureTemplateManager` all now reached via `WorldGenContext` record accessors). Rewritten as a `@ModifyExpressionValue` on the `WorldOptions.generateStructures()` boolean check, forcing `true` when `worldGenContext.generator()` is our `TreasureChunkGenerator`/`AnuLairChunkGenerator` - simpler and lower-risk than the original `@Inject`+manual `createStructures()` call, since vanilla's own code already does that call right after the check we're forcing.

**`JigsawPlacementMixin`**: this was indeed the single highest-breakage-risk mixin in the whole migration, exactly as flagged. Vanilla's `JigsawPlacement$Placer` internals changed substantially: `readPoolName` → `readPoolKey` (now resolves pool aliases via a new `PoolAliasLookup` parameter - a genuinely new vanilla feature for data-driven structure pool aliasing, not just a rename), `tryPlacingChildren` gained two new trailing parameters (`PoolAliasLookup`, `LiquidSettings` - the latter also now a required trailing constructor arg on `PoolElementStructurePiece`), and **`placing` changed from a `Deque<PieceState>` to a `SequencedPriorityIterator<PieceState>`** (a priority-queue-like structure using a `placement_priority` NBT field vanilla now reads per-jigsaw-block - ported faithfully rather than dropping the new prioritization behavior for our own pieces).

**The real blocker, found only via live RCON testing (`/place structure fossil:fossil_site_normal`), not by static analysis**: `JigsawPlacement.PieceState` is a package-private record. Two attempted fixes failed before landing on the right one:
1. First attempt: a Mixin `@Invoker("<init>")` "duck interface" (works fine for regular methods) - failed at runtime with `InvalidAccessorException: OBJECT_FACTORY requires matching return type` - constructor-invokers require the *exact* target type as the declared return type, and `Object` doesn't satisfy that either. Confirmed via two separate live boot-crash iterations before accepting this pattern doesn't work for constructors the way it does for regular accessors.
2. Second attempt: a plain (non-mixin) Java helper class placed directly in `net.minecraft.world.level.levelgen.structure.pools` (same package as the target, so package-private access works at the language level) - compiled fine, but **crashed the server at the module-resolution stage before any mixin processing even starts**: `ResolutionException: Modules minecraft and fossil export package net.minecraft.world.level.levelgen.structure.pools to module terrablender` - a hard split-package conflict, since TerraBlender's own module also needs that package and the JPMS forbids two modules owning the same package.
3. **What actually works**: plain reflection. A `private static final Constructor<?>` field on `JigsawPlacementMixin` itself, resolved once via `Class.forName(...).getDeclaredConstructor(...).setAccessible(true)`, called via `.newInstance(...)` wrapped in a small helper method. This sidesteps both the Mixin accessor-return-type restriction (no accessor involved) and the split-package problem (no new class in the `net.minecraft` package tree) entirely. Live-verified: `/place structure fossil:fossil_site_normal 0 100 0` now succeeds with zero exceptions (previously crashed the whole RCON command with a `MixinTransformerError`/`ResolutionException` depending on which broken attempt was live at the time).

**Reusable lesson for any future "need to construct a package-private Minecraft inner class from mod code" situation**: don't reach for Mixin's `@Invoker("<init>")` (it requires an exact-type return, which is unavailable by definition in this scenario) and don't create a same-package helper class in your own mod's source tree (triggers a JPMS split-package resolution failure against any other mod that also touches that package) - go straight to cached reflection.

## Current `fossil.mixins.json` state (Phase 8 complete)

```json
"mixins": ["AbstractFishMixin", "ServerEntityMixin", "WalkNodeEvaluatorMixin", "MobMixin",
           "CatMixin", "OcelotMixin", "NaturalSpawnerMixin", "ChunkStatusMixin", "JigsawPlacementMixin"],
"client": ["ParticleAccessor", "TerrainParticleMixin", "TitleScreenMixin", "PlayerInfoMixin",
           "DebugLevelRendererMixin", "DebugMinecraftMixin", "DebugMouseHandlerMixin", "CameraMixin",
           "AnimatableManagerAccessor", "AnimationControllerAccessor", "AnimationProcessorAccessor",
           "AnimationControllerMixin", "AnimationProcessorMixin", "GeoEntityRendererMixin"]
```

**Deferred to Phase 9 (not part of this 8-task breakdown)**: `JigsawStructureMixin` (Lithostitched compat) and `PickupHandlerMixin` (CarryOn compat), per the standing decision in [[fossils-phase8-mixins-progress]].

**How to apply**: Phase 8 is done. Re-verify this file's mixin list against the actual `fossil.mixins.json` before trusting it (point-in-time snapshot). Next phase-level work should either move to whatever comes after Phase 8 in [[fossils-migration-plan]], or address the still-outstanding `textures/item/` backfill gap flagged in [[fossils-phase7-task10-plant-loader]] (1 file vs source's 989).
