# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Unofficial port of Fossils and Archeology Revival to **Minecraft 1.21.1 / NeoForge** (ModDevGradle, Parchment mappings, Java 21). Mod id `fossil`, base package `com.github.fossil`.

## Commands

```bash
./gradlew build          # compile + jar (what CI runs); output in build/libs
./gradlew runClient
./gradlew runServer      # dedicated server, --nogui, game dir is ./run
./gradlew runData        # datagen -> src/generated/resources
./gradlew runGameTestServer
```

- **JDK:** the ambient `java` in git-bash on this machine is JDK 8 and fails the ModDevGradle plugin. Set `JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot"` before invoking Gradle.
- **No unit tests** exist (no `src/test`). Verification is done by booting the game/server. The gametest namespace is wired to `fossil` but no gametests are registered.
- **`libs/Rainbow-Reef-1.21.1-3.0.0.jar`** is a gitignored private jar; the build will not compile `compat/rainbowreef` without it.
- **`-PpackRun`** clears every `runtimeOnly` dependency so `runClient`/`runServer` load only the jars in `run/mods` — used to reproduce bugs against a real modpack's mod set. GeckoLib, TerraBlender and more-hitboxes still load from Gradle, so don't put those jars in `run/mods`.
- **Headless `runServer`:** the dedicated server exits immediately on stdin EOF. Keep stdin open (`tail -f /dev/null | ./gradlew runServer > log 2>&1`) and drive tests via a datapack `load` function + `say` output in the log rather than console commands. Kill leftover `java.exe` children afterwards.

## Build quirks worth knowing

- **more-hitboxes is repacked.** Its published jar lacks the `[[mixins]]` entry and an `Automatic-Module-Name`, so `build.gradle` extracts it, patches the TOML/manifest, and depends on the patched jar via `implementation files(...)` **and** `jarJar` (so the fix ships inside our jar). Don't replace this with a plain Maven coordinate.
- **Hard deps** (`implementation`): GeckoLib, TerraBlender, more-hitboxes. **Everything else** (JEI/EMI/REI, Jade, Farmer's Delight + ~20 addons, Create, Alex's Mobs Continued, Fintastic, Rainbow Reef, Carry On) is optional: `compileOnly` + `runtimeOnly`, and gated in code by `ModList.get().isLoaded(...)`. Some are deliberately `compileOnly` only because their 1.21.1 builds crash at boot (Carry On, Farmer's Respite) — see comments in `build.gradle` before adding `runtimeOnly`.
- `src/main/templates/META-INF/neoforge.mods.toml` is expanded from `gradle.properties` by `generateModMetadata`.
- `src/generated/` is gitignored but added as a resource dir; generated files are also copied into `src/main/resources` to ship them, and `processResources` uses `DuplicatesStrategy.EXCLUDE`. When changing something that datagen owns, update the provider and re-run `runData` rather than hand-editing JSON (and make sure the checked-in copy under `src/main/resources` matches).

## Architecture

**Entry point:** `FossilsAndArcheologyRevival` registers every `DeferredRegister` holder class (`ModBlocks`, `ModItems`, `ModEntities`, …) on the mod bus, plus: code-only registries (`STRUCTURE_TYPE`, `CHUNK_GENERATOR`) inside a `RegisterEvent` listener (they're frozen everywhere else), TerraBlender region + surface rules in `FMLCommonSetupEvent#enqueueWork`, and optional compat registration gated on `ModList`. Other event handlers use `@EventBusSubscriber` (e.g. `client/ClientModEvents`, `entity/data/EntityDataEvents`).

**Prehistoric creatures** — adding/changing one touches several places:
- `entity/ModEntities` — `registerDino(...)` entity type + attribute registration list.
- `entity/prehistoric/base/PrehistoricEntityInfo` — enum of every creature: mob type (DINOSAUR, FISH, BIRD, …), time period, spawn-egg colours, flags like `bucketable()`. Drives generated items (DNA, eggs, fossils, etc.) and lookups via `resourceName`.
- Class hierarchy: `Prehistoric` (extends `TamableAnimal`, GeckoLib multipart) → `PrehistoricFlying` / `PrehistoricSwimming(Bucketable)` / `PrehistoricLeaping` / `PrehistoricFlocking`; fish use the separate `PrehistoricFish` (extends `AbstractFish`). Behaviour is split into `entity/prehistoric/system/*System` classes (AI, mood, sleep, sit, instruction, leap) rather than living in the entity.
- **Data-driven stats:** `data/fossil/entity_info/<name>.json` (attributes, AI types, diet, scale, age days, hunger, population) loaded by `EntityDataLoader` (reload listener, synced to clients). Also `data/fossil/hitboxes/<name>.json` (more-hitboxes multipart) and `data/fossil/animations/entity/` (server-side animation data); client assets in `assets/fossil/geo`, `assets/fossil/animations`.
- Renderers registered in `client/ClientModEvents`; sounds in `sounds/ModSounds`.

**Worldgen:** datapack registries (configured/placed features, biomes, structures, template pools, structure sets, biome modifiers, dimension types, damage types, enchantments) are defined in Java `bootstrap` methods and emitted by `data/DataGenerators` via `createDatapackRegistryObjects`. The `fossil:volcano` biome is added through TerraBlender (`world/biome/FossilRegion`). Custom dimensions (Anu lair, treasure room) use custom chunk generators in `world/chunk`.

**Volcano surface-rule workaround:** in large modpacks TerraBlender's surface-rule dispatch is lost, so `mixin/NoiseBasedChunkGeneratorMixin` (with `world/surfacerules/TerraBlenderSurfaceRuleFix` and `VolcanoSurfaceRuleInjector`) re-applies TerraBlender's tag and, as the reliable layer, splices `ModSurfaceRules.VOLCANIC_SURFACE_RULE` directly into the rule chain at `buildSurface`. Don't "fix" volcano ground by changing biome placement — only newly generated chunks reflect surface-rule changes.

**Village houses:** vanilla `template_pool/village/*/houses.json` overrides were removed in favour of runtime injection (`world/feature/structures/VillageHouseInjector` + `mixin/StructureTemplatePoolAccessor`) to avoid clobbering other mods' village additions.

**Mixins:** `src/main/resources/fossil.mixins.json`, package `com.github.fossil.mixin`. Compat mixins targeting optional mods (e.g. `PickupHandlerMixin` for Carry On) must use `require = 0`. New mixins must be added to the JSON.

**Compat:** each integration lives in `compat/<mod>/` and must only be class-loaded behind an `isLoaded` check (JEI/EMI/REI/Jade use their own plugin discovery annotations instead). Farmer's Delight addon integrations are driven by `compat/farmers/FarmersDelightAddonCompat` + `AddonConstants`.

Note: comments in `build.gradle` reference `.claude/memory/*.md` notes; those files are not in the repo.
