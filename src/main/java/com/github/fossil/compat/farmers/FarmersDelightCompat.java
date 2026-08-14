package com.github.fossil.compat.farmers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.food.FoodMappings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.fml.ModList;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Merged replacement for source's split {@code CommonFarmersDelightCompat} (common module) +
 * {@code FarmersDelightCompat} (forge module) - this target repo has no Fabric/Forge split and
 * neither source class had any loader-specific behavior, so they're combined here. Deliberately
 * excludes the ~22 addon compat classes under source's
 * {@code forge.compat.farmers.addon}/this repo's {@code com.github.fossil.compat.farmers.addon} -
 * those are ported separately; this class does not call into that package.
 *
 * <p>{@link #registerFoodMappings()} does not gate on Farmer's Delight being loaded - the caller
 * (this mod's main class) is responsible for checking
 * {@code ModList.get().isLoaded("farmersdelight")} before invoking it, matching the
 * {@code AlexsMobsCompat}/{@code CarryOnCompat} convention elsewhere in
 * {@code com.github.fossil.compat} (neither of those gate internally either - both are simple
 * FD-class-referencing statics that would crash if called without FD present).
 * {@link #removeConflictingRecipes} does its own loaded-check internally, since unlike the food
 * mappings it must run unconditionally on every server start to strip whichever side's
 * conflicting recipe applies.
 */
public class FarmersDelightCompat {
    private static final String FARMERS_DELIGHT_MOD_ID = "farmersdelight";

    /**
     * Ported from source's {@code CommonFarmersDelightCompat#removeConflictingRecipes}. 1.21.1's
     * {@code RecipeManager} still exposes {@code getRecipes()}/{@code replaceRecipes(Iterable)} -
     * the only shape change from source's 1.20.1 code is that {@code RecipeHolder} is now a record
     * ({@code id()} instead of {@code getId()}). Copies into a fresh mutable list rather than
     * mutating whatever collection {@code getRecipes()} hands back, since that collection's
     * mutability isn't part of the documented API contract.
     */
    public static void removeConflictingRecipes(MinecraftServer server) {
        List<RecipeHolder<?>> recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
        if (ModList.get().isLoaded(FARMERS_DELIGHT_MOD_ID)) {
            FossilsAndArcheologyRevival.LOGGER.info("Farmer's Delight detected and incompatible recipes removed.");
            recipes.removeIf(recipe -> {
                String name = recipe.id().getPath();
                return name.startsWith("egg_to_cooked_egg") || name.startsWith("dino_eggs_to_cooked_egg");
            });
        } else {
            recipes.removeIf(recipe -> recipe.id().getPath().startsWith("fa_eggs_to_fried_egg"));
        }
        server.getRecipeManager().replaceRecipes(recipes);
    }

    /**
     * Ported from source's forge-only {@code FarmersDelightCompat#registerFoodMappings(FoodMappingsManager)}.
     * This target repo never ported {@code FoodMappingsManager} (see {@link FoodMappings}'s class
     * javadoc) - drops the manager parameter and calls the static {@link FoodMappings} methods
     * directly instead. Every FD item/block referenced below was checked directly against the
     * resolved farmers-delight-1.21.1-1.3.2.jar's {@code ModItems}/{@code ModBlocks} - none were
     * renamed or removed since source's era, so all ~70 entries (including source's own duplicate
     * {@code addMeat(ROASTED_MUTTON_CHOPS)}/{@code addFish(COD_ROLL)} calls) are preserved as-is.
     */
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ModItems.CABBAGE.get());
        FoodMappings.addPlant(ModItems.TOMATO.get());
        FoodMappings.addPlant(ModItems.ONION.get());
        FoodMappings.addPlant(ModItems.TOMATO_SAUCE.get());
        FoodMappings.addPlant(ModItems.WHEAT_DOUGH.get());
        FoodMappings.addPlant(ModItems.RAW_PASTA.get());
        FoodMappings.addPlant(ModItems.PUMPKIN_SLICE.get());
        FoodMappings.addPlant(ModItems.CABBAGE_LEAF.get());
        FoodMappings.addPlant(ModItems.PIE_CRUST.get());
        FoodMappings.addPlant(ModItems.APPLE_PIE.get(), getPieValue((PieBlock) ModBlocks.APPLE_PIE.get()));
        FoodMappings.addPlant(ModItems.SWEET_BERRY_CHEESECAKE.get(), getPieValue((PieBlock) ModBlocks.SWEET_BERRY_CHEESECAKE.get()));
        FoodMappings.addPlant(ModItems.CHOCOLATE_PIE.get(), getPieValue((PieBlock) ModBlocks.CHOCOLATE_PIE.get()));
        FoodMappings.addPlant(ModItems.APPLE_PIE_SLICE.get());
        FoodMappings.addPlant(ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get());
        FoodMappings.addPlant(ModItems.CHOCOLATE_PIE_SLICE.get());
        FoodMappings.addPlant(ModItems.PUMPKIN_PIE_SLICE.get());
        FoodMappings.addPlant(ModItems.SWEET_BERRY_COOKIE.get());
        FoodMappings.addPlant(ModItems.HONEY_COOKIE.get());
        FoodMappings.addPlant(ModItems.MELON_POPSICLE.get());
        FoodMappings.addPlant(ModItems.GLOW_BERRY_CUSTARD.get());
        FoodMappings.addPlant(ModItems.FRUIT_SALAD.get());
        FoodMappings.addPlant(ModItems.MIXED_SALAD.get());
        FoodMappings.addPlant(ModItems.NETHER_SALAD.get());
        FoodMappings.addPlant(ModItems.GLEAMING_SALAD.get());
        FoodMappings.addPlant(ModItems.KELP_ROLL.get());
        FoodMappings.addPlant(ModItems.KELP_ROLL_SLICE.get());
        FoodMappings.addPlant(ModItems.COOKED_RICE.get());
        FoodMappings.addPlant(ModItems.VEGETABLE_SOUP.get());
        FoodMappings.addPlant(ModItems.FRIED_RICE.get());
        FoodMappings.addPlant(ModItems.PUMPKIN_SOUP.get());
        FoodMappings.addPlant(ModItems.ONION_SOUP.get());
        FoodMappings.addPlant(ModItems.MUSHROOM_RICE.get());
        FoodMappings.addPlant(ModItems.VEGETABLE_NOODLES.get());
        FoodMappings.addPlant(ModItems.STUFFED_PUMPKIN.get());
        FoodMappings.addPlant(ModItems.CABBAGE_SEEDS.get(), 5);
        FoodMappings.addPlant(ModItems.TOMATO_SEEDS.get(), 5);

        FoodMappings.addEgg(ModItems.FRIED_EGG.get());
        FoodMappings.addEgg(ModItems.EGG_SANDWICH.get());

        FoodMappings.addMeat(ModItems.MINCED_BEEF.get());
        FoodMappings.addMeat(ModItems.BEEF_PATTY.get());
        FoodMappings.addMeat(ModItems.CHICKEN_CUTS.get());
        FoodMappings.addMeat(ModItems.COOKED_CHICKEN_CUTS.get());
        FoodMappings.addMeat(ModItems.BACON.get());
        FoodMappings.addMeat(ModItems.COOKED_BACON.get());
        FoodMappings.addMeat(ModItems.HAM.get());
        FoodMappings.addMeat(ModItems.SMOKED_HAM.get());
        FoodMappings.addMeat(ModItems.BARBECUE_STICK.get());
        FoodMappings.addMeat(ModItems.CHICKEN_SANDWICH.get());
        FoodMappings.addMeat(ModItems.HAMBURGER.get());
        FoodMappings.addMeat(ModItems.BACON_SANDWICH.get());
        FoodMappings.addMeat(ModItems.MUTTON_WRAP.get());
        FoodMappings.addMeat(ModItems.DUMPLINGS.get());
        FoodMappings.addMeat(ModItems.STUFFED_POTATO.get());
        FoodMappings.addMeat(ModItems.BONE_BROTH.get());
        FoodMappings.addMeat(ModItems.BEEF_STEW.get());
        FoodMappings.addMeat(ModItems.CHICKEN_SOUP.get());
        FoodMappings.addMeat(ModItems.NOODLE_SOUP.get());
        FoodMappings.addMeat(ModItems.BACON_AND_EGGS.get());
        FoodMappings.addMeat(ModItems.PASTA_WITH_MEATBALLS.get());
        FoodMappings.addMeat(ModItems.PASTA_WITH_MUTTON_CHOP.get());
        FoodMappings.addMeat(ModItems.CABBAGE_ROLLS.get());
        FoodMappings.addMeat(ModItems.ROASTED_MUTTON_CHOPS.get());
        FoodMappings.addMeat(ModItems.STEAK_AND_POTATOES.get());
        FoodMappings.addMeat(ModItems.ROAST_CHICKEN.get());
        FoodMappings.addMeat(ModItems.ROASTED_MUTTON_CHOPS.get());
        FoodMappings.addMeat(ModItems.ROASTED_MUTTON_CHOPS.get());
        FoodMappings.addMeat(ModItems.ROASTED_MUTTON_CHOPS.get());

        FoodMappings.addFish(ModItems.COD_SLICE.get());
        FoodMappings.addFish(ModItems.COOKED_COD_SLICE.get());
        FoodMappings.addFish(ModItems.SALMON_SLICE.get());
        FoodMappings.addFish(ModItems.COOKED_SALMON_SLICE.get());
        FoodMappings.addFish(ModItems.SALMON_ROLL.get());
        FoodMappings.addFish(ModItems.COD_ROLL.get());
        FoodMappings.addFish(ModItems.FISH_STEW.get());
        FoodMappings.addFish(ModItems.BAKED_COD_STEW.get());
        FoodMappings.addFish(ModItems.GRILLED_SALMON.get());
        FoodMappings.addFish(ModItems.COD_ROLL.get());
        FoodMappings.addFish(ModItems.COD_ROLL.get());
    }

    /**
     * Ported from source's {@code FarmersDelightCompat#getPieValue(PieBlock)}. Source called
     * {@code block.getPieSliceItem().getItem().getFoodProperties().getNutrition()} - in 1.21.1,
     * {@code PieBlock#getPieSliceItem()} already returns an {@code ItemStack} (no {@code .getItem()}
     * needed), and food data now comes from {@code ItemStack#getFoodProperties(LivingEntity)}
     * (nullable-entity overload) returning a {@code FoodProperties} record with a {@code nutrition()}
     * accessor - same pattern already used by {@code FoodMappings#defaultNutrition}.
     */
    private static int getPieValue(PieBlock block) {
        var food = block.getPieSliceItem().getFoodProperties(null);
        return (food != null ? food.nutrition() : 0) * block.getMaxBites() * 5;
    }
}
