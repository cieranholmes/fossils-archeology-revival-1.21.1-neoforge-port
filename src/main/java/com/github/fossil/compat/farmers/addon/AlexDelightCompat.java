package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import com.ncpbails.alexsdelight.registry.ADItems;

/**
 * Ported from source's forge {@code AlexDelightCompat} (Alex's Delight addon, modid
 * {@code alexsdelight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead, per this repo's {@code FoodMappings} design (see
 * its class javadoc). Every mapping entry preserved exactly (22 entries).
 *
 * <p>Verified directly against the resolved {@code alexs-delight-556448-7612806.jar} via javap - all
 * 22 field names are unchanged from source, only the registry class moved from
 * {@code com.ncpbails.alexsdelight.item.ModItems} to {@code com.ncpbails.alexsdelight.registry.ADItems}.
 */
public class AlexDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addMeat(ADItems.RAW_BISON.get());
        FoodMappings.addMeat(ADItems.COOKED_BISON.get());
        FoodMappings.addMeat(ADItems.RAW_BUNFUNGUS.get());
        FoodMappings.addMeat(ADItems.COOKED_BUNFUNGUS.get());
        FoodMappings.addMeat(ADItems.COOKED_CENTIPEDE_LEG.get());
        FoodMappings.addMeat(ADItems.KANGAROO_SHANK.get());
        FoodMappings.addMeat(ADItems.COOKED_KANGAROO_SHANK.get());
        FoodMappings.addMeat(ADItems.LOOSE_MOOSE_RIB.get());
        FoodMappings.addMeat(ADItems.COOKED_LOOSE_MOOSE_RIB.get());
        FoodMappings.addMeat(ADItems.BISON_MINCE.get());
        FoodMappings.addMeat(ADItems.BISON_PATTY.get());
        FoodMappings.addMeat(ADItems.RAW_BUNFUNGUS_DRUMSTICK.get());
        FoodMappings.addMeat(ADItems.COOKED_BUNFUNGUS_DRUMSTICK.get());
        FoodMappings.addFish(ADItems.RAW_CATFISH_SLICE.get());
        FoodMappings.addFish(ADItems.COOKED_CATFISH_SLICE.get());
        FoodMappings.addPlant(ADItems.GONGYLIDIA_BRUSCHETTA.get());
        FoodMappings.addMeat(ADItems.MAGGOT_SALAD.get());
        FoodMappings.addMeat(ADItems.KANGAROO_STEW.get());
        FoodMappings.addPlant(ADItems.ACACIA_BLOSSOM_SOUP.get());
        FoodMappings.addFish(ADItems.LOBSTER_PASTA.get());
        FoodMappings.addMeat(ADItems.BISON_BURGER.get());
        FoodMappings.addMeat(ADItems.BUNFUNGUS_SANDWICH.get());
        FoodMappings.addMeat(ADItems.KANGAROO_PASTA.get());
    }
}
