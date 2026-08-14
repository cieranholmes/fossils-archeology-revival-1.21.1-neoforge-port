package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import com.scouter.oceansdelight.items.ODItems;

/**
 * Ported from source's forge {@code OceanDelightCompat} (Ocean's Delight addon, modid
 * {@code oceansdelight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (16/16
 * added).
 *
 * <p>Not verified against a resolved jar - ported from source's class/field names as best effort.
 * Web audit confirmed a NeoForge 1.21.1 build exists (Modrinth/CurseForge {@code oceans-delight}
 * v1.0.4, filename {@code oceansdelight-neoforge-1.0.4-1.21.1}) and confirmed the modid as
 * {@code oceansdelight}, matching source.
 */
public class OceanDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addFish(ODItems.TENTACLES.get());
        FoodMappings.addFish(ODItems.CUT_TENTACLES.get());
        FoodMappings.addFish(ODItems.SQUID_RINGS.get());
        FoodMappings.addFish(ODItems.TENTACLE_ON_A_STICK.get());
        FoodMappings.addFish(ODItems.BAKED_TENTACLE_ON_A_STICK.get());
        FoodMappings.addFish(ODItems.BOWL_OF_GUARDIAN_SOUP.get());
        FoodMappings.addFish(ODItems.GUARDIAN_TAIL.get());
        FoodMappings.addFish(ODItems.COOKED_GUARDIAN_TAIL.get());
        FoodMappings.addFish(ODItems.ELDER_GUARDIAN_SLAB.get());
        FoodMappings.addFish(ODItems.ELDER_GUARDIAN_SLICE.get());
        FoodMappings.addFish(ODItems.COOKED_ELDER_GUARDIAN_SLICE.get());
        FoodMappings.addFish(ODItems.ELDER_GUARDIAN_ROLL.get());
        FoodMappings.addFish(ODItems.CABBAGE_WRAPPED_ELDER_GUARDIAN.get());
        FoodMappings.addFish(ODItems.FUGU_SLICE.get());
        FoodMappings.addFish(ODItems.FUGU_ROLL.get());
        FoodMappings.addPlant(ODItems.BRAISED_SEA_PICKLE.get());
        //16/16 added
    }
}
