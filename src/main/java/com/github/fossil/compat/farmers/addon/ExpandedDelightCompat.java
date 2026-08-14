package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import ianm1647.expandeddelight.common.registry.EDBlocks;
import ianm1647.expandeddelight.common.registry.EDItems;

/**
 * Ported from source's <b>fabric-only</b> {@code ExpandedDelightCompat} (Expanded Delight addon,
 * modid {@code expandeddelight}) - this addon had no forge-side source class, so this is ported
 * from the fabric module instead. Drops the {@code FoodMappingsManager} parameter - calls the
 * static {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly.
 *
 * <p>Verified directly against the resolved {@code expanded-delight-0.1.4-neoforge.jar} via javap:
 * the NeoForge-native build rebased the registry onto {@code Supplier<Item>}-returning static
 * fields (still reached via {@code .get()}), under package
 * {@code ianm1647.expandeddelight.common.registry}, classes {@code EDItems}/{@code EDBlocks} (not
 * {@code ItemRegistry}/{@code BlockRegistry} as source's Fabric-only class assumed). One naming
 * difference beyond the package/class rename: source's {@code RAW_CINNAMON}/{@code GROUND_CINNAMON}
 * map to this build's {@code CINNAMON_STICK}/{@code CINNAMON} respectively (no field of the old
 * names exists; {@code CINNAMON_STICK} is the harvested raw stick and {@code CINNAMON} is the
 * ground/processed form, matching source's intent). Every other field name is unchanged.
 */
public class ExpandedDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(EDItems.CINNAMON_STICK.get(), 5);
        FoodMappings.addPlant(EDItems.CINNAMON.get(), 15);
        FoodMappings.addPlant(EDItems.ASPARAGUS_SEEDS.get(), 5);
        FoodMappings.addPlant(EDItems.CHILI_PEPPER_SEEDS.get(), 5);
        FoodMappings.addPlant(EDItems.ASPARAGUS.get());
        FoodMappings.addPlant(EDItems.SWEET_POTATO.get());
        FoodMappings.addPlant(EDItems.CHILI_PEPPER.get());
        FoodMappings.addPlant(EDItems.PEANUT.get());
        FoodMappings.addPlant(EDItems.BAKED_SWEET_POTATO.get());
        FoodMappings.addPlant(EDItems.PEANUT_BUTTER.get());
        //cheese wheel, cheese slice, cheese sandwich and grilled cheese excluded
        FoodMappings.addPlant(EDItems.PEANUT_BUTTER_SANDWICH.get());
        FoodMappings.addPlant(EDItems.PEANUT_BUTTER_HONEY_SANDWICH.get());
        FoodMappings.addPlant(EDItems.GLOW_BERRY_JELLY_SANDWICH.get());
        FoodMappings.addPlant(EDItems.SWEET_BERRY_JELLY_SANDWICH.get());
        FoodMappings.addPlant(EDItems.SWEET_ROLL.get());
        FoodMappings.addPlant(EDItems.BERRY_SWEET_ROLL.get());
        FoodMappings.addPlant(EDItems.GLOW_BERRY_SWEET_ROLL.get());
        FoodMappings.addPlant(EDItems.CINNAMON_RICE.get());
        FoodMappings.addPlant(EDItems.CINNAMON_APPLES.get());
        //2 cookies excluded
        FoodMappings.addPlant(EDItems.SNICKERDOODLE.get());
        FoodMappings.addPlant(EDItems.PEANUT_SALAD.get());
        FoodMappings.addPlant(EDItems.SWEET_POTATO_SALAD.get());
        FoodMappings.addPlant(EDItems.ASPARAGUS_SOUP.get());
        FoodMappings.addPlant(EDItems.ASPARAGUS_SOUP_CREAMY.get());
        FoodMappings.addPlant(EDItems.PEANUT_HONEY_SOUP.get());
        //Mac and cheese excluded
        //Cheesy asparagus and bacon excluded
        FoodMappings.addPlant(EDItems.PEPERONATA.get()); //Pasta with onions, vegetables and tomatoes
        FoodMappings.addPlant(EDBlocks.CINNAMON_SAPLING.get(), 15);
        FoodMappings.addPlant(EDItems.GLOW_BERRY_JELLY_SANDWICH.get());
    }
}
