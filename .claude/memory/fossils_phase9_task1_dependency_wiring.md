---
name: fossils-phase9-task1-dependency-wiring
description: "Phase 9 task 1 (dependency wiring) COMPLETE as of 2026-08-14: all 9 compat-tier NeoForge 1.21.1 dependencies (JEI/EMI/REI/Jade/Farmer's Delight/Carry On/Create+Flywheel/Alex's Mobs+Citadel) added to build.gradle/gradle.properties and verified resolving on both compile and runtime classpaths"
metadata:
  type: project
---

Task 1 of [[fossils-phase9-compat-research]]'s 11-task breakdown. Done: build.gradle/gradle.properties edited, nothing else yet (no compat Java code ported - tasks 2-10 still pending).

## What was added

5 new repositories in `build.gradle` (each scoped via `content { includeGroup(...) }` to avoid polluting resolution for unrelated groups): `maven.blamejared.com` (JEI), `repo.sleeping.town` (EMI), `maven.shedaniel.me` (REI + Cloth Config, two groups: `me.shedaniel` and `me.shedaniel.cloth`), `maven.architectury.dev` (REI's transitive `dev.architectury:architectury-neoforge` dep), `maven.createmod.net` (Create/Flywheel), `api.modrinth.com/maven` (Jade/Farmer's Delight/Carry On/Alex's Mobs/Citadel, all under the `maven.modrinth` group-id alias).

10 new version properties in `gradle.properties` under a new "Optional compat dependencies (Phase 9)" section, and 9 `compileOnly`+`runtimeOnly` dependency pairs in `build.gradle` (see [[fossils-phase9-compat-research]] for the version numbers themselves and why each was picked).

## Gotcha found + fixed: REI's transitive deps needed their own repo entries

First `./gradlew dependencies --configuration compileClasspath` pass FAILED to resolve `me.shedaniel.cloth:cloth-config-neoforge` and `dev.architectury:architectury-neoforge` — both are real transitive dependencies of `me.shedaniel:RoughlyEnoughItems-neoforge`'s own POM, but my initial `Shedaniel (REI)` repo's `content{includeGroup('me.shedaniel')}` filter only matched the `me.shedaniel` group, not `me.shedaniel.cloth`, and nothing declared `dev.architectury` at all. Fixed by widening the Shedaniel repo to include both `me.shedaniel` and `me.shedaniel.cloth`, and adding a dedicated `maven.architectury.dev` repo for `dev.architectury`. **Lesson: when compileOnly-declaring a compat mod, its own transitive deps still need repos wired even though this project's code never imports their classes directly** — Gradle resolves the full graph regardless of compileOnly vs implementation.

## Verification done

`./gradlew dependencies --configuration compileClasspath` and `--configuration runtimeClasspath` both show all 11 compat-related coordinates (9 declared + cloth-config + architectury transitively) resolving with zero `FAILED`/`Could not resolve` lines. `./gradlew compileJava` still succeeds cleanly (no compat Java code exists yet, so this just confirms the dependency change alone didn't break anything). **Not yet done: an actual `runClient`/`runServer` boot with these mod jars present** - that's deferred to task 11's full verification pass once tasks 2-10 give these dependencies something to actually exercise.

## Note for future tasks

Citadel's own maven coordinate resolved fine directly via the Modrinth repo with no extra wiring needed (unlike REI, its dependency graph is self-contained) - `maven.modrinth:citadel-(1.21.1-port):2.7.6` shows up correctly under `runtimeClasspath`.
