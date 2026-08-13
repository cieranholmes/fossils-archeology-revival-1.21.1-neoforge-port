package com.github.fossils.food;

import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricMobType;
import com.github.fossils.entity.prehistoric.base.VanillaEntityInfo;

/**
 * Populates the target's simplified in-memory FoodMappings directly from each species' foodItem/cookedFoodItem/
 * eggItem fields - see fossils-phase2-items-progress memory for why source's JSON+networking FoodMappingsManager
 * is deliberately not ported. Must run after ModItems.register(...)'s RegisterEvent has actually fired (i.e. from
 * FMLCommonSetupEvent, not mod construction) so those fields are populated.
 */
public class FossilFoodMappings {
    public static void register() {
        for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
            if (info.mobType == PrehistoricMobType.FISH) {
                if (info.eggItem != null) FoodMappings.addFish(info.eggItem, 20);
                if (info.foodItem != null) FoodMappings.addFish(info.foodItem);
                if (info.cookedFoodItem != null) FoodMappings.addFish(info.cookedFoodItem);
            } else {
                if (info.foodItem != null) FoodMappings.addMeat(info.foodItem);
                if (info.cookedFoodItem != null) FoodMappings.addMeat(info.cookedFoodItem);
            }
            if (info.mobType == PrehistoricMobType.BIRD) {
                FoodMappings.addEgg(info.cultivatedBirdEggItem, 15);
                FoodMappings.addEgg(info.birdEggItem, 10);
            }
        }
        for (VanillaEntityInfo info : VanillaEntityInfo.values()) {
            if (info.mobType == PrehistoricMobType.VANILLA_BIRD) {
                FoodMappings.addEgg(info.cultivatedBirdEggItem, 15);
            }
        }
    }
}
