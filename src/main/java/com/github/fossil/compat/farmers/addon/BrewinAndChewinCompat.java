package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import umpaz.brewinandchewin.common.registry.BnCItems;

/**
 * Ported from source's forge/fabric {@code BrewinAndChewinCompat} (Brewin' &amp; Chewin' addon,
 * modid {@code brewinandchewin}). Drops the {@code FoodMappingsManager} parameter - calls the
 * static {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly
 * (8 entries, including source's own duplicate {@code addPlant(KIMCHI)} call).
 *
 * <p>Verified directly against the resolved {@code brewin-and-chewin-4.4.2+1.21.1-neoforge.jar} via
 * javap: every field name is unchanged, but the field TYPE changed - {@code BnCItems} (in the
 * multi-loader {@code common.registry} package) now holds plain
 * {@code net.minecraft.world.item.Item} fields directly, not a {@code Supplier<Item>}/
 * {@code DeferredHolder}, so the {@code .get()} calls source used are gone.
 */
public class BrewinAndChewinCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(BnCItems.KIMCHI);
        FoodMappings.addMeat(BnCItems.JERKY);
        FoodMappings.addPlant(BnCItems.PICKLED_PICKLES);
        FoodMappings.addFish(BnCItems.KIPPERS);
        FoodMappings.addPlant(BnCItems.COCOA_FUDGE);
        FoodMappings.addPlant(BnCItems.KIMCHI);
        FoodMappings.addMeat(BnCItems.PIZZA_SLICE); //Contains meat
        FoodMappings.addMeat(BnCItems.HAM_AND_CHEESE_SANDWICH);
        //8/8 added
    }
}
