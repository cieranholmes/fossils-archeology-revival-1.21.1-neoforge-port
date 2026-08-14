package com.github.fossil.compat.farmers.addon;

import cn.foggyhillside.ends_delight.registry.ModBlocks;
import cn.foggyhillside.ends_delight.registry.ModItems;
import com.github.fossil.food.FoodMappings;
import vectorwing.farmersdelight.common.block.PieBlock;

/**
 * Ported from source's forge {@code EndDelightCompat} (End's Delight addon, modid
 * {@code ends_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly (33/33
 * added).
 *
 * <p>Verified directly against the resolved {@code ends-delight-2.5.1+neoforge.1.21.1.jar} via
 * javap: the registry classes are {@code cn.foggyhillside.ends_delight.registry.ModItems}/
 * {@code ModBlocks} (not {@code ItemRegistry}/{@code BlockRegistry} as originally assumed), and
 * every field was renamed from source's PascalCase (e.g. {@code AssortedSalad}) to
 * SCREAMING_SNAKE_CASE (e.g. {@code ASSORTED_SALAD}) - all 33 items still exist under the new
 * naming, none dropped. {@code ChorusFruitPieBlock} (backing {@code CHORUS_FRUIT_PIE}) confirmed
 * via javap to still extend Farmer's Delight's own {@code PieBlock} directly, so the cast below is
 * still valid.
 *
 * <p>Source imported {@code getPieValue} as a static import from this repo's
 * {@code com.github.fossil.compat.farmers.FarmersDelightCompat} - that method is {@code private}
 * there and this task must not edit anything outside the {@code farmers.addon} package, so the pie
 * value calculation (same 1.21.1 Farmer's Delight API used elsewhere in this repo) is duplicated
 * locally instead.
 */
public class EndDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addPlant(ModItems.ASSORTED_SALAD.get());
        FoodMappings.addPlant(ModItems.CHORUS_COOKIE.get());
        FoodMappings.addPlant(ModItems.CHORUS_FRUIT_PIE.get(), getPieValue((PieBlock) ModBlocks.CHORUS_FRUIT_PIE.get()));
        FoodMappings.addPlant(ModItems.CHORUS_FLOWER_PIE.get());
        FoodMappings.addPlant(ModItems.CHORUS_FRUIT_PIE_SLICE.get());
        FoodMappings.addPlant(ModItems.CHORUS_FRUIT_GRAIN.get());
        FoodMappings.addPlant(ModItems.CHORUS_FRUIT_POPSICLE.get());
        FoodMappings.addPlant(ModItems.CHORUS_SUCCULENT.get());
        FoodMappings.addPlant(ModItems.DRAGON_BREATH_AND_CHORUS_SOUP.get());
        FoodMappings.addMeat(ModItems.DRAGON_LEG.get());
        FoodMappings.addMeat(ModItems.DRAGON_LEG_WITH_SAUCE.get());
        FoodMappings.addMeat(ModItems.DRAGON_MEAT_STEW.get());
        FoodMappings.addMeat(ModItems.DRIED_ENDERMITE_MEAT.get());
        FoodMappings.addMeat(ModItems.END_BARBECUE_STICK.get());
        FoodMappings.addMeat(ModItems.ENDER_CONGEE.get());
        FoodMappings.addPlant(ModItems.CHORUS_SAUCE.get());
        FoodMappings.addPlant(ModItems.END_MIXED_SALAD.get());
        FoodMappings.addEgg(ModItems.FRIED_DRAGON_EGG.get());
        FoodMappings.addMeat(ModItems.GRILLED_SHULKER.get());
        FoodMappings.addEgg(ModItems.LIQUID_DRAGON_EGG.get());
        FoodMappings.addMeat(ModItems.RAW_DRAGON_MEAT.get());
        FoodMappings.addMeat(ModItems.RAW_DRAGON_MEAT_CUTS.get());
        FoodMappings.addMeat(ModItems.RAW_ENDERMITE_MEAT.get());
        FoodMappings.addMeat(ModItems.ROASTED_DRAGON_MEAT.get());
        FoodMappings.addMeat(ModItems.ROASTED_DRAGON_MEAT_CUTS.get());
        FoodMappings.addMeat(ModItems.ROASTED_DRAGON_STEAK.get());
        FoodMappings.addMeat(ModItems.ROASTED_SHULKER_MEAT.get());
        FoodMappings.addMeat(ModItems.ROASTED_SHULKER_MEAT_SLICE.get());
        FoodMappings.addMeat(ModItems.SHULKER_MEAT.get());
        FoodMappings.addMeat(ModItems.SHULKER_MEAT_SLICE.get());
        FoodMappings.addMeat(ModItems.SMOKED_DRAGON_LEG.get());
        FoodMappings.addMeat(ModItems.STIR_FRIED_SHULKER_MEAT.get());
        FoodMappings.addPlant(ModItems.STUFFED_RICE_CAKE.get());
        FoodMappings.addPlant(ModItems.DRIED_CHORUS_FLOWER.get(), 5);
        FoodMappings.addEgg(ModItems.STEAMED_DRAGON_EGG.get());
        //33/33 added
    }

    /**
     * Duplicated from this repo's {@code FarmersDelightCompat#getPieValue} (kept {@code private}
     * there, and this file cannot reach outside {@code farmers.addon} to change that) - same 1.21.1
     * Farmer's Delight API: {@code PieBlock#getPieSliceItem()} returns an {@code ItemStack} directly,
     * and food data comes from the nullable-entity {@code ItemStack#getFoodProperties(LivingEntity)}
     * overload.
     */
    private static int getPieValue(PieBlock block) {
        var food = block.getPieSliceItem().getFoodProperties(null);
        return (food != null ? food.nutrition() : 0) * block.getMaxBites() * 5;
    }
}
