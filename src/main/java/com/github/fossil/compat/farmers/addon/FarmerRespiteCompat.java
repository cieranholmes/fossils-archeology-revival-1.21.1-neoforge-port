package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import com.farmersrespite.core.registry.FRBlocks;
import com.farmersrespite.core.registry.FRItems;
import vectorwing.farmersdelight.common.block.PieBlock;

/**
 * Ported from source's forge {@code FarmerRespiteCompat} (Farmer's Respite addon, modid
 * {@code farmersrespite}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (16
 * entries).
 *
 * <p><b>Uncertainty flagged:</b> the official Farmer's Respite mod (by lumpazl) is abandoned at
 * Forge 1.20.1 with no NeoForge 1.21.1 build. The web audit found an actively-maintained
 * <b>unofficial</b> NeoForge 1.21.1 port ("Farmer's Respite (Unofficial Port)" by TylerCross,
 * Modrinth slug {@code farmers-respite-unofficial-port}, v3.0.0) that reuses the exact same modid
 * {@code farmersrespite} (confirmed directly from its mods.toml) - this is a comparatively
 * lower-risk situation than a full rebrand: verified directly against the resolved
 * {@code farmers-respite-unofficial-port-3.0.0.jar} via javap - every field name below is
 * unchanged from source, only the package moved from {@code umpaz.farmersrespite.common.registry}
 * to {@code com.farmersrespite.core.registry}.
 *
 * <p>Source imported {@code getPieValue} as a static import from this repo's
 * {@code com.github.fossil.compat.farmers.FarmersDelightCompat} - that method is {@code private}
 * there and this file cannot reach outside {@code farmers.addon} to change that, so the pie value
 * calculation is duplicated locally instead (same 1.21.1 Farmer's Delight API used elsewhere in
 * this repo).
 */
public class FarmerRespiteCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(FRBlocks.WILD_TEA_BUSH.get(), 20);
        FoodMappings.addPlant(FRItems.TEA_SEEDS.get(), 5);
        FoodMappings.addPlant(FRItems.GREEN_TEA_LEAVES.get(), 5);
        FoodMappings.addPlant(FRItems.YELLOW_TEA_LEAVES.get(), 5);
        FoodMappings.addPlant(FRItems.BLACK_TEA_LEAVES.get(), 5);
        FoodMappings.addPlant(FRItems.COFFEE_BERRIES.get());
        FoodMappings.addPlant(FRItems.ROSE_HIPS.get(), 5);
        FoodMappings.addPlant(FRItems.GREEN_TEA_COOKIE.get());
        FoodMappings.addPlant(FRItems.NETHER_WART_SOURDOUGH.get());
        FoodMappings.addFish(FRItems.BLACK_COD.get());
        FoodMappings.addPlant(FRItems.TEA_CURRY.get());
        FoodMappings.addMeat(FRItems.BLAZING_CHILI.get());
        FoodMappings.addPlant(FRItems.COFFEE_CAKE.get(), 7 * 7 * 3);
        FoodMappings.addPlant(FRItems.COFFEE_CAKE_SLICE.get());
        FoodMappings.addPlant(FRItems.ROSE_HIP_PIE_SLICE.get());
        FoodMappings.addPlant(FRItems.ROSE_HIP_PIE.get(), getPieValue((PieBlock) FRBlocks.ROSE_HIP_PIE.get()));
    }

    private static int getPieValue(PieBlock block) {
        var food = block.getPieSliceItem().getFoodProperties(null);
        return (food != null ? food.nutrition() : 0) * block.getMaxBites() * 5;
    }
}
