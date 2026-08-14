---
name: fossils-phase9-compat-research
description: "Phase 9 (compat integrations) research checkpoint (2026-08-14): confirmed current NeoForge 1.21.1 versions for every compat dependency, full compat file inventory from source incl. 2 items missed by the original migration plan (Carry On, Create/Flywheel/Registrate). ZERO implementation done yet."
metadata:
  type: project
---

Research-only pass before starting [[fossils-migration-plan]]'s Phase 9. Per [[feedback-plan-includes-research-stop-before-code]], no files have been edited yet — this is the confirmed scope + version catalog to work from. See TaskList (tasks 1-11, "Phase 9 task N: ...") for the tracked task breakdown this maps to.

**Source repo location correction:** confirmed still at `~/IdeaProjects/FossilsArcheologyRevival` (see fix applied directly to [[fossils-migration-plan]] - a prior note there wrongly claimed it moved to `~/Personal`, which doesn't exist on this machine).

## Confirmed NeoForge 1.21.1 versions (checked 2026-08-14, re-verify at actual build time)

- **JEI**: 19.24.0.317 (modrinth)
- **EMI**: 1.1.22+1.21.1+neoforge
- **REI**: 16.0.799+neoforge (also 16.0.797 seen)
- **Jade**: 15.10.5+neoforge
- **Farmer's Delight**: 1.3.2 (`1.21.1-1.3.2` on modrinth) — source used a much older FD version, expect API drift
- **Carry On**: 2.2.2 on modrinth has 1.21.1 NeoForge support (source used forge `2.1.2.7`/fabric `Mkla4B3q` via `maven.modrinth:carry-on`)
- **Create**: 6.0.10-280, **Flywheel**: 1.0.6, **Registrate**: MC1.21-1.3.0+67 (per Create's own NeoForge-1.21.1 dev wiki page)
- **Alex's Mobs**: no official 1.21.1 build exists; **unofficial NeoForge port** at modrinth slug `alexs-mobs(1.21.1)`, versions up to 1.22.17 seen, depends on **Citadel (Unofficial Port)** modrinth slug `citadel-(1.21.1-port)`, up to 2.7.5 seen. User confirmed 2026-08-14 the unofficial-port route is acceptable.
- **Prehistoric Fauna**: confirmed no 1.21.1 (or NeoForge) build anywhere — latest activity targets 1.20.1 Forge only. **Decision: drop**, per the migration plan's own expectation.

## Compat scope corrections vs. the original migration plan / inventory

The plan's Phase 9 description and [[fossils-migration-inventory]]'s "32 common compat files" catalog **missed two real compat integrations** found by re-scanning the source tree directly:

1. **Carry On** (`compat/carryon/CarryOnCompat.java`, common) — a **mixin-based** soft-dependency compat, not a plugin-style one. `CarryOnCompat.overridePickUp()` is called from `forge/mixin/PickupHandlerMixin.java` (`@Mixin(PickupHandler.class)`, `require = 0` so it no-ops if Carry On isn't installed) targeting `tschipp.carryon.common.carry.PickupHandler#tryPickupEntity`. Needs a real compile-time dependency (`modCompileOnly "maven.modrinth:carry-on:$carry_on_version"`) since the mixin imports CarryOn's class directly, despite being "optional" at runtime. Small (~20 lines + 1 mixin).
2. **Create/Flywheel/Registrate** — the plan listed this as a full compat integration, but the *only* thing that actually exists in source is `forge/data/providers/FossilCreateRecipeProvider.java`: a **datagen-only** class emitting 7 Create "crushing" recipe JSONs for fossil blocks, gated by a `ModLoadedCondition`/`whenModLoaded(Create.ID)` so the recipes are inert if Create isn't present. No runtime `IModPlugin`-style class exists at all — this is much lighter than the plan implied and belongs alongside `ModRecipeProvider`/datagen wiring, not a full plugin port.

## Per-integration file inventory (from source, all under `common/src/.../compat/` unless noted)

- **JEI** (`compat/jei/`, 7 files): `FossilJEIPlugin` + `AnalyzerCategory`/`CultureVatCategory`/`MultiOutputCategory`/`SifterCategory`/`WithFuelCategory`/`WorktableCategory`.
- **EMI** (`compat/emi/`, 7 files + 1 forge/fabric-split file): `FossilEmiPlugin` + `AnalyzerEmiRecipe`/`CultureVatEmiRecipe`/`MultiOutputEmiRecipe`/`SifterEmiRecipe`/`WithFuelEmiRecipe`/`WorktableEmiRecipe`; `MultiOutputEmiRecipeImpl` is forge/fabric-split (`compat/emi/forge/` vs `compat/emi/fabric/`), collapses to one NeoForge class.
- **REI** (`compat/rei/`, heaviest, 13 files): `FossilREIClientPlugin`+`FossilREICommonPlugin` (common versions) plus forge-only `FossilREIClientPluginForge`/`FossilREICommonPluginForge` wrappers (collapse to one), categories+displays for Analyzer/CultureVat/MultiOutput/Sifter/Worktable, plus `AnalyzerTagRecipe`.
- **Jade** (`forge/compat/jade/` + `fabric/compat/jade/`, small, 4 files each side, ~125 LOC forge side): `FossilJadePluginForge`/`FossilJadePluginFabric` (collapse, use forge's `IWailaPlugin` shape), `FeederStatusProvider`, `PrehistoricBreedingProvider`, `PrehistoricGrowthProvider`.
- **Alex's Mobs** (`forge/compat/alexsmobs/AlexsMobsCompat.java`, forge-only, ~110 lines): pure food-mapping registration calls (`FoodMappingsManager.addMeat/addFish/addPlant` for ~90 AM items/entities), no rendering/entity logic — low risk despite the unofficial-port dependency risk.
- **Farmer's Delight core** (`compat/farmers/CommonFarmersDelightCompat.java` common + `forge/compat/farmers/FarmersDelightCompat.java`, ~130 LOC total): recipe-conflict removal (drops vanilla-conflicting egg-cooking recipes when FD is/isn't present) + ~70 FD item food-mappings.
- **Farmer's Delight addons** (`forge/compat/farmers/addon/`, 21 files + `util/AddonConstants.java`; `fabric/.../addon/`, 12 files + util) — union of ~22 unique addon mods: Alex's/Argentina's/Ars Nouveau+Delightful/BYG+Delightful/Brewin'&Chewin'/Butcher's/Casualness/Coffee/Corn/Crabber's/Cultural/Delightful(base)/End/Ender/Expanded/Farmer's Respite/Festive/Miner/Nether/Ocean/Pineapple/Seed Delight. Each file is 15-100 lines, almost all pure `FoodMappingsManager` registration calls (same low-risk shape as the core FD/Alex's Mobs compat above). **Per the migration plan's explicit caution, each addon's own 1.21.1 NeoForge availability needs checking individually before porting it** — not done yet, that's task 10's job specifically.

## GeckoLib compat classes are NOT part of Phase 9 scope

`compat/geckolib/AnimationControllerOverride.java` + `AnimationProcessorOverride.java` are already ported and present in the target repo (`src/main/java/com/github/fossil/compat/geckolib/`) — these were handled as part of [[fossils-phase8-tasks6to8-progress]]'s GeckoLib `AnimationProcessor.tickAnimation` rewrite, not Phase 9. Don't re-port them.
