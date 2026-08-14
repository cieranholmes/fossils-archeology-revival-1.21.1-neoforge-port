package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import lcyzsdh.coffee_delight.registry.ItemRegistry;

/**
 * Ported from source's forge {@code CoffeeDelightCompat} (Coffee Delight addon). Drops the
 * {@code FoodMappingsManager} parameter - calls the static {@link FoodMappings} methods directly
 * instead. Every mapping entry preserved exactly (10/10 added).
 *
 * <p>Verified directly against the resolved {@code coffee-delight-1.4.1.jar} via javap: this addon
 * fully rebranded its package from {@code club.someoneice.cofe_delight.init.ItemInit} to
 * {@code lcyzsdh.coffee_delight.registry.ItemRegistry} - every field name is unchanged, only the
 * package/class moved. Modid remains {@code coffee_delight} (confirmed from the jar's
 * neoforge.mods.toml), matching what {@code AddonConstants}/{@code FarmersDelightAddonCompat}
 * already check.
 */
public class CoffeeDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ItemRegistry.COFFEE_BERRIES.get(), 5);
        FoodMappings.addPlant(ItemRegistry.COFFEE_FLOUR.get(), 15);
        FoodMappings.addPlant(ItemRegistry.COFFEE_BEANS.get());
        FoodMappings.addPlant(ItemRegistry.COFFEE_BEANS_COOKED.get());
        FoodMappings.addPlant(ItemRegistry.COFFEE_PIE_SIDE.get());
        FoodMappings.addPlant(ItemRegistry.COFFEE_PIE.get()); //Not a block!
        FoodMappings.addPlant(ItemRegistry.QUICHE.get());
        FoodMappings.addPlant(ItemRegistry.WAFER.get());
        FoodMappings.addPlant(ItemRegistry.BERRIES_WAFER.get());
        FoodMappings.addPlant(ItemRegistry.COFFEE_WAFER.get());
        //10/10 added
    }
}
