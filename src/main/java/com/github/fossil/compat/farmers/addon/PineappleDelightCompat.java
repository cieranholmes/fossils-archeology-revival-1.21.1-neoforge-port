package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import com.github.fossil.food.FoodType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * Ported from source's forge {@code PineappleDelightCompat} (Pineapple Delight addon, modid
 * {@code pineapple_delight}). Drops the {@code FoodMappingsManager} parameter - calls the static
 * {@link FoodMappings} methods directly instead. Every mapping entry preserved exactly.
 *
 * <p>Source's dynamic {@code BuiltInRegistries...getOptional(new ResourceLocation(modId, path))}
 * lookup style is kept (rather than hardcoding item/block class references) since that's how source
 * itself avoided a hard compile-time dependency on the addon. Updated {@code new ResourceLocation(...)}
 * to this repo's {@code ResourceLocation.fromNamespaceAndPath(...)} convention (source's
 * two-arg constructor is deprecated in 1.21.1; see this repo's {@code ModItemTags}/{@code
 * ModBlockTags} for the same pattern already in use elsewhere).
 *
 * <p>Web audit confirmed a NeoForge 1.21.1 build exists (Modrinth/CurseForge {@code
 * pineapple-delight}, e.g. version {@code 1.1.2-1.21.1-neoforge}) and confirmed the modid as
 * {@code pineapple_delight}, matching source. Because this file only does dynamic registry lookups
 * (no compile-time class references into the addon's own code), it does not actually need the
 * addon as a compile dependency at all - it will resolve/compile standalone.
 */
public class PineappleDelightCompat {
    public static void registerFoodMappings() {
        String modId = "pineapple_delight";
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_fried_rice")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_pie_side")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_side")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_pie")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_cake_slice")).ifPresent(FoodMappings::addPlant);
        BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_pie")).ifPresent(block -> {
            FoodMappings.addPlant(block.asItem(), 4 * FoodType.PLANT.multiplier());
        });
        BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_cake")).ifPresent(block -> {
            FoodMappings.addPlant(block.asItem(), 3 * 4 * FoodType.PLANT.multiplier());
        });
        BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_crop")).ifPresent(block -> {
            FoodMappings.addPlant(block.asItem(), FoodType.PLANT.multiplier());
        });
        BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath(modId, "pineapple_wild_crop")).ifPresent(block -> {
            FoodMappings.addPlant(block.asItem(), FoodType.PLANT.multiplier());
        });
    }
}
