package com.github.fossil.compat.farmers.addon;

import com.baisylia.culturaldelights.block.ModBlocks;
import com.baisylia.culturaldelights.item.ModItems;
import com.github.fossil.food.FoodMappings;

/**
 * Ported from source's forge {@code CulturalDelightCompat} (Cultural Delights addon, modid
 * {@code culturaldelights}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (51/51
 * added).
 *
 * <p>Verified directly against the resolved {@code cultural-delights-0.17.8.jar} via javap. Real
 * runtime bug found and fixed: source's stream-filter-by-{@code toString()} lookup for
 * {@code avocado_pit} (which the previous port assumed had no dedicated constant field) threw
 * {@code ArrayIndexOutOfBoundsException} at server boot - {@code ModItems.ITEMS.getEntries()}
 * never contains it because {@code avocado_pit} is registered as a real <b>block</b>
 * ({@code ModBlocks.AVOCADO_PIT}, confirmed via javap - it has a blockstate/model/loot table, not
 * a plain item), not a bare item. Replaced the fragile runtime lookup with a direct field
 * reference.
 */
public class CulturalDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ModItems.CUCUMBER_SEEDS.get(), 5);
        FoodMappings.addPlant(ModItems.CORN_KERNELS.get(), 5);
        FoodMappings.addPlant(ModItems.EGGPLANT_SEEDS.get(), 5);
        FoodMappings.addPlant(ModItems.AVOCADO.get());
        FoodMappings.addPlant(ModItems.CUT_AVOCADO.get());
        FoodMappings.addPlant(ModItems.CUCUMBER.get());
        FoodMappings.addPlant(ModItems.PICKLE.get());
        FoodMappings.addPlant(ModItems.CUT_CUCUMBER.get());
        FoodMappings.addPlant(ModItems.CUT_PICKLE.get());
        FoodMappings.addPlant(ModItems.EGGPLANT.get());
        FoodMappings.addPlant(ModItems.CUT_EGGPLANT.get());
        FoodMappings.addPlant(ModItems.SMOKED_EGGPLANT.get());
        FoodMappings.addPlant(ModItems.SMOKED_TOMATO.get());
        FoodMappings.addPlant(ModItems.SMOKED_CUT_EGGPLANT.get());
        FoodMappings.addPlant(ModItems.SMOKED_WHITE_EGGPLANT.get());
        FoodMappings.addPlant(ModItems.WHITE_EGGPLANT.get());
        FoodMappings.addPlant(ModItems.CORN_COB.get());
        FoodMappings.addFish(ModItems.SQUID.get());
        FoodMappings.addFish(ModItems.COOKED_SQUID.get());
        FoodMappings.addFish(ModItems.RAW_CALAMARI.get());
        FoodMappings.addFish(ModItems.COOKED_CALAMARI.get());
        FoodMappings.addPlant(ModItems.POPCORN.get());
        FoodMappings.addPlant(ModItems.CORN_DOUGH.get());
        FoodMappings.addPlant(ModItems.TORTILLA.get());
        FoodMappings.addPlant(ModItems.TORTILLA_CHIPS.get());
        FoodMappings.addPlant(ModItems.ELOTE.get());
        FoodMappings.addPlant(ModItems.HEARTY_SALAD.get());
        FoodMappings.addMeat(ModItems.BEEF_BURRITO.get());
        FoodMappings.addMeat(ModItems.MUTTON_SANDWICH.get());
        FoodMappings.addPlant(ModItems.EGGPLANT_PARMESAN.get());
        FoodMappings.addPlant(ModItems.EGGPLANT_BURGER.get()); //This has no meat in it so I consider it a plant.
        FoodMappings.addPlant(ModItems.CREAMED_CORN.get());
        FoodMappings.addPlant(ModItems.CORN_COB.get());
        FoodMappings.addMeat(ModItems.CHICKEN_TACO.get());
        FoodMappings.addMeat(ModItems.SPICY_CURRY.get());
        FoodMappings.addMeat(ModItems.PORK_WRAP.get());
        FoodMappings.addFish(ModItems.FISH_TACO.get());
        FoodMappings.addPlant(ModItems.MIDORI_ROLL.get());
        FoodMappings.addPlant(ModItems.MIDORI_ROLL_SLICE.get());
        FoodMappings.addEgg(ModItems.EGG_ROLL.get());
        FoodMappings.addMeat(ModItems.CHICKEN_ROLL.get());
        FoodMappings.addMeat(ModItems.CHICKEN_ROLL_SLICE.get());
        FoodMappings.addFish(ModItems.PUFFERFISH_ROLL.get());
        FoodMappings.addFish(ModItems.TROPICAL_ROLL.get());
        FoodMappings.addFish(ModItems.RICE_BALL.get()); //This has fish in it so I count it as fish
        FoodMappings.addFish(ModItems.CALAMARI_ROLL.get());
        FoodMappings.addPlant(ModBlocks.AVOCADO_LEAVES.get(), 20);
        FoodMappings.addPlant(ModBlocks.AVOCADO_SAPLING.get(), 15);
        FoodMappings.addPlant(ModItems.AVOCADO_TOAST.get());
        FoodMappings.addPlant(ModBlocks.AVOCADO_PIT.get(), 5);
        //51/51 added! That is a lot of food!
    }
}
