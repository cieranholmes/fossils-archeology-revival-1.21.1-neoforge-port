package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import net.toopa.festivedelight.init.FestiveDelightModBlocks;
import net.toopa.festivedelight.init.FestiveDelightModItems;

/**
 * Ported from source's forge {@code FestiveDelightCompat} (Festive Delight addon, modid
 * {@code festive_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (14/15
 * added, 1 excluded, matching source).
 *
 * <p>Verified directly against the resolved jar via javap - every field name is unchanged from
 * source, no code changes were needed. The only real gotcha was in build.gradle: Modrinth publishes
 * a "2.0.0" version_number for BOTH a genuine NeoForge 1.21.1 build (version id {@code FP8ZwEus})
 * and an unrelated Forge 1.20.1 build (version id {@code 4rjkNzGf}) - resolving by the plain
 * {@code "2.0.0"} version_number silently picked the wrong (Forge) one, whose
 * {@code net.minecraftforge.registries.RegistryObject} type doesn't exist on a NeoForge classpath.
 * Fixed by pinning {@code festive_delight_version} to the specific version ID {@code FP8ZwEus}
 * instead of the ambiguous version_number.
 */
public class FestiveDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addMeat(FestiveDelightModItems.FESTIVE_CHIKEN.get());
        FoodMappings.addPlant(FestiveDelightModItems.CINNAMON_POWDER.get(), 10);
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_DOUGH.get(), 10);
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_MAN_DOUGH.get(), 10);
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_COOKIE_DOUGH_CREEPER.get(), 10);
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_COOKIE_CREEPER.get());
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_MAN.get());
        FoodMappings.addPlant(FestiveDelightModItems.GINGERBREAD_MAN_BASE.get());
        //FoodMappings.addPlant(FestiveDelightModItems.SUGAR_CANE.get()); Not suitable for dinos
        FoodMappings.addPlant(FestiveDelightModBlocks.CINNAMON_BUSH.get(), 15);
        //14/15 added, 1 excluded
    }
}
