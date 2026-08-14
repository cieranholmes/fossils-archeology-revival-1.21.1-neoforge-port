package com.github.fossil.compat.farmers.addon;

import com.axedgaming.endersdelight.Common.Registries.EdBlocks;
import com.axedgaming.endersdelight.Common.Registries.EdItems;
import com.github.fossil.food.FoodMappings;
import vectorwing.farmersdelight.common.block.PieBlock;

/**
 * Ported from source's forge {@code EnderDelightCompat} (Ender's Delight addon, modid
 * {@code endersdelight} - a distinct addon from "End's Delight"/{@link EndDelightCompat} despite the
 * near-identical name/theme). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (18/21
 * added, 3 excluded for causing nausea, matching source).
 *
 * <p>Verified directly against the resolved {@code enders-delight-1.2.0.jar} via javap. Every food
 * item field name is unchanged from source; only the registry package/class changed, from
 * {@code com.axedgaming.endersdelight.item.ModItems}/{@code block.ModBlocks} to
 * {@code com.axedgaming.endersdelight.Common.Registries.EdItems}/{@code EdBlocks} (note the
 * unusual capitalized {@code Common.Registries} package segments - confirmed directly from the
 * jar, not a typo). Source's own addon-specific {@code PieBlock} class no longer exists in this
 * version - the addon's {@code CHORUS_PIE} block now reuses Farmer's Delight's own
 * {@code vectorwing.farmersdelight.common.block.PieBlock} directly (confirmed: no
 * {@code PieBlock} class exists anywhere in this jar), so {@code getPieValue} below uses that.
 */
public class EnderDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(EdItems.CHORUS_STEW.get());
        FoodMappings.addMeat(EdItems.CRAWLING_SANDWICH.get());
        FoodMappings.addMeat(EdItems.ENDER_PAELLA.get()); //This has meat in it so I will count it as meat
        FoodMappings.addMeat(EdItems.ENDERMITE_STEW.get());
        FoodMappings.addPlant(EdItems.PEARL_PASTA.get()); //This is pasta so I will count it as a plant
        FoodMappings.addPlant(EdItems.STRANGE_ECLAIR.get()); //This is made of wheat, sugar and eye of ender so I will count it as a plant
        FoodMappings.addPlant(EdItems.STUFFED_SHULKER_BOWL.get());
        FoodMappings.addPlant(EdItems.TWISTED_CEREAL.get()); //This is made of ender sight, ender eyes, chorus fruit and milk, I will add it as a plant because ender sight/ender eyes isn't really "meat"
        FoodMappings.addMeat(EdItems.UNCANNY_COOKIES.get()); //Has endermite skin in it, I will count that as meat.
        FoodMappings.addMeat(EdItems.CRISPY_SKEWER.get());
        FoodMappings.addPlant(EdItems.CHORUS_PIE_SLICE.get());
        FoodMappings.addPlant(EdItems.CHORUS_PIE.get(), getPieValue((PieBlock) EdBlocks.CHORUS_PIE.get()));
        //18/21 added, Excluded 3 items because they cause nausea.
    }

    private static int getPieValue(PieBlock block) {
        var food = block.getPieSliceItem().getFoodProperties(null);
        return (food != null ? food.nutrition() : 0) * block.getMaxBites() * 5;
    }
}
