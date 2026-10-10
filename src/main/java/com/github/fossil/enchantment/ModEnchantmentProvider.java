package com.github.fossil.enchantment;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * {@link ModEnchantments}' actual JSON definitions (data/fossil/enchantment/{archeology,paleontology}.json) are
 * the shipped source of truth - this bootstrap only exists because datagen's {@code HolderLookup.Provider}
 * (built by vanilla's {@code VanillaRegistries}, see {@code net.minecraft.world.item.enchantment.Enchantments#bootstrap})
 * never reads mod resource JSON, so any datagen code resolving these enchantments (loot tables referencing
 * archeology/paleontology levels) needs a matching Java-side registration to be present in that registry set.
 * Mirrors the JSON content field-for-field; wired in via {@code GatherDataEvent#createDatapackRegistryObjects}.
 */
public class ModEnchantmentProvider {

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        context.register(ModEnchantments.ARCHEOLOGY, Enchantment.enchantment(
                        Enchantment.definition(items.getOrThrow(ItemTags.PICKAXES), 1, 3,
                                Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(51, 10), 8, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.SILK_TOUCH), enchantments.getOrThrow(ModEnchantments.PALEONTOLOGY)))
                .build(ModEnchantments.ARCHEOLOGY.location()));

        context.register(ModEnchantments.PALEONTOLOGY, Enchantment.enchantment(
                        Enchantment.definition(items.getOrThrow(ItemTags.PICKAXES), 1, 3,
                                Enchantment.dynamicCost(1, 10), Enchantment.dynamicCost(51, 10), 8, EquipmentSlotGroup.MAINHAND))
                .exclusiveWith(HolderSet.direct(enchantments.getOrThrow(Enchantments.SILK_TOUCH), enchantments.getOrThrow(ModEnchantments.ARCHEOLOGY)))
                .build(ModEnchantments.PALEONTOLOGY.location()));
    }
}
