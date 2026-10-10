package com.github.fossil.compat.fintastic;

import com.github.fossil.food.FoodMappings;
import net.voidarkana.fintastic.common.item.FintyItems;

public class FintasticCompat {
    public static void register() {
        FoodMappings.addFish(FintyItems.RAW_FISH.get());
        FoodMappings.addFish(FintyItems.COOKED_FISH.get());
        FoodMappings.addFish(FintyItems.FEATHERBACK.get());
        FoodMappings.addFish(FintyItems.MINNOW.get());
        FoodMappings.addFish(FintyItems.CATFISH.get());
        FoodMappings.addFish(FintyItems.SMALL_CATFISH.get());
        FoodMappings.addFish(FintyItems.GUPPY.get());
        FoodMappings.addFish(FintyItems.SHARKMINNOW.get());
        FoodMappings.addFish(FintyItems.PLECO.get());
        FoodMappings.addFish(FintyItems.ARAPAIMA.get());
        FoodMappings.addFish(FintyItems.GOURAMI.get());
        FoodMappings.addFish(FintyItems.COELACANTH.get());
    }
}
