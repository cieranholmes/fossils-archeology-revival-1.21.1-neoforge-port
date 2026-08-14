package com.github.fossil.compat.farmers.addon;

import alabaster.crabbersdelight.common.registry.CDModItems;
import com.github.fossil.food.FoodMappings;

/**
 * Ported from source's forge {@code CrabberDelightCompat} (Crabber's Delight addon, modid
 * {@code crabbersdelight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (22/22
 * added).
 *
 * <p>Not verified against a resolved jar - ported from source's class/field names as best effort.
 * Web audit confirmed a NeoForge 1.21.1 build exists (Modrinth {@code crabbers-delight} v1.2.6; the
 * upstream GitHub repo's only active branch is literally named {@code NeoForge-1.21.1}). The modid
 * could not be independently confirmed (repo's mods.toml uses a Gradle template variable) - kept as
 * source's {@code crabbersdelight} guess, unconfirmed.
 */
public class CrabberDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addFish(CDModItems.BISQUE.get()); //Dish made of fish+plants, so I considered it a fish
        FoodMappings.addFish(CDModItems.CLAM_BAKE.get()); //Dish made of fish+plants, so I considered it a fish
        FoodMappings.addMeat(CDModItems.CLAM_CHOWDER.get()); //Dish made of clam meat+plants, so I considered it a meat
        FoodMappings.addMeat(CDModItems.COOKED_CLAM_MEAT.get()); //Clam meat so meat
        FoodMappings.addFish(CDModItems.COOKED_CLAWSTER.get());
        FoodMappings.addFish(CDModItems.COOKED_CRAB.get());
        FoodMappings.addFish(CDModItems.COOKED_SHRIMP.get());
        FoodMappings.addFish(CDModItems.COOKED_TROPICAL_FISH.get());
        FoodMappings.addFish(CDModItems.COOKED_TROPICAL_FISH_SLICE.get());
        FoodMappings.addFish(CDModItems.CRAB_CAKES.get()); //Has crab in it so I will count it as a fish
        FoodMappings.addFish(CDModItems.CRAB_LEGS.get());
        FoodMappings.addFish(CDModItems.FISH_STICK.get());
        FoodMappings.addPlant(CDModItems.KELP_SHAKE.get(), 5); //This is a drink but it is made of kelp so plant
        FoodMappings.addMeat(CDModItems.RAW_CLAM_MEAT.get());
        FoodMappings.addFish(CDModItems.RAW_CLAWSTER.get());
        FoodMappings.addFish(CDModItems.RAW_CRAB.get());
        FoodMappings.addFish(CDModItems.RAW_SHRIMP.get());
        FoodMappings.addMeat(CDModItems.SEAFOOD_GUMBO.get()); //Dish made of meat, fish+plants, so I considered it a meat
        FoodMappings.addFish(CDModItems.SHRIMP_SKEWER.get());
        FoodMappings.addFish(CDModItems.STUFFED_NAUTILUS_SHELL.get());
        FoodMappings.addMeat(CDModItems.SURF_AND_TURF.get()); //Made of fish and meat, so meat
        FoodMappings.addFish(CDModItems.TROPICAL_FISH_SLICE.get());
        //22/22 added
    }
}
