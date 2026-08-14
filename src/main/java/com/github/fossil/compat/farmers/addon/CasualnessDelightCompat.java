package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import top.tobitobi.casualnessdelight.common.registry.ModItems;

/**
 * Ported from source's forge {@code CasualnessDelightCompat} (Casualness' Delight addon, modid
 * {@code casualness_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead.
 *
 * <p>Verified directly against the resolved {@code casualness-delight-909519-7467708.jar}
 * ({@code CasualnessDelight-0.6.jar}, CurseForge-only) via javap: the registry class is
 * {@code top.tobitobi.casualnessdelight.common.registry.ModItems} (not
 * {@code com.va11halla.casualness_delight.registry.ItemRegistry}), and every field was renamed
 * from PascalCase (e.g. {@code FishAndChips}) to SCREAMING_SNAKE_CASE (e.g. {@code FISH_AND_CHIPS}).
 * 29/39 mapped (up from source's 32/39: the 3 "fried dumpling" items - {@code RawFriedDumpling},
 * {@code FriedDumpling}, {@code BowlOfFriedDumpling} - no longer exist anywhere in this jar under
 * any recognizable name and are dropped here; the other 7 exclusions are source's own, preserved
 * below with their original reasons).
 */
public class CasualnessDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addFish(ModItems.FISH_AND_CHIPS.get());
        FoodMappings.addPlant(ModItems.YORKSHIRE_PUDDING.get());
        FoodMappings.addMeat(ModItems.BEEF_NOODLES.get());
        FoodMappings.addMeat(ModItems.QUICHE_LORRAINE_SLICE.get());
        //FoodMappings.addMeat(ModItems.STARGAZY_PIE.get()); excluded because it gives you nausea
        FoodMappings.addFish(ModItems.BOWL_OF_PAPER_WRAPPED_FISH.get());
        FoodMappings.addMeat(ModItems.BOBO_CHICKEN.get());
        FoodMappings.addMeat(ModItems.PHANTOM_DUMPLINGS.get());
        //2 cheese wheels and cheese wheel slice excluded because they are made of milk, so they don't fit into any food category
        //FoodMappings.addMeat(ModItems.PHANTOM_PUFF.get()); Phantom membrane is not edible, and this is just phantom membrane+cheese+milk
        //FoodMappings.addMeat(ModItems.SPICY_STRIPS.get()); Excluded because it gives rotten effect
        //FoodMappings.addMeat(ModItems.GREEN_TONGUE.get()); Popsicle, so a no-go
        FoodMappings.addPlant(ModItems.RAW_GLUTEN.get());
        FoodMappings.addPlant(ModItems.GLUTEN.get());
        FoodMappings.addPlant(ModItems.GLUTEN_SKEWER.get());
        FoodMappings.addPlant(ModItems.ROAST_GLUTEN.get());
        FoodMappings.addMeat(ModItems.RAW_DONKEY_MEAT.get());
        FoodMappings.addMeat(ModItems.COOKED_DONKEY_MEAT.get());
        FoodMappings.addMeat(ModItems.DONKEY_BURGER.get());
        FoodMappings.addPlant(ModItems.RAW_POTATO_BOBO_CHICKEN.get(), 4 * 7); //This is just a potato+a stick
        FoodMappings.addPlant(ModItems.RAW_CABBAGE_BOBO_CHICKEN.get(), 4 * 7); //This is just a cabbage+a stick
        FoodMappings.addMeat(ModItems.RAW_CHICKEN_BOBO_CHICKEN.get(), 4 * 7);
        FoodMappings.addPlant(ModItems.POTATO_BOBO_CHICKEN.get());
        FoodMappings.addPlant(ModItems.CABBAGE_BOBO_CHICKEN.get());
        FoodMappings.addMeat(ModItems.RAW_CHICKEN_BOBO_CHICKEN.get(), 4 * 7);
        FoodMappings.addPlant(ModItems.POTATO_SLICE.get(), 4 * 7);
        FoodMappings.addPlant(ModItems.POTATO_CHIP.get());
        FoodMappings.addMeat(ModItems.RAW_SPRING_ROLL.get(), 4 * 7);
        FoodMappings.addMeat(ModItems.SPRING_ROLL.get());
        FoodMappings.addMeat(ModItems.SPRING_ROLL_MEDLEY.get(), 6 * 6 * 7); //Made of 6 spring rolls
        FoodMappings.addMeat(ModItems.FRIED_CHICKEN_CHIP.get());
        FoodMappings.addFish(ModItems.FRIED_FISH.get());
        FoodMappings.addMeat(ModItems.TONKATSU.get());
        // DROPPED: RawFriedDumpling/FriedDumpling/BowlOfFriedDumpling no longer exist in this jar
        // (v0.6) under any recognizable name - not renamed, genuinely removed from the mod.
        FoodMappings.addPlant(ModItems.BOWL_OF_SWEET_RICE.get());
    }
}
