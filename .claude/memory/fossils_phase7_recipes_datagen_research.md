---
name: fossils-phase7-recipes-datagen-research
description: "Phase 7 (Recipes & datagen) API research complete 2026-08-14: confirmed 1.21.1 Recipe/RecipeSerializer, NeoForge datagen provider, and plant_loader geometry-loader API shapes — NO implementation done yet, research only"
metadata:
  type: project
---

Research-only checkpoint for [[fossils-migration-plan]] Phase 7, done 2026-08-14 per explicit user instruction to finish research and stop before writing any code. Nothing in this file has been implemented in the target repo yet — see "What's actually done" at the bottom before assuming otherwise.

**Why this file exists:** Phase 7 needs a real API rewrite (not a mechanical port) for both the recipe system and the entire datagen module, since MC 1.21.1 replaced JSON/FriendlyByteBuf-based `RecipeSerializer`s with a codec-based system, and NeoForge's datagen providers now thread `CompletableFuture<HolderLookup.Provider>` through most constructors. All API shapes below were confirmed by reading actual decompiled/source jars in `~/.gradle/caches` (this project's exact toolchain: `minecraft_version=1.21.1`, `neo_version=21.1.248`) — nothing guessed.

## Scope found during scoping (2026-08-14)

- `CustomizeToDinoFunction` loot function (`src/main/java/com/github/fossil/loot/`) is **already ported** with the correct `MapCodec`/`RecordCodecBuilder` pattern — not a Phase 7 gap, cross it off.
- Recipe framework (`recipe/` package) does not exist at all yet in the target repo.
- Datagen (`GatherDataEvent` handler, any provider) does not exist at all yet in the target repo.
- Explicitly Phase 9 scope, not Phase 7: `FossilCreateRecipeProvider`, `FossilFarmersRecipeProvider`, `PFaunaTagsProvider` (compat-integration datagen).

## 1. Recipe API (1.21.1) — confirmed via decompiled MC source

Source: `~/.gradle/caches/neoformruntime/intermediate_results/sourcesAndCompiledWithNeoForge_*_output.jar!/net/minecraft/world/item/crafting/*.java`

