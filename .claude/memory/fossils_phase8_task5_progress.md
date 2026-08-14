---
name: fossils-phase8-task5-progress
description: "Phase 8 Task 5 (Cat/Ocelot laser goals, CameraMixin, ExplosionMixin, NaturalSpawnerMixin) COMPLETE as of 2026-08-14: all 4 items ported+live-verified; ExplosionMixin was replaced entirely by a native NeoForge ExplosionEvent.Start listener instead of a mixin"
metadata:
  type: project
---

Completes item 5 of the 8-task Phase 8 mixin port breakdown in [[fossils-phase8-mixins-progress]]. That file's own task-5 description said "Cat/Ocelot laser-goal mixins, CameraMixin, ExplosionMixin, NaturalSpawnerMixin" — all 4 are now done.

## What was ported

- **`CatMixin`/`OcelotMixin`** (`com.github.fossil.mixin`, both-sides) — straight port, zero API drift. Both inject a `NearestAttackableTargetGoal<LaserPointEntity>` into `targetSelector` at the tail of `registerGoals()`, giving cats/ocelots a chase-the-laser-pointer AI goal. `Cat`/`TamableAnimal`/`Ocelot`/`Animal` constructors and `Mob.targetSelector`'s visibility (`public final`) are all unchanged from the 1.20.1 source in 1.21.1.
- **`CameraMixin`** (client-only) — pulls the detached third-person camera back when riding a `Prehistoric` mob taller than 2 blocks. **Real API drift found**: `Camera.move(double, double, double)` in source is actually `Camera.move(float zoom, float dy, float dx)` in 1.21.1 (param types changed double→float). Fixed by re-declaring the `@Shadow` with `float` params and casting the computed offset. `Camera.setup(BlockGetter, Entity, boolean, boolean, float)`'s own signature is unchanged.
- **`NaturalSpawnerMixin`** (both-sides) — straight port, zero drift. `NaturalSpawner.getRandomPosWithin(Level, LevelChunk)`'s body/local-variable order (`chunkpos, i, j, k, l` → captured as `chunkPos, x, z, maxY, y`) is byte-for-byte unchanged in 1.21.1, so the `@Inject(locals = LocalCapture.CAPTURE_FAILSOFT)` local capture still lines up. Biases mob-spawn Y position into a safe band inside the Anu Lair dimension so sentries don't spawn in the void.
- **`ExplosionMixin` → NOT ported as a mixin.** Replaced with a plain `@EventBusSubscriber` listener, `AnuLairEvents.preventExplosionBeforeAnuKilled` (new file: `com/github/fossil/entity/monster/AnuLairEvents.java`), subscribing to NeoForge's own `ExplosionEvent.Start` (`net.neoforged.neoforge.event.level.ExplosionEvent`, fired on `NeoForge.EVENT_BUS`, `ICancellableEvent`). This is strictly better than the source's mixin: `Start` fires *before* the explosion computes its affected-block list at all, so `event.setCanceled(true)` suppresses the whole explosion in one call — no need to reach into `Explosion`'s private `toBlow` field via `@Shadow`/`locals` capture like the old mixin did. Confirms the migration plan's category-(d) guidance ("check for a native event before defaulting to a mixin") was right to flag this one specifically. Uses the exact same `AnuBoss.AnuLair.FACTORY`-based `getDataStorage().get(...)` lookup and `level().dimension() == ModDimensions.ANU_LAIR` comparison idiom already used at `AnuBoss.java:174` — kept consistent with existing target code rather than the source mixin's `.location().equals(...)` phrasing.

## Verification

Compile clean (`./gradlew compileJava`). Live-verified via the established nohup-background + debug.log-grep pattern from [[fossils-phase8-mixins-progress]]:
- `runServer`: booted to `Done (5.073s)!`, log shows `Mixing CatMixin`/`Mixing OcelotMixin`/`Mixing NaturalSpawnerMixin from fossil.mixins.json`, zero FATAL/InvalidInjectionException. Shut down cleanly via RCON `stop`.
- `runClient`: log shows `Mixing CameraMixin from fossil.mixins.json into net.minecraft.client.Camera`, zero FATAL. Only pre-existing unrelated warnings present (missing sounds/subtitles for a couple of entities, a GeckoLib animation-parse warning for `postosuchus`) — none related to this task's changes. Killed cleanly via `pkill -f net.neoforged.devlaunch.Main`.

**Not exercised interactively**: `CameraMixin`'s actual runtime behavior (mounting a >2-block-tall Prehistoric and checking the detached camera pulls back) and `CatMixin`/`OcelotMixin`'s in-game goal behavior (throwing a laser pointer at a cat/ocelot and watching it chase) need a human playtest to confirm visually — boot-clean + mixin-applied only proves the bytecode wired up without throwing, not that the gameplay feel is correct. Flag this the same way [[fossils-phase7-task10-plant-loader]] did before its own live playtest surfaced 3 real bugs that compile/boot alone missed.

## Current `fossil.mixins.json` state (end of Task 5)

```json
"mixins": ["AbstractFishMixin", "ServerEntityMixin", "WalkNodeEvaluatorMixin", "MobMixin",
           "CatMixin", "OcelotMixin", "NaturalSpawnerMixin"],
"client": ["ParticleAccessor", "TerrainParticleMixin", "TitleScreenMixin", "PlayerInfoMixin",
           "DebugLevelRendererMixin", "DebugMinecraftMixin", "DebugMouseHandlerMixin", "CameraMixin"]
```

**How to apply:** when resuming Phase 8, start at Task 6 (`SyncedEntityDataMixin`, port only the `markDirty` half per [[fossils-phase8-mixins-progress]]'s existing notes). Re-verify this file's mixin list against the actual `fossil.mixins.json` before trusting it (point-in-time snapshot).
