package com.github.fossil.compat.farmers.addon;

import cn.mcmod.corn_delight.item.ItemRegistry;
import com.github.fossil.food.FoodMappings;

/**
 * Ported from source's forge {@code CornDelightCompat} (Corn Delight addon, modid
 * {@code corn_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (18/18
 * added).
 *
 * <p>Verified directly against the resolved jar via javap - every field name is unchanged from
 * source, no code changes were needed. The real gotcha: several fields' generic type
 * ({@code DeferredItem<cn.mcmod_mmf.mmlib.item.ItemFoodBase>}/{@code ItemFoodSeeds}) resolves to
 * classes from Corn Delight's own hard dependency "Mysterious Mountain Lib" (modrinth {@code mmlib}),
 * which wasn't on this project's classpath - javac needs that class resolvable even though this file
 * never references it by name, since {@code .get()}'s return type carries it. Fixed by adding
 * {@code mmlib} (v1.1.8-1.21.1) as its own compileOnly/runtimeOnly dependency in build.gradle.
 */
public class CornDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ItemRegistry.BOILED_CORN.get());
        FoodMappings.addPlant(ItemRegistry.CARAMEL_POPCORN.get());
        FoodMappings.addMeat(ItemRegistry.CLASSIC_CORN_DOG.get());
        FoodMappings.addPlant(ItemRegistry.CORN.get());
        FoodMappings.addMeat(ItemRegistry.CORN_DOG.get());
        FoodMappings.addPlant(ItemRegistry.CORN_SEEDS.get(), 5);
        FoodMappings.addPlant(ItemRegistry.CORN_SOUP.get());
        FoodMappings.addPlant(ItemRegistry.CORNBREAD.get());
        FoodMappings.addPlant(ItemRegistry.CORNBREAD_BATTER.get());
        FoodMappings.addPlant(ItemRegistry.CORNBREAD_STUFFING.get());
        FoodMappings.addPlant(ItemRegistry.CREAMED_CORN.get());
        FoodMappings.addPlant(ItemRegistry.GRILLED_CORN.get());
        FoodMappings.addMeat(ItemRegistry.NACHOS.get()); //The recipe has meat in it
        FoodMappings.addPlant(ItemRegistry.POPCORN.get()); //This is made from seeds so it is okay for it to be eaten by dinos
        FoodMappings.addPlant(ItemRegistry.RAW_TORTILLA.get());
        FoodMappings.addMeat(ItemRegistry.TACO.get());
        FoodMappings.addPlant(ItemRegistry.TORTILLA.get());
        FoodMappings.addPlant(ItemRegistry.TORTILLA_CHIP.get());
        //18/18 added
    }
}