- `Recipe<T extends RecipeInput>` — generic bound changed from `Container` to new marker interface `RecipeInput` (`getItem(int)`, `size()`, default `isEmpty()`). **`getId()` is gone entirely.**
- `assemble(T input, HolderLookup.Provider registries)` — was `RegistryAccess`, now `HolderLookup.Provider`. Same for `getResultItem(HolderLookup.Provider)`.
- `RecipeSerializer<T>`: two methods only — `MapCodec<T> codec()` and `StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()`. No more `fromJson`/`fromNetwork`/`toNetwork(ResourceLocation, ...)`.
- `RecipeHolder<T>` (record: `id`, `value`) is the new id+recipe pairing. `RecipeManager.getRecipeFor(...)` / `getAllRecipesFor(...)` / `byKey(...)` all return `Optional<RecipeHolder<T>>` / `List<RecipeHolder<T>>` now, not bare `T`. Get the recipe object via `.value()`, id via `.id()`.
- `Ingredient.CODEC` / `Ingredient.CODEC_NONEMPTY` (JSON, unchanged concept) + new `Ingredient.CONTENTS_STREAM_CODEC` (network — replaces old `fromNetwork`/`toNetwork(FriendlyByteBuf)`, used as `.decode(buf)`/`.encode(buf, ingredient)`).
- `RecipeType<T>` unchanged in shape — still a registered marker singleton via `Registry.register(BuiltInRegistries.RECIPE_TYPE, ...)`.
- New `SingleRecipeInput(ItemStack item) implements RecipeInput` is vanilla's simplest concrete `RecipeInput` — template for single-input machine recipes. For this mod's multi-slot recipes (Analyzer/Sifter/CultureVat/Worktable), define a custom `record XInput(...) implements RecipeInput` per recipe type (or one shared one if slot shapes match).
- **Best templates to copy the shape from**: `ShapelessRecipe.Serializer` (multi-ingredient, full `MapCodec` via `RecordCodecBuilder.mapCodec` + `StreamCodec.of(encoder, decoder)` pair) and `SimpleCookingSerializer<T extends AbstractCookingRecipe>` (single-ingredient, closer to this mod's per-recipe-type serializer shape). Both in `net/minecraft/world/item/crafting/`.

**Porting implication for `MultiOutputAndSlotsRecipe`/`WithFuelRecipe`** (the two source base classes): each concrete recipe type needs (a) its own `RecipeInput` record matching its slot count, (b) a `MapCodec` built via `RecordCodecBuilder.mapCodec(...)` mirroring the old `fromJson` field reads, (c) a `StreamCodec` built via `StreamCodec.of(toNetwork, fromNetwork)` or `StreamCodec.composite(...)` mirroring the old network read/write order. The weighted-output `NavigableMap<Double, ItemStack>` shape (used by Analyzer/Sifter) has no direct vanilla codec — needs a hand-written `Codec`/`StreamCodec` for that map shape (e.g. a list of `{weight, item}` pairs converted to/from the `TreeMap`).

## 2. NeoForge datagen provider API (1.21.1 / NeoForge 21.1.248) — confirmed via NeoForge sources jar

Source: `~/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/21.1.248/.../neoforge-21.1.248-sources.jar`

- **Entry point**: `net.neoforged.neoforge.data.event.GatherDataEvent` — mod-bus event, unchanged conceptually from Forge. Key methods: `getGenerator()` → `DataGenerator`, `getExistingFileHelper()`, `getLookupProvider()` → `CompletableFuture<HolderLookup.Provider>`, `addProvider(T)`, `createProvider(DataProviderFromOutputLookup<T>)` (convenience that auto-passes output+lookup), `createBlockAndItemTags(...)` (convenience for the common block-tags→item-tags wiring).
- **Block states/models**: `net.neoforged.neoforge.client.model.generators.BlockStateProvider`/`BlockModelProvider`/`ItemModelProvider`/`ModelProvider<T>` — package survived **completely intact** from Forge, just re-rooted under `net.neoforged.neoforge`. `BlockStateProvider(PackOutput, String modid, ExistingFileHelper)` constructor unchanged. `ItemModelProvider` is its own class (not folded into BlockStateProvider, though `BlockStateProvider.itemModels()` exposes a convenience instance).
- **Tags**: `net.minecraft.data.tags.TagsProvider`/`ItemTagsProvider`/`EntityTypeTagsProvider` (vanilla) + `net.neoforged.neoforge.common.data.BlockTagsProvider` (NeoForge convenience wrapper — extend this one for blocks, not vanilla's raw `IntrinsicHolderTagsProvider`). All take `CompletableFuture<HolderLookup.Provider>` now. `ItemTagsProvider` additionally needs `CompletableFuture<TagsProvider.TagLookup<Block>>` (get via `blockTagsProviderInstance.contentsGetter()`) to support `copy(blockTag, itemTag)`.
- **Recipes**: `net.minecraft.data.recipes.RecipeProvider` — override `buildRecipes(RecipeOutput)` (or the 2-arg `buildRecipes(RecipeOutput, HolderLookup.Provider)` overload). `RecipeOutput` replaces the old `Consumer<FinishedRecipe>` — has `.accept(id, recipe, advancement)` and `.advancement()`.
- **Loot tables**: `net.minecraft.data.loot.LootTableProvider(PackOutput, Set<ResourceKey<LootTable>>, List<SubProviderEntry>, CompletableFuture<HolderLookup.Provider>)` — no NeoForge-specific wrapper exists here, use vanilla's class directly. `SubProviderEntry` is a record: `(Function<HolderLookup.Provider, LootTableSubProvider> provider, LootContextParamSet paramSet)`. Each of `ModBlockLootTables`/`ModEntityLootTables`/`ModGenericLootTables` implements `LootTableSubProvider` (interface unchanged: `generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder>)`).
- **Advancements**: **use `net.neoforged.neoforge.common.data.AdvancementProvider`, not vanilla's** `net.minecraft.data.advancements.AdvancementProvider` (vanilla's is `@Deprecated` in favor of the NeoForge one specifically for the `ExistingFileHelper` access it provides). Constructor: `(PackOutput, CompletableFuture<HolderLookup.Provider>, ExistingFileHelper, List<AdvancementGenerator>)`. `FossilAdvancements` should implement the nested `AdvancementProvider.AdvancementGenerator` interface (`generate(HolderLookup.Provider, Consumer<AdvancementHolder>, ExistingFileHelper)`).
- **General pattern**: every registry-touching provider constructor now takes `CompletableFuture<HolderLookup.Provider>` sourced from `event.getLookupProvider()`; `PackOutput`/`ExistingFileHelper` come from `event.getGenerator().getPackOutput()`/`event.getExistingFileHelper()`.

## 3. `fossil:plant_loader` custom geometry loader — confirmed via NeoForge sources jar + source repo read

Source repo files to port (all under `~/Personal/FossilsArcheologyRevival`): `forge/.../forge/client/model/{PlantModelLoader,PlantModelGeometry,PlantBakedModel}.java` (Forge-specific, use as the porting seed per this project's "seed from Forge side" convention) + `common/.../client/model/block/{PlantBlockModel,PlantModelBakery}.java` (loader-agnostic JSON/baking logic, port as-is).

**Why it exists**: vanilla `BlockElement` only supports single-axis rotation from a fixed pivot within the 0–16 cube. The plant/figurine JSONs need arbitrary 3-axis Euler rotation and out-of-bounds coordinates (confirmed in `zamites.json`, e.g. coordinates like `43.999`, `-25.97`) to build organic branching geometry — impossible to express as a normal block model. `PlantBlockModel`'s custom deserializer reads a `"parts"` key (deliberately not `"elements"`, so vanilla's deserializer doesn't choke on it first) with per-part `from`/`to`/`origin`/3-axis `rotation`/`faces`.

**Confirmed 23 models** in `src/main/resources/assets/fossil/models/block/` using `"loader": "fossil:plant_loader"`: `figurine_{enderman,skeleton,piglin,zombie,anu,steve}_{pristine,restored,destroyed}.json` (18) + `zamites.json`, `dipteris.json`, `tempskya_leaf.json`, `tempskya_top.json`, `mutant_tree_tumor.json` (5) = 23.

**1.21.1 API — near-1:1 package rename from Forge**:
- `IGeometryLoader<T extends IUnbakedGeometry<T>>` / `IUnbakedGeometry<T>` now live at `net.neoforged.neoforge.client.model.geometry` (was `net.minecraftforge.client.model.geometry`). `read(JsonObject, JsonDeserializationContext)` unchanged.
- **Real signature change**: `IUnbakedGeometry.bake(...)` **dropped its trailing `ResourceLocation location` param** — NeoForge 1.21.1 has 5 params ending at `ItemOverrides overrides` (Forge 1.20.1 had 6, ending at `..., ResourceLocation location)`).
- **Registration change**: still `ModelEvent.RegisterGeometryLoaders` (mod-bus, client-only), but `event.register(...)` now takes a full `ResourceLocation` key instead of a bare `String` — e.g. NeoForge's own `ClientNeoForgeMod` does `event.register(ResourceLocation.fromNamespaceAndPath("neoforge", "elements"), ElementsModel.Loader.INSTANCE)`. Port as `event.register(ResourceLocation.fromNamespaceAndPath("fossil", "plant_loader"), new PlantModelLoader())`.
- `IGeometryBakingContext` is essentially identical to Forge's version (same method set, plus a NeoForge-added `getRenderTypeHint()`/`getRenderType(ResourceLocation)`).
- `IDynamicBakedModel` (`PlantBakedModel`'s interface) exists at the analogous NeoForge package with no confirmed signature changes — **not fully verified**, a quick read of `net/neoforged/neoforge/client/model/IDynamicBakedModel.java` (in the same sources jar) should happen before/during the actual port to close this gap.
- Best NeoForge-native template for the porting pattern (not required, but shows current idioms): `net.neoforged.neoforge.client.model.ElementsModel` — a built-in geometry loader that re-parses a custom elements-shaped JSON and bakes quads via `BlockModel.bakeFace(...)`, registered the same way.

## What's actually done vs. not (as of 2026-08-14)

**Done**: this research only. Zero files created or modified in the target repo for Phase 7.

**Not started**: everything in [[fossils-migration-plan]]'s Phase 7 task list — recipe framework, datagen entry point + all providers, `plant_loader`. See [[fossils-migration-plan]] for the full Phase 7 description and the 10-task order agreed with the user (recipe framework → datagen entry point → block state/model provider → item model + tag providers → loot providers → recipe provider → advancement provider → food value provider → worldgen provider → plant_loader).

**User's explicit instruction (2026-08-14, see [[feedback-plan-includes-research-stop-before-code]]):** finish research as part of planning, but do not write/edit any implementation files until given explicit go-ahead per task.
