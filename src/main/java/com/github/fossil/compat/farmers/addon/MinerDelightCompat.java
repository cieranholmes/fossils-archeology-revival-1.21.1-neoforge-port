package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import com.sammy.minersdelight.setup.MDItems;

/**
 * Ported from source's forge {@code MinerDelightCompat} (Miner's Delight addon, modid
 * {@code miners_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (25/26
 * added, 1 excluded, matching source).
 *
 * <p>Not verified against a resolved jar - ported from source's class/field names as best effort.
 * Web audit confirmed a NeoForge 1.21.1 build exists (Modrinth {@code miners-delight} v1.4.5; the
 * CurseForge project is now titled "Miner's Delight +", slug {@code miners-delight-plus}, same
 * mod/codebase). Modid not independently confirmed via a filename - kept as source's
 * {@code miners_delight} guess.
 */
public class MinerDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(MDItems.BAKED_CAVE_CARROT.get());
        FoodMappings.addFish(MDItems.BAKED_SQUID.get());
        FoodMappings.addFish(MDItems.BAKED_TENTACLES.get());
        FoodMappings.addMeat(MDItems.BAT_WING.get());
        FoodMappings.addPlant(MDItems.BEETROOT_SOUP_CUP.get());
        FoodMappings.addFish(MDItems.BOWL_OF_STUFFED_SQUID.get()); //Has squid so fish
        FoodMappings.addPlant(MDItems.CAVE_CARROT.get());
        FoodMappings.addPlant(MDItems.CAVE_SOUP.get());
        FoodMappings.addFish(MDItems.GLOW_SQUID.get());
        //FoodMappings.addPlant(MDItems.HOT_COCOA_CUP.get()); Excluded because the normal cocoa cup is excluded too
        FoodMappings.addMeat(MDItems.IMPROVISED_BARBECUE_STICK.get()); //Has bat wings so meat
        FoodMappings.addPlant(MDItems.MUSHROOM_STEW_CUP.get());
        FoodMappings.addPlant(MDItems.PASTA_WITH_VEGGIEBALLS.get());
        FoodMappings.addMeat(MDItems.RABBIT_STEW_CUP.get());
        FoodMappings.addEgg(MDItems.SILVERFISH_EGGS.get());
        FoodMappings.addMeat(MDItems.SMOKED_BAT_WING.get());
        FoodMappings.addFish(MDItems.SQUID.get());
        FoodMappings.addFish(MDItems.TENTACLES.get());
        FoodMappings.addFish(MDItems.WEIRD_CAVIAR.get());
        FoodMappings.addFish(MDItems.BAKED_COD_STEW_CUP.get());
        FoodMappings.addMeat(MDItems.NOODLE_SOUP_CUP.get()); //This has meat in it so it is a meat
        FoodMappings.addMeat(MDItems.BEEF_STEW_CUP.get());
        FoodMappings.addMeat(MDItems.CHICKEN_SOUP_CUP.get());
        FoodMappings.addFish(MDItems.FISH_STEW_CUP.get());
        FoodMappings.addMeat(MDItems.PUMPKIN_SOUP_CUP.get()); //This has meat in it so it is a meat
        FoodMappings.addPlant(MDItems.VEGETABLE_SOUP_CUP.get());
        //25/26, 1 excluded
    }
}
