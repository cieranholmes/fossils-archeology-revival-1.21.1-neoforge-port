---
name: fossils-phase7-task2-datagen-entry-point
description: "Phase 7 task 2 (datagen GatherDataEvent entry point) COMPLETE as of 2026-08-14: minimal DataGenerators class wired into the mod-bus, compile+boot verified, zero providers registered yet"
metadata:
  type: project
---

Continuation of [[fossils-phase7-task1-recipe-framework]] — task 2 of [[fossils-migration-plan]] Phase 7 (per the 10-task order in [[fossils-phase7-recipes-datagen-research]]) is now implemented and verified.

## What was done

New file `src/main/java/com/github/fossil/data/DataGenerators.java`: a `register(IEventBus modEventBus)` static method that does `modEventBus.addListener(DataGenerators::gatherData)`, with `gatherData(GatherDataEvent event)` currently **empty** — no providers registered yet. Wired into `FossilsAndArcheologyRevival.java`'s constructor via `DataGenerators.register(modEventBus);`, alongside the other `Mod*.register(modEventBus)` calls.

**Deliberately scoped this narrow**: the source repo's `DataGenerators` (`forge/.../forge/data/DataGenerators.java`) registers 10 providers in one method, but none of those provider classes (`ModBlockStateProvider`, `ModItemProvider`, `ModBlockTagsProvider`, `ModItemTagsProvider`, `ModEntityTypeTagsProvider`, `ModRecipeProvider`, `FossilAdvancements`, `ModLootProvider`, `ModWorldGenProvider`, `ModFoodValueProvider`) exist in this repo yet — those are tasks 3–9. Rather than write dead references to nonexistent classes, the entry point is a true skeleton; each later task adds its own `event.createProvider(...)`/`generator.addProvider(...)` line as that provider is built, keeping compile clean at every checkpoint (this project's standing bar, see [[feedback-phase-completion-includes-assets]]).

## Key decision: no `@Mod.EventBusSubscriber`

Source uses `@Mod.EventBusSubscriber(modid = ..., bus = Mod.EventBusSubscriber.Bus.MOD)` + `@SubscribeEvent`. This project never uses that annotation pattern anywhere (confirmed by grep) — every event listener is wired explicitly via `modEventBus.addListener(...)` calls inside `FossilsAndArcheologyRevival`'s constructor (see e.g. `RegisterEvent`/`FMLCommonSetupEvent` listeners already there, and `EntityDataEvents`). `DataGenerators.register(IEventBus)` follows that same explicit-wiring convention, matching the `Mod*.register(modEventBus)` naming/call shape used by every other subsystem.

## API confirmed live (extends [[fossils-phase7-recipes-datagen-research]])

Read `net.neoforged.neoforge.data.event.GatherDataEvent` and `net.minecraft.data.DataGenerator` source directly (not just inferred): `GatherDataEvent.addProvider(T)`/`createProvider(DataProviderFromOutput<T>)`/`createProvider(DataProviderFromOutputLookup<T>)`/`createBlockAndItemTags(...)` all present and match the research file's shapes. `DataGenerator.addProvider(boolean run, DataProvider.Factory<T> factory)` (the Forge-style client/server-gated overload used by the source's `event.includeClient()`/`includeServer()` calls) is **still present unchanged** in 1.21.1/NeoForge 21.1.248 — so the later provider-registration tasks can port the source's `generator.addProvider(event.includeClient(), ...)` lines close to verbatim.

## Verification

`./gradlew compileJava` clean. `./gradlew runServer` booted to `Done (2.191s)!`, RCON up, no datagen-related errors (the only warnings present — 5 entities missing `RegisterSpawnPlacementsEvent` registration — are pre-existing and unrelated to this change).

## What's next

Task 3 per the 10-task order: block state/model provider (`ModBlockStateProvider`/`ModItemProvider` from source, using NeoForge's `net.neoforged.neoforge.client.model.generators.BlockStateProvider`/`ItemModelProvider`). **User's "do task N then stop" pattern applies again — do not start task 3 without being asked**, same as the Phase 1→2 and task 1→2 boundaries (see [[feedback-stop-after-each-task]]).
