package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import org.hiedacamellia.seeddelight.registry.BlockRegistry;
import org.hiedacamellia.seeddelight.registry.ItemRegistry;
import vectorwing.farmersdelight.common.block.PieBlock;

/**
 * Ported from source's forge {@code SeedDelightCompat} (Seed Delight addon, modid
 * {@code seeddelight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (30/30
 * added, including source's own duplicate {@code addPlant(DriedWatermelonSeed)} call).
 *
 * <p>Verified directly against the resolved jar via javap - every field name is unchanged from
 * source, no code changes were needed. The real gotcha was in build.gradle: Modrinth's
 * {@code seed-delight} project reuses the version_number {@code "1.0.1"} across six different
 * loader/game-version combinations (including a Forge-only 1.18.2 build) - resolving by the plain
 * version_number silently picked a build whose {@code RegistryObject} type doesn't exist on a
 * NeoForge classpath. Fixed by pinning {@code seed_delight_version} to the specific version ID
 * {@code uzv8gBFo} (the genuine NeoForge 1.21/1.21.1 build) instead.
 *
 * <p>Source imported {@code getPieValue} as a static import from this repo's
 * {@code com.github.fossil.compat.farmers.FarmersDelightCompat} - that method is {@code private}
 * there and this file cannot reach outside {@code farmers.addon} to change that, so the pie value
 * calculation is duplicated locally instead (same 1.21.1 Farmer's Delight API used elsewhere in
 * this repo).
 */
public class SeedDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ItemRegistry.Acorn.get(), 5);
        FoodMappings.addPlant(ItemRegistry.AcornKernel.get());
        FoodMappings.addPlant(ItemRegistry.Pinecone.get(), 5);
        FoodMappings.addPlant(ItemRegistry.PineNutKernel.get());
        FoodMappings.addPlant(ItemRegistry.SunflowerSeed.get(), 5);
        FoodMappings.addPlant(ItemRegistry.FriedSunflowerSeed.get());
        FoodMappings.addPlant(ItemRegistry.DriedWatermelonSeed.get());
        FoodMappings.addPlant(ItemRegistry.DriedWatermelonSeed.get());
        FoodMappings.addPlant(ItemRegistry.Rosehip.get());
        FoodMappings.addPlant(ItemRegistry.Cherry.get());
        FoodMappings.addPlant(ItemRegistry.RawAcornNoodle.get());
        FoodMappings.addPlant(ItemRegistry.RosehipJamSandwich.get());
        FoodMappings.addPlant(ItemRegistry.CherryJamSandwich.get());
        FoodMappings.addPlant(ItemRegistry.SunflowerSeedCrisp.get());
        FoodMappings.addPlant(ItemRegistry.SunflowerSeedToast.get());
        FoodMappings.addPlant(ItemRegistry.SeedRosehipPie.get());
        FoodMappings.addMeat(ItemRegistry.RoastedBeefWithSeed.get());
        FoodMappings.addPlant(ItemRegistry.AcornTofu.get());
        FoodMappings.addPlant(ItemRegistry.StirFriedCabbageWithAcorn.get());
        FoodMappings.addPlant(ItemRegistry.AcornBread.get());
        FoodMappings.addPlant(ItemRegistry.PinenutGruel.get());
        FoodMappings.addPlant(ItemRegistry.PinenutCake.get());
        FoodMappings.addPlant(ItemRegistry.SeedTart.get());
        FoodMappings.addMeat(ItemRegistry.PinenutWithMeatballs.get());
        FoodMappings.addPlant(ItemRegistry.RoseCookie.get());
        FoodMappings.addPlant(ItemRegistry.RosehipCake.get());
        FoodMappings.addMeat(ItemRegistry.CherryPork.get());
        FoodMappings.addPlant(ItemRegistry.MilkCherryMouss.get());
        FoodMappings.addPlant(ItemRegistry.RosehipPie.get(), getPieValue((PieBlock) BlockRegistry.RosehipPie.get()));
        FoodMappings.addPlant(ItemRegistry.RosehipPieSlice.get());
        //30/30 added
    }

    private static int getPieValue(PieBlock block) {
        var food = block.getPieSliceItem().getFoodProperties(null);
        return (food != null ? food.nutrition() : 0) * block.getMaxBites() * 5;
    }
}
