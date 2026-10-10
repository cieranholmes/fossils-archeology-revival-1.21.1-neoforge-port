package com.github.fossil.data.providers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.PrehistoricPlantInfo;
import com.github.fossil.block.custom_blocks.AmphoraVaseBlock;
import com.github.fossil.block.custom_blocks.KylixVaseBlock;
import com.github.fossil.block.custom_blocks.VaseBlock;
import com.github.fossil.entity.prehistoric.base.EntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.VanillaEntityInfo;
import com.github.fossil.item.ModItems;
import com.github.fossil.item.ToyBallItem;
import com.github.fossil.item.ToyScratchingPostItem;
import com.github.fossil.item.ToyTetheredLogItem;
import com.github.fossil.recipe.AnalyzerRecipeBuilder;
import com.github.fossil.recipe.CultureVatRecipeBuilder;
import com.github.fossil.recipe.MultiOutputAndSlotsRecipeBuilder;
import com.github.fossil.recipe.SifterRecipeBuilder;
import com.github.fossil.recipe.WorktableRecipeBuilder;
import com.github.fossil.tags.ModItemTags;
import com.github.fossil.util.TimePeriod;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.github.fossil.block.ModBlocks.*;
import static com.github.fossil.entity.prehistoric.base.VanillaEntityInfo.*;
import static com.github.fossil.item.ModItems.*;

/**
 * Ported from the source mod's {@code ModRecipeProvider} - 1.21.1 dropped {@code Consumer<FinishedRecipe>}
 * in favor of {@link RecipeOutput}, and {@link RecipeProvider} now requires a
 * {@code CompletableFuture<HolderLookup.Provider>} in its constructor (unused here directly, only
 * threaded through to the super constructor). {@code generateRecipes(RecipeOutput, BlockFamily)} also
 * gained a trailing {@link FeatureFlagSet} parameter - passed {@code FeatureFlagSet.of(FeatureFlags.VANILLA)}
 * since none of this mod's block families are gated behind an experimental feature flag, mirroring vanilla's
 * own {@code VanillaRecipeProvider}. The Architectury {@code modId} constructor parameter on the
 * {@code analyzed}/{@code sifter}/{@code worktable}/{@code cultureVat} recipe builders is dropped (NeoForge-only,
 * see the {@code recipe} package's builders). {@code RegistrySupplier<? extends ItemLike>} overloads for
 * {@code worktable}/{@code cultureVat} are dropped entirely - NeoForge's {@code DeferredBlock}/{@code DeferredItem}
 * both implement {@code ItemLike} directly (confirmed in the tags-provider port), so a single
 * {@code ItemLike}-based helper covers every call site without needing a separate {@code .get()}.
 */
public class ModRecipeProvider extends RecipeProvider {
    public static final BlockFamily ANCIENT_WOOD_PLANKS = new BlockFamily.Builder(ModBlocks.ANCIENT_WOOD_PLANKS.get()).slab(ANCIENT_WOOD_SLAB.get()).stairs(ANCIENT_WOOD_STAIRS.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily CALAMITES_PLANKS = new BlockFamily.Builder(ModBlocks.CALAMITES_PLANKS.get()).button(CALAMITES_BUTTON.get()).fence(CALAMITES_FENCE.get()).fenceGate(CALAMITES_FENCE_GATE.get()).pressurePlate(CALAMITES_PRESSURE_PLATE.get()).slab(CALAMITES_SLAB.get()).stairs(CALAMITES_STAIRS.get()).door(CALAMITES_DOOR.get()).trapdoor(CALAMITES_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily CORDAITES_PLANKS = new BlockFamily.Builder(ModBlocks.CORDAITES_PLANKS.get()).button(CORDAITES_BUTTON.get()).fence(CORDAITES_FENCE.get()).fenceGate(CORDAITES_FENCE_GATE.get()).pressurePlate(CORDAITES_PRESSURE_PLATE.get()).slab(CORDAITES_SLAB.get()).stairs(CORDAITES_STAIRS.get()).door(CORDAITES_DOOR.get()).trapdoor(CORDAITES_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily MUTANT_TREE_PLANKS = new BlockFamily.Builder(ModBlocks.MUTANT_TREE_PLANKS.get()).button(MUTANT_TREE_BUTTON.get()).fence(MUTANT_TREE_FENCE.get()).fenceGate(MUTANT_TREE_FENCE_GATE.get()).pressurePlate(MUTANT_TREE_PRESSURE_PLATE.get()).slab(MUTANT_TREE_SLAB.get()).stairs(MUTANT_TREE_STAIRS.get()).door(MUTANT_TREE_DOOR.get()).trapdoor(MUTANT_TREE_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily PALM_PLANKS = new BlockFamily.Builder(ModBlocks.PALM_PLANKS.get()).button(PALM_BUTTON.get()).fence(PALM_FENCE.get()).fenceGate(PALM_FENCE_GATE.get()).pressurePlate(PALM_PRESSURE_PLATE.get()).slab(PALM_SLAB.get()).stairs(PALM_STAIRS.get()).door(PALM_DOOR.get()).trapdoor(PALM_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily SIGILLARIA_PLANKS = new BlockFamily.Builder(ModBlocks.SIGILLARIA_PLANKS.get()).button(SIGILLARIA_BUTTON.get()).fence(SIGILLARIA_FENCE.get()).fenceGate(SIGILLARIA_FENCE_GATE.get()).pressurePlate(SIGILLARIA_PRESSURE_PLATE.get()).slab(SIGILLARIA_SLAB.get()).stairs(SIGILLARIA_STAIRS.get()).door(SIGILLARIA_DOOR.get()).trapdoor(SIGILLARIA_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily TEMPSKYA_PLANKS = new BlockFamily.Builder(ModBlocks.TEMPSKYA_PLANKS.get()).button(TEMPSKYA_BUTTON.get()).fence(TEMPSKYA_FENCE.get()).fenceGate(TEMPSKYA_FENCE_GATE.get()).pressurePlate(TEMPSKYA_PRESSURE_PLATE.get()).slab(TEMPSKYA_SLAB.get()).stairs(TEMPSKYA_STAIRS.get()).door(TEMPSKYA_DOOR.get()).trapdoor(TEMPSKYA_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), enumMap -> {
        enumMap.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
        enumMap.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
        enumMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
        enumMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        enumMap.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
        enumMap.put(DyeColor.LIME, Blocks.LIME_WOOL);
        enumMap.put(DyeColor.PINK, Blocks.PINK_WOOL);
        enumMap.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
        enumMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        enumMap.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
        enumMap.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
        enumMap.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
        enumMap.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
        enumMap.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
        enumMap.put(DyeColor.RED, Blocks.RED_WOOL);
        enumMap.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
    });

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        boolean cookingRecipes = true;
        boolean craftingRecipes = true;
        boolean machineRecipes = true;
        if (cookingRecipes) {
            for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
                if (info.foodItem != null && info.cookedFoodItem != null) {
                    fullCooking(RecipeCategory.FOOD, info.foodItem, info.cookedFoodItem, recipeOutput, 1.5f);
                }
            }
            fullOre(RecipeCategory.BUILDING_BLOCKS, DENSE_SAND.get(), REINFORCED_GLASS.get(), recipeOutput, 3);
        }
        if (craftingRecipes) {
            ToyBallItem white = TOY_BALLS.get(DyeColor.WHITE).get();
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, white).define('W', Blocks.WHITE_WOOL).define('S', Items.STRING)
                    .define('B', Items.SLIME_BALL).pattern("SWS").pattern("WBW").pattern("SWS").unlockedBy("has_wool",
                            has(ItemTags.WOOL)).save(recipeOutput, RecipeBuilder.getDefaultRecipeId(white));
            for (Map.Entry<DyeColor, DeferredItem<ToyBallItem>> entry : TOY_BALLS.entrySet()) {
                DyeColor color = entry.getKey();
                ToyBallItem ball = entry.getValue().get();
                if (color == DyeColor.WHITE) continue;
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ball).requires(colorToDye(color)).requires(white).unlockedBy("has_ball",
                        has(white)).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":toy_ball_white_to_" + color.getName());
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, white).requires(Items.WHITE_DYE).requires(ball).unlockedBy("has_ball",
                        has(white)).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":toy_ball_" + color.getName() + "_to_white");
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ball).define('W', ITEM_BY_DYE.get(entry.getKey())).define('S', Items.STRING)
                        .define('B', Items.SLIME_BALL).pattern("SWS").pattern("WBW").pattern("SWS").unlockedBy("has_wool",
                                has(ItemTags.WOOL)).save(recipeOutput, RecipeBuilder.getDefaultRecipeId(ball));
            }
            for (Map.Entry<String, DeferredItem<ToyTetheredLogItem>> entry : TOY_TETHERED_LOGS.entrySet()) {
                var block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse("minecraft:" + entry.getKey() + "_log"));
                block.ifPresent(log -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.getValue().get()).define('S', Items.STRING).define('L', log)
                        .pattern("S").pattern("S").pattern("L").unlockedBy("has_log", has(log)).save(recipeOutput));
            }
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TOY_TETHERED_LOGS.get(WoodType.CRIMSON.name()).get()).define('S', Items.STRING)
                    .define('L', Blocks.CRIMSON_STEM).pattern("S").pattern("S").pattern("L").unlockedBy("has_log",
                            has(Blocks.CRIMSON_STEM)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TOY_TETHERED_LOGS.get(WoodType.WARPED.name()).get()).define('S', Items.STRING)
                    .define('L', Blocks.WARPED_STEM).pattern("S").pattern("S").pattern("L").unlockedBy("has_log",
                            has(Blocks.WARPED_STEM)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TOY_TETHERED_LOGS.get(WoodType.BAMBOO.name()).get()).define('S', Items.STRING)
                    .define('L', Blocks.BAMBOO_BLOCK).pattern("S").pattern("S").pattern("L").unlockedBy("has_log",
                            has(Blocks.BAMBOO_BLOCK)).save(recipeOutput);
            for (Map.Entry<String, DeferredItem<ToyScratchingPostItem>> entry : TOY_SCRATCHING_POSTS.entrySet()) {
                var block = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse("minecraft:" + entry.getKey() + "_slab"));
                block.ifPresent(slab -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, entry.getValue().get()).define('S', Items.STICK).define('X', slab)
                        .define('W', ItemTags.WOOL).pattern("WWW").pattern("WSW").pattern(" X ").unlockedBy("has_slab",
                                has(slab)).save(recipeOutput));
            }
            var hasScarabGem = has(SCARAB_GEM.get());
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AQUATIC_SCARAB_GEM.get()).requires(SCARAB_GEM.get()).requires(AMBER_CHUNK_DOMINICAN.get())
                    .unlockedBy("has_scarab_gem", hasScarabGem).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, AMBER_BLOCK.get()).define('A', AMBER_CHUNK.get())
                    .pattern("AAA").pattern("AAA").pattern("AAA").unlockedBy("has_amber_chunk", has(AMBER_CHUNK.get())).save(recipeOutput);
            smithing(Items.DIAMOND_AXE, SCARAB_GEM.get(), SCARAB_AXE.get(), RecipeCategory.TOOLS, recipeOutput);
            smithing(Items.DIAMOND_HOE, SCARAB_GEM.get(), SCARAB_HOE.get(), RecipeCategory.TOOLS, recipeOutput);
            smithing(Items.DIAMOND_PICKAXE, SCARAB_GEM.get(), SCARAB_PICKAXE.get(), RecipeCategory.TOOLS, recipeOutput);
            smithing(Items.DIAMOND_SHOVEL, SCARAB_GEM.get(), SCARAB_SHOVEL.get(), RecipeCategory.TOOLS, recipeOutput);
            smithing(Items.DIAMOND_SWORD, SCARAB_GEM.get(), SCARAB_SWORD.get(), RecipeCategory.COMBAT, recipeOutput);

            var bonesLeg = ModItemTags.LEG_BONES;
            var bonesFoot = ModItemTags.FOOT_BONES;
            var bonesVertebrae = ModItemTags.VERTEBRAE_BONES;
            var bonesRibcage = ModItemTags.RIBCAGE_BONES;
            var bonesSkull = ModItemTags.SKULL_BONES;
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BONE_BOOTS.get()).define('L', bonesLeg).define('F', bonesFoot).pattern("L L").pattern("F F").unlockedBy("has_bones", has(bonesFoot)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BONE_CHESTPLATE.get()).define('B', Items.BONE).define('V', bonesVertebrae).define('R', bonesRibcage).pattern("B B").pattern(" V ").pattern("BRB").unlockedBy("has_bones", has(bonesRibcage)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BONE_HELMET.get()).define('B', Items.BONE).define('S', bonesSkull).pattern("BSB").pattern("B B").unlockedBy("has_bones", has(bonesSkull)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BONE_LEGGINGS.get()).define('B', Items.BONE).define('L', bonesLeg).pattern("BBB").pattern("L L").pattern("B B").unlockedBy("has_bones", has(bonesLeg)).save(recipeOutput);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL).requires(VOLCANIC_ASH.get(), 4).unlockedBy("has_volcanic_ash", has(VOLCANIC_ASH.get())).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":bone_meal_from_ash");
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL).requires(ModItemTags.ALL_BONES).unlockedBy("has_bone", has(ModItemTags.ALL_BONES)).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":bone_meal_from_bone");
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CHICKEN_ESSENCE.get(), 8).define('G', Items.GLASS_BOTTLE).define('C', COOKED_CHICKEN_SOUP.get()).pattern("GGG").pattern("GCG").pattern("GGG").unlockedBy("has_cooked_chicken_soup", has(COOKED_CHICKEN_SOUP.get())).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, STUNTED_ESSENCE.get()).define('P', Items.POISONOUS_POTATO).define('C', CHICKEN_ESSENCE.get()).pattern(" P ").pattern("PCP").pattern(" P ").unlockedBy("has_chicken_essence", has(CHICKEN_ESSENCE.get())).save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ANALYZER.get()).define('I', Items.IRON_INGOT).define('R', RELIC_SCRAP.get()).define('B', ModItemTags.FOSSILS).pattern("IRI").pattern("IBI").unlockedBy("has_fossil", has(ModItemTags.FOSSILS)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BUBBLE_BLOWER.get()).define('I', Items.GOLD_INGOT).define('N', Items.GOLD_NUGGET).define('W', Items.WATER_BUCKET).pattern("NIN").pattern("IWI").pattern("NIN").unlockedBy("has_dino_egg", has(ModItemTags.DINO_EGGS)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, SIFTER.get()).define('I', Blocks.IRON_BARS).define('S', Items.STRING).define('P', ItemTags.PLANKS).pattern("SPS").pattern("PIP").pattern("PSP").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WORKTABLE.get()).define('L', Items.LEATHER).define('C', Blocks.CRAFTING_TABLE).pattern("L").pattern("C").unlockedBy("has_crafting_table", has(Blocks.CRAFTING_TABLE)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, DRUM.get()).define('L', Items.LEATHER).define('R', Items.REDSTONE).define('P', ItemTags.PLANKS).pattern("LLL").pattern("PRP").pattern("PPP").unlockedBy("has_crafting_table", has(Blocks.CRAFTING_TABLE)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, CULTURE_VAT.get()).define('G', Tags.Items.GLASS_BLOCKS).define('S', Tags.Items.SLIME_BALLS).define('W', Items.WATER_BUCKET).define('I', Items.IRON_INGOT).pattern("GSG").pattern("GWG").pattern("III").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FEEDER.get()).define('I', Items.IRON_INGOT).define('G', Tags.Items.GLASS_BLOCKS).define('B', Items.STONE_BUTTON).define('W', Items.BUCKET).define('S', Blocks.STONE).pattern("IGI").pattern("BWS").pattern("SSS").unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, DINOPEDIA.get()).requires(Items.BOOK).requires(ModItemTags.FOSSILS).unlockedBy("has_bio_fossil", has(BIO_FOSSIL.get())).save(recipeOutput);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, RAW_CHICKEN_SOUP.get()).requires(Items.BUCKET).requires(Items.CHICKEN).unlockedBy("has_chicken", has(Items.CHICKEN)).save(recipeOutput);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BIO_GOO.get()).requires(ModItemTags.ALL_EGGS).requires(Items.WHEAT).requires(Items.ROTTEN_FLESH).requires(Items.MILK_BUCKET).unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH)).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":bio_goo_from_rotten_flesh");
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BIO_GOO.get(), 4).requires(ModItemTags.ALL_EGGS).requires(Items.WHEAT).requires(FAILURESAURUS_FLESH.get()).requires(Items.MILK_BUCKET).unlockedBy("has_failuresaurus_flesh", has(FAILURESAURUS_FLESH.get())).save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":bio_goo_from_failuresaurus_flesh");
            ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, SKULL_STICK.get()).requires(Items.STICK).requires(SKULL_BLOCK.get()).unlockedBy("has_skull_block", has(SKULL_BLOCK.get())).save(recipeOutput);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, TOOTH_DAGGER.get()).requires(Items.STICK).requires(PrehistoricEntityInfo.TYRANNOSAURUS.uniqueBoneItem).unlockedBy("has_tooth", has(PrehistoricEntityInfo.TYRANNOSAURUS.uniqueBoneItem)).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, WHIP.get()).define('S', Items.STRING).define('T', Items.STICK).pattern("  S").pattern(" TS").pattern("T S").unlockedBy("has_dinopedia", has(DINOPEDIA.get())).save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, AMPHORA_VASE_DAMAGED.get()).define('P', POTTERY_SHARD.get()).pattern("PP").pattern("PP").pattern("PP").unlockedBy("has_pottery_shard", has(POTTERY_SHARD.get())).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, KYLIX_VASE_DAMAGED.get()).define('P', POTTERY_SHARD.get()).pattern("PPP").pattern(" P ").unlockedBy("has_pottery_shard", has(POTTERY_SHARD.get())).save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, VOLUTE_VASE_DAMAGED.get()).define('P', POTTERY_SHARD.get()).pattern("P P").pattern("P P").pattern("PPP").unlockedBy("has_pottery_shard", has(POTTERY_SHARD.get())).save(recipeOutput);
            for (Pair<DyeColor, DeferredBlock<VaseBlock>> pair : VASES_WITH_COLOR) {
                VaseBlock vase = pair.getSecond().get();
                VaseBlock restored;
                if (vase instanceof AmphoraVaseBlock) {
                    restored = AMPHORA_VASE_RESTORED.get();
                } else if (vase instanceof KylixVaseBlock) {
                    restored = KYLIX_VASE_RESTORED.get();
                } else {
                    restored = VOLUTE_VASE_RESTORED.get();
                }
                ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, vase).requires(restored).requires(DyeItem.byColor(pair.getFirst())).unlockedBy("has_restored_vase", has(restored)).save(recipeOutput);
            }

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, DENSE_SAND.get()).requires(Blocks.SAND).requires(Items.QUARTZ).unlockedBy("has_sand", has(Blocks.SAND)).save(recipeOutput);
            generateRecipes(recipeOutput, ANCIENT_WOOD_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, CALAMITES_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, CORDAITES_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, MUTANT_TREE_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, PALM_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, SIGILLARIA_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            generateRecipes(recipeOutput, TEMPSKYA_PLANKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
            planksFromLogs(recipeOutput, ModBlocks.ANCIENT_WOOD_PLANKS.get(), ModItemTags.ANCIENT_WOOD_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.CALAMITES_PLANKS.get(), ModItemTags.CALAMITES_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.CORDAITES_PLANKS.get(), ModItemTags.CORDAITES_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.MUTANT_TREE_PLANKS.get(), ModItemTags.MUTANT_TREE_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.PALM_PLANKS.get(), ModItemTags.PALM_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.SIGILLARIA_PLANKS.get(), ModItemTags.SIGILLARIA_LOGS, 4);
            planksFromLogs(recipeOutput, ModBlocks.TEMPSKYA_PLANKS.get(), ModItemTags.TEMPSKYA_LOGS, 4);
            woodFromLogs(recipeOutput, CALAMITES_WOOD.get(), CALAMITES_LOG.get());
            woodFromLogs(recipeOutput, CORDAITES_WOOD.get(), CORDAITES_LOG.get());
            woodFromLogs(recipeOutput, MUTANT_TREE_WOOD.get(), MUTANT_TREE_LOG.get());
            woodFromLogs(recipeOutput, PALM_WOOD.get(), PALM_LOG.get());
            woodFromLogs(recipeOutput, SIGILLARIA_WOOD.get(), SIGILLARIA_LOG.get());
            woodFromLogs(recipeOutput, TEMPSKYA_WOOD.get(), TEMPSKYA_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_CALAMITES_WOOD.get(), STRIPPED_CALAMITES_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_CORDAITES_WOOD.get(), STRIPPED_CORDAITES_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_MUTANT_TREE_WOOD.get(), STRIPPED_MUTANT_TREE_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_PALM_WOOD.get(), STRIPPED_PALM_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_SIGILLARIA_WOOD.get(), STRIPPED_SIGILLARIA_LOG.get());
            woodFromLogs(recipeOutput, STRIPPED_TEMPSKYA_WOOD.get(), STRIPPED_TEMPSKYA_LOG.get());

            stonecutter(recipeOutput, ANCIENT_STONE_SLAB.get(), ANCIENT_STONE.get(), 2);
            stonecutter(recipeOutput, ANCIENT_STONE_STAIRS.get(), ANCIENT_STONE.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, ANCIENT_STONE_WALL.get(), ANCIENT_STONE.get(), 1);
            stonecutter(recipeOutput, ANCIENT_STONE_BRICKS.get(), ANCIENT_STONE.get(), 1);
            stonecutter(recipeOutput, ANCIENT_STONE_SLAB.get(), ANCIENT_STONE_BRICKS.get(), 2);
            stonecutter(recipeOutput, ANCIENT_STONE_STAIRS.get(), ANCIENT_STONE_BRICKS.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, ANCIENT_STONE_WALL.get(), ANCIENT_STONE_BRICKS.get(), 1);
            stonecutter(recipeOutput, VOLCANIC_BRICK_SLAB.get(), VOLCANIC_ROCK.get(), 2);
            stonecutter(recipeOutput, VOLCANIC_BRICK_STAIRS.get(), VOLCANIC_ROCK.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, VOLCANIC_BRICK_WALL.get(), VOLCANIC_ROCK.get(), 1);
            stonecutter(recipeOutput, VOLCANIC_BRICKS.get(), VOLCANIC_ROCK.get(), 1);
            stonecutter(recipeOutput, VOLCANIC_BRICK_SLAB.get(), VOLCANIC_BRICKS.get(), 2);
            stonecutter(recipeOutput, VOLCANIC_BRICK_STAIRS.get(), VOLCANIC_BRICKS.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, VOLCANIC_BRICK_WALL.get(), VOLCANIC_BRICKS.get(), 1);

            stonecutter(recipeOutput, VOLCANIC_TILE_SLAB.get(), VOLCANIC_BRICKS.get(), 2);
            stonecutter(recipeOutput, VOLCANIC_TILE_STAIRS.get(), VOLCANIC_BRICKS.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, VOLCANIC_TILE_WALL.get(), VOLCANIC_BRICKS.get(), 1);
            stonecutter(recipeOutput, VOLCANIC_TILES.get(), VOLCANIC_BRICKS.get(), 1);
            stonecutter(recipeOutput, VOLCANIC_TILE_SLAB.get(), VOLCANIC_TILES.get(), 2);
            stonecutter(recipeOutput, VOLCANIC_TILE_STAIRS.get(), VOLCANIC_TILES.get(), 1);
            stonecutter(RecipeCategory.DECORATIONS, recipeOutput, VOLCANIC_TILE_WALL.get(), VOLCANIC_TILES.get(), 1);
        }

        if (machineRecipes) {
            //with 19 plant seeds at 1% each
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> plantFossil = analyzed(PLANT_FOSSIL.get())
                    .addOutput(Blocks.SAND, 25)
                    .addOutput(Blocks.CACTUS, 20)
                    .addOutput(Blocks.POPPY, 1.5)
                    .addOutput(Blocks.ALLIUM, 1.5)
                    .addOutput(Blocks.DANDELION, 1.5)
                    .addOutput(Blocks.OXEYE_DAISY, 1.5)
                    .addOutput(Blocks.CORNFLOWER, 1.5)
                    .addOutput(Blocks.AZURE_BLUET, 1.5)
                    .addOutput(Blocks.FERN, 1.5)
                    .addOutput(Blocks.SHORT_GRASS, 1.5)
                    .addOutput(Blocks.DEAD_BUSH, 3)
                    .addOutput(Blocks.RED_MUSHROOM, 3)
                    .addOutput(Blocks.BROWN_MUSHROOM, 3)
                    .addOutput(FERN_SEED_FOSSIL.get(), 5)
                    .addOutput(CALAMITES_FOSSIL_SAPLING.get(), 2)
                    .addOutput(CORDAITES_FOSSIL_SAPLING.get(), 2)
                    .addOutput(PALM_FOSSIL_SAPLING.get(), 2)
                    .addOutput(SIGILLARIA_FOSSIL_SAPLING.get(), 2)
                    .addOutput(TEMPSKYA_FOSSIL_SAPLING.get(), 2);

            double seedWeight = (100F - plantFossil.getTotal()) / (double) PrehistoricPlantInfo.plantsWithSeeds().size();
            for (PrehistoricPlantInfo info : PrehistoricPlantInfo.plantsWithSeeds()) {
                plantFossil.addOutput(info.getFossilizedPlantSeedItem(), seedWeight);
            }
            plantFossil.save(recipeOutput);
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> bioFossil = analyzed(BIO_FOSSIL.get())
                    .addOutput(Blocks.SAND, 35)
                    .addOutput(Items.BONE_MEAL, 50);
            List<PrehistoricEntityInfo> bioFossilEntityList = PrehistoricEntityInfo.getTimePeriodList(TimePeriod.MESOZOIC);
            double bioFossilDNAChance = 15F / (double) bioFossilEntityList.size();
            for (PrehistoricEntityInfo info : bioFossilEntityList) {
                bioFossil.addOutput(info.dnaItem, bioFossilDNAChance);
            }
            bioFossil.save(recipeOutput);
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> shaleFossil = analyzed(SHALE_FOSSIL.get())
                    .addOutput(Blocks.TUFF, 35)
                    .addOutput(Items.BONE_MEAL, 50);
            List<PrehistoricEntityInfo> shaleFossilEntityList = PrehistoricEntityInfo.getTimePeriodList(TimePeriod.PALEOZOIC);
            double shaleFossilDNAChance = 15F / (double) shaleFossilEntityList.size();
            for (PrehistoricEntityInfo info : shaleFossilEntityList) {
                shaleFossil.addOutput(info.dnaItem, shaleFossilDNAChance);
            }
            shaleFossil.save(recipeOutput);
            analyzed(TAR_DROP.get())
                    .addOutput(Items.COAL, 20)
                    .addOutput(Items.CHARCOAL, 20)
                    .addOutput(TAR_FOSSIL.get(), 45)
                    .addOutput(VOLCANIC_ROCK.get(), 15).save(recipeOutput);
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> tarFossil = analyzed(TAR_FOSSIL.get())
                    .addOutput(Items.BONE_MEAL, 15)
                    .addOutput(VOLCANIC_ROCK.get(), 30);
            List<EntityInfo> tarFossilEntityList = new ArrayList<>(PrehistoricEntityInfo.getTimePeriodList(TimePeriod.CENOZOIC));
            tarFossilEntityList.add(AXOLOTL);
            tarFossilEntityList.add(BAT);
            tarFossilEntityList.add(CAT);
            tarFossilEntityList.add(DOLPHIN);
            tarFossilEntityList.add(FOX);
            tarFossilEntityList.add(OCELOT);
            tarFossilEntityList.add(PANDA);
            tarFossilEntityList.add(WOLF);
            tarFossilEntityList.add(TURTLE);
            tarFossilEntityList.add(CAMEL);
            double tarFossilDNAChance = 25 / (double) tarFossilEntityList.size();
            for (EntityInfo info : tarFossilEntityList) {
                tarFossil.addOutput(info.dnaItem(), tarFossilDNAChance);
            }
            tarFossil.save(recipeOutput);

            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> failuresaurusFlesh = analyzed(FAILURESAURUS_FLESH.get())
                    .addOutput(Items.ROTTEN_FLESH, 33);
            double failuresaurusDNAChance = 67F / (PrehistoricEntityInfo.values().length + VanillaEntityInfo.values().length);
            for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
                failuresaurusFlesh.addOutput(info.dnaItem, failuresaurusDNAChance);
            }
            for (VanillaEntityInfo info : values()) {
                failuresaurusFlesh.addOutput(info.dnaItem, failuresaurusDNAChance);
            }
            failuresaurusFlesh.save(recipeOutput);
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> frozenMeat = analyzed(FROZEN_MEAT.get())
                    .addOutput(Items.CHICKEN, 15)
                    .addOutput(Items.MUTTON, 15)
                    .addOutput(Items.BEEF, 15)
                    .addOutput(Items.PORKCHOP, 15)
                    .addOutput(Items.CHICKEN, 15)
                    .addOutput(TAR_FOSSIL.get(), 20);
            for (EntityInfo info : tarFossilEntityList) {
                frozenMeat.addOutput(info.dnaItem(), tarFossilDNAChance);
            }
            frozenMeat.save(recipeOutput);
            analyzed(AMBER_CHUNK_DOMINICAN.get()).addOutput(Items.SPIDER_EYE, 9).addOutput(Items.STRING, 10).addOutput(Blocks.DIRT, 25).addOutput(Blocks.GRAVEL, 25)
                    .addOutput(Items.WHEAT_SEEDS, 1).addOutput(Items.BEETROOT_SEEDS, 1).addOutput(Items.PUMPKIN_SEEDS, 1).addOutput(Items.MELON_SEEDS, 1)
                    .addOutput(CALAMITES_FOSSIL_SAPLING.get(), 1).addOutput(CORDAITES_FOSSIL_SAPLING.get(), 1).addOutput(PALM_FOSSIL_SAPLING.get(), 1).addOutput(SIGILLARIA_FOSSIL_SAPLING.get(), 1).addOutput(TEMPSKYA_FOSSIL_SAPLING.get(), 1).save(recipeOutput);
            MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> amberChunkMosquito = analyzed(AMBER_CHUNK_MOSQUITO.get())
                    .addOutput(Items.SPIDER_EYE, 10);
            List<PrehistoricEntityInfo> amberChunkMosquitoEntityList = PrehistoricEntityInfo.getTimePeriodList(TimePeriod.MESOZOIC);
            double amberChunkMosquitoDNAChance = 90F / (double) amberChunkMosquitoEntityList.size();
            for (PrehistoricEntityInfo info : amberChunkMosquitoEntityList) {
                amberChunkMosquito.addOutput(info.dnaItem, amberChunkMosquitoDNAChance);
            }
            amberChunkMosquito.save(recipeOutput);
            analyzed(Items.HONEYCOMB).addOutput(BEE.dnaItem, 100).save(recipeOutput);
            analyzed(Items.CHICKEN).addOutput(CHICKEN.dnaItem, 100).save(recipeOutput);
            analyzed(Items.EGG).addOutput(CHICKEN.dnaItem, 25).nothing(75).save(recipeOutput);
            analyzed(Items.FEATHER).addOutput(CHICKEN.dnaItem, 95).addOutput(PARROT.dnaItem, 5).save(recipeOutput);
            analyzed(Items.COD).addOutput(COD.dnaItem, 90).addOutput(POLAR_BEAR.dnaItem, 10).save(recipeOutput);
            analyzed(Items.BEEF).addOutput(COW.dnaItem, 99).addOutput(MOOSHROOM.dnaItem, 1).save(recipeOutput);
            analyzed(Items.LEATHER).addOutput(COW.dnaItem, 57).addOutput(DONKEY.dnaItem, 10).addOutput(HORSE.dnaItem, 27).addOutput(HOGLIN.dnaItem, 5).addOutput(MOOSHROOM.dnaItem, 1).save(recipeOutput);
            analyzed(Items.GLOW_INK_SAC).addOutput(GLOW_SQUID.dnaItem, 40).nothing(60).save(recipeOutput);
            analyzed(Items.GOAT_HORN).addOutput(GOAT.dnaItem, 100).save(recipeOutput);
            analyzed(Items.PORKCHOP).addOutput(PIG.dnaItem, 95).addOutput(HOGLIN.dnaItem, 5).save(recipeOutput);
            analyzed(Items.PUFFERFISH).addOutput(PUFFERFISH.dnaItem, 90).addOutput(POLAR_BEAR.dnaItem, 10).save(recipeOutput);
            analyzed(Items.RABBIT).addOutput(RABBIT.dnaItem, 100).save(recipeOutput);
            analyzed(Items.RABBIT_FOOT).addOutput(RABBIT.dnaItem, 100).save(recipeOutput);
            analyzed(Items.RABBIT_HIDE).addOutput(RABBIT.dnaItem, 100).save(recipeOutput);
            analyzed(Items.SALMON).addOutput(SALMON.dnaItem, 90).addOutput(POLAR_BEAR.dnaItem, 10).save(recipeOutput);
            analyzed(Items.MUTTON).addOutput(SHEEP.dnaItem, 100).save(recipeOutput);
            analyzed(ItemTags.WOOL).addOutput(Items.STRING, 3, 50).addOutput(SHEEP.dnaItem, 27).addOutput(LLAMA.dnaItem, 13).addOutput(GOAT.dnaItem, 10).save(recipeOutput);
            analyzed(Items.INK_SAC).addOutput(SQUID.dnaItem, 20).nothing(80).save(recipeOutput);
            analyzed(Items.STRING).addOutput(STRIDER.dnaItem, 5).nothing(95).save(recipeOutput);
            analyzed(Items.TROPICAL_FISH).addOutput(TROPICAL_FISH.dnaItem, 90).addOutput(POLAR_BEAR.dnaItem, 10).save(recipeOutput);
            analyzed(Items.SNIFFER_EGG).addOutput(SNIFFER.dnaItem, 100).save(recipeOutput);
            analyzed(Items.TURTLE_SCUTE).addOutput(TURTLE.dnaItem, 100).save(recipeOutput);
            analyzed(Items.TURTLE_EGG).addOutput(TURTLE.dnaItem, 100).save(recipeOutput);
            analyzed(Items.LILY_PAD).addOutput(FROG.dnaItem, 5).nothing(95).save(recipeOutput);

            analyzed(RELIC_SCRAP.get()).addOutput(Blocks.GRAVEL, 30).addOutput(Items.FLINT, 18).addOutput(POTTERY_SHARD.get(), 4).addOutput(BROKEN_HELMET.get(), 4).addOutput(BROKEN_SWORD.get(), 4).addOutput(STONE_TABLET.get(), 30)
                    .addOutput(ANU_FIGURINE_DESTROYED.get(), 4).addOutput(ENDERMAN_FIGURINE_DESTROYED.get(), 4).addOutput(PIGLIN_FIGURINE_DESTROYED.get(), 4).addOutput(SKELETON_FIGURINE_DESTROYED.get(), 4).addOutput(STEVE_FIGURINE_DESTROYED.get(), 4).addOutput(ZOMBIE_FIGURINE_DESTROYED.get(), 4).save(recipeOutput);

            worktable(AMPHORA_VASE_DAMAGED, AMPHORA_VASE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(VOLUTE_VASE_DAMAGED, VOLUTE_VASE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(KYLIX_VASE_DAMAGED, KYLIX_VASE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(ANU_FIGURINE_DESTROYED, ANU_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(ENDERMAN_FIGURINE_DESTROYED, ENDERMAN_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(PIGLIN_FIGURINE_DESTROYED, PIGLIN_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(SKELETON_FIGURINE_DESTROYED, SKELETON_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(STEVE_FIGURINE_DESTROYED, STEVE_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(ZOMBIE_FIGURINE_DESTROYED, ZOMBIE_FIGURINE_RESTORED, POTTERY_SHARD, recipeOutput);
            worktable(ANU_FIGURINE_RESTORED, ANU_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(ENDERMAN_FIGURINE_RESTORED, ENDERMAN_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(PIGLIN_FIGURINE_RESTORED, PIGLIN_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(SKELETON_FIGURINE_RESTORED, SKELETON_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(STEVE_FIGURINE_RESTORED, STEVE_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(ZOMBIE_FIGURINE_RESTORED, ZOMBIE_FIGURINE_PRISTINE, POTTERY_SHARD, recipeOutput);
            worktable(BROKEN_SWORD, ANCIENT_SWORD, RELIC_SCRAP, 3000, recipeOutput);
            worktable(BROKEN_HELMET, ANCIENT_HELMET, RELIC_SCRAP, 3000, recipeOutput);
            worktable(ANCIENT_SWORD, ANCIENT_SWORD, RELIC_SCRAP, 3000, recipeOutput);
            worktable(ANCIENT_HELMET, ANCIENT_HELMET, RELIC_SCRAP, 3000, recipeOutput);
            worktable(SCARAB_SWORD, SCARAB_SWORD, RELIC_SCRAP, recipeOutput);
            worktable(SCARAB_PICKAXE, SCARAB_PICKAXE, RELIC_SCRAP, recipeOutput);
            worktable(SCARAB_AXE, SCARAB_AXE, RELIC_SCRAP, recipeOutput);
            worktable(SCARAB_SHOVEL, SCARAB_SHOVEL, RELIC_SCRAP, recipeOutput);
            worktable(SCARAB_HOE, SCARAB_HOE, RELIC_SCRAP, recipeOutput);
            worktable(WOODEN_JAVELIN, WOODEN_JAVELIN, RELIC_SCRAP, recipeOutput);
            worktable(STONE_JAVELIN, STONE_JAVELIN, RELIC_SCRAP, recipeOutput);
            worktable(IRON_JAVELIN, IRON_JAVELIN, RELIC_SCRAP, recipeOutput);
            worktable(GOLD_JAVELIN, GOLD_JAVELIN, RELIC_SCRAP, recipeOutput);
            worktable(DIAMOND_JAVELIN, DIAMOND_JAVELIN, RELIC_SCRAP, recipeOutput);
            worktable(ANCIENT_JAVELIN, ANCIENT_JAVELIN, RELIC_SCRAP, recipeOutput);

            sifter(ModItemTags.SIFTER_INPUTS).nothing(5d).addOutput(Items.POTATO, 15d).
                    addOutput(Items.CARROT, 15d).addOutput(Items.BEETROOT_SEEDS, 5d).addOutput(Items.PUMPKIN_SEEDS, 4d).
                    addOutput(Items.MELON_SEEDS, 4d).addOutput(Items.BONE_MEAL, 20d).
                    addOutput(Items.IRON_NUGGET, 3d).addOutput(Items.GOLD_NUGGET, 3d).
                    addOutput(ModBlocks.AMBER_CHUNK_DOMINICAN.get(), 1d).addOutput(ModItems.FERN_SEED_FOSSIL.get(), 1d).
                    addOutput(Blocks.COBBLESTONE, 5d).
                    addOutput(ModItems.PLANT_FOSSIL.get(), 2d).addOutput(ModItems.BIO_FOSSIL.get(), 2d).
                    addOutput(ModItems.POTTERY_SHARD.get(), 5d).addOutput(Items.CLAY_BALL, 10d).save(recipeOutput);

            sifter(Blocks.GRAVEL).nothing(5d).addOutput(Items.POTATO, 15d).
                    addOutput(Items.CARROT, 10d).addOutput(Items.BEETROOT_SEEDS, 3d).addOutput(Items.PUMPKIN_SEEDS, 2d).
                    addOutput(Items.MELON_SEEDS, 2d).addOutput(Items.FLINT, 5d).addOutput(Items.BONE_MEAL, 20d).
                    addOutput(Items.IRON_NUGGET, 4d).addOutput(Items.GOLD_NUGGET, 4d).
                    addOutput(ModBlocks.AMBER_CHUNK_DOMINICAN.get(), 1d).
                    addOutput(Blocks.COBBLESTONE, 5d).
                    addOutput(ModItems.PLANT_FOSSIL.get(), 2d).addOutput(ModItems.BIO_FOSSIL.get(), 2d).
                    addOutput(ModItems.POTTERY_SHARD.get(), 5d).addOutput(Items.CLAY_BALL, 15d).save(recipeOutput);

            for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
                if (info.dnaItem != null && info.getDNAResult() != null) {
                    cultureVat(info.dnaItem, info.getDNAResult(), recipeOutput);
                }
            }
            for (VanillaEntityInfo info : VanillaEntityInfo.values()) {
                if (info.dnaItem != null && info.getDNAResult() != null) {
                    cultureVat(info.dnaItem, info.getDNAResult(), recipeOutput);
                }
                if (info.cultivatedBirdEggItem != null) {
                    analyzed(info.cultivatedBirdEggItem).addOutput(info.dnaItem, 100).save(recipeOutput);
                }
            }
            analyzed(Items.NAUTILUS_SHELL).addOutput(PrehistoricEntityInfo.NAUTILUS.dnaItem, 100).save(recipeOutput);
            cultureVat(BEE.dnaItem, ARTIFICIAL_HONEYCOMB.get(), recipeOutput);
            cultureVat(SNIFFER.dnaItem, Items.SNIFFER_EGG, recipeOutput);
            cultureVat(TURTLE.dnaItem, Items.TURTLE_EGG, recipeOutput);
            cultureVat(FROG.dnaItem, Items.FROGSPAWN, recipeOutput);
            //FERN_SEED_FOSSIL -> FERN_SEED culture vat recipe dropped: FERN_SEED is a BlockItem for
            //ModBlocks.FERNS (FernsBlock), which isn't ported yet - see ModItems.java's own comment on
            //FERN_SEED_FOSSIL and fossils-phase7-task4-tags-providers memory for the same gap.
            cultureVat(CALAMITES_FOSSIL_SAPLING, CALAMITES_SAPLING, recipeOutput);
            cultureVat(CORDAITES_FOSSIL_SAPLING, CORDAITES_SAPLING, recipeOutput);
            cultureVat(PALM_FOSSIL_SAPLING, PALM_SAPLING, recipeOutput);
            cultureVat(SIGILLARIA_FOSSIL_SAPLING, SIGILLARIA_SAPLING, recipeOutput);
            cultureVat(TEMPSKYA_FOSSIL_SAPLING, TEMPSKYA_SAPLING, recipeOutput);
            for (PrehistoricPlantInfo info : PrehistoricPlantInfo.plantsWithSeeds()) {
                cultureVat(info.getFossilizedPlantSeedItem(), info.getPlantSeedItem(), recipeOutput);
            }
        }
        //Create-mod compat crafting recipes (source's FossilCreateRecipeProvider) are explicitly Phase 9
        //(compat-integration datagen) scope, not Phase 7 - see fossils-phase7-recipes-datagen-research memory.
    }

    public AnalyzerRecipeBuilder analyzed(ItemLike itemLike) {
        return new AnalyzerRecipeBuilder(itemLike);
    }

    public AnalyzerRecipeBuilder analyzed(TagKey<Item> tagKey) {
        return new AnalyzerRecipeBuilder(tagKey);
    }

    public SifterRecipeBuilder sifter(ItemLike itemLike) {
        return new SifterRecipeBuilder(itemLike);
    }

    public SifterRecipeBuilder sifter(TagKey<Item> tagKey) {
        return new SifterRecipeBuilder(tagKey);
    }

    public void worktable(ItemLike itemInput, ItemLike itemOutput, ItemLike itemFuel, int duration, RecipeOutput recipeOutput) {
        new WorktableRecipeBuilder(itemInput, itemFuel, itemOutput, duration).save(recipeOutput);
    }

    public void worktable(ItemLike itemInput, ItemLike itemOutput, ItemLike itemFuel, RecipeOutput recipeOutput) {
        new WorktableRecipeBuilder(itemInput, itemFuel, itemOutput).save(recipeOutput);
    }

    public void cultureVat(ItemLike itemInput, ItemLike itemOutput, RecipeOutput recipeOutput) {
        new CultureVatRecipeBuilder(itemInput, itemOutput).save(recipeOutput);
    }

    private Item colorToDye(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
        };
    }

    private static void smithing(ItemLike base, ItemLike addition, ItemLike result, RecipeCategory recipeCategory, RecipeOutput recipeOutput) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(base), Ingredient.of(addition), recipeCategory, result.asItem())
                .unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(addition).build()))
                .save(recipeOutput, BuiltInRegistries.ITEM.getKey(result.asItem()));
    }

    private static void fullCooking(RecipeCategory category, Ingredient ingredient, ItemPredicate predicate, ItemLike result, String ingredientName, String resultName, RecipeOutput recipeOutput, float exp) {
        ResourceLocation resultLocation = FossilsAndArcheologyRevival.location(resultName);
        var furnace = SimpleCookingRecipeBuilder.smelting(ingredient, category, result, exp, 200)
                .unlockedBy("has_" + ingredientName, inventoryTrigger(predicate));
        var campfire = SimpleCookingRecipeBuilder.campfireCooking(ingredient, category, result, exp, 600)
                .unlockedBy("has_" + ingredientName, inventoryTrigger(predicate));
        var smoker = SimpleCookingRecipeBuilder.smoking(ingredient, category, result, exp, 100)
                .unlockedBy("has_" + ingredientName, inventoryTrigger(predicate));
        furnace.save(recipeOutput, resultLocation);
        campfire.save(recipeOutput, resultLocation + "_from_campfire_cooking");
        smoker.save(recipeOutput, resultLocation + "_from_smoking");
    }

    private static void fullCooking(RecipeCategory category, ItemLike ingredient, ItemLike result, RecipeOutput recipeOutput, float exp) {
        fullCooking(category, Ingredient.of(ingredient), ItemPredicate.Builder.item().of(ingredient).build(), result, RecipeBuilder.getDefaultRecipeId(ingredient).getPath(), RecipeBuilder.getDefaultRecipeId(result).getPath(), recipeOutput, exp);
    }

    private void fullOre(RecipeCategory category, ItemLike ingredient, ItemLike result, RecipeOutput recipeOutput, float exp) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), category, result, exp, 200)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(result) + "_from_smelting_" + getItemName(ingredient));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), category, result, exp, 100)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(result) + "_from_blasting_" + getItemName(ingredient));
    }

    protected void stonecutter(RecipeOutput recipeOutput, ItemLike result, ItemLike material, int resultCount) {
        stonecutter(RecipeCategory.BUILDING_BLOCKS, recipeOutput, result, material, resultCount);
    }

    protected void stonecutter(RecipeCategory category, RecipeOutput recipeOutput, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), category, result, resultCount).unlockedBy(getHasName(material), has(material));
        String id = getConversionRecipeName(result, material);
        builder.save(recipeOutput, FossilsAndArcheologyRevival.MODID + ":" + id + "_stonecutting");
    }
}
