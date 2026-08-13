package com.github.fossil.material;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.item.ModItems;
import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * ArmorMaterial became a registry entry (record) in 1.21.1 instead of an interface an enum could implement - the old
 * per-type "durability multiplier" (ANCIENT 9, BONE 15) no longer lives on the material itself (ArmorItem.Type now
 * carries a fixed base durability per slot); callers must multiply it in explicitly via
 * {@code ArmorItem.Type.X.getDurability(multiplier)} when building each armor item's Item.Properties, matching how
 * vanilla's own Items.java registers e.g. IRON_HELMET. See ANCIENT_DURABILITY_MULTIPLIER/BONE_DURABILITY_MULTIPLIER below.
 */
public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, FossilsAndArcheologyRevival.MODID);

    //durabilityMultiplier: ArmorItem.Type.X.getDurability(multiplier) at each armor item's registration site
    //(1.21.1's ArmorMaterial record dropped the per-material durability multiplier vanilla used to carry).
    public static final int ANCIENT_DURABILITY_MULTIPLIER = 15;
    public static final int BONE_DURABILITY_MULTIPLIER = 25;

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANCIENT = register("ancient",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.HELMET, 2);
            }),
            9, 0, 0,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            () -> Ingredient.of(ModItems.SCARAB_GEM.get()),
            //Ancient armor is fully GeckoLib-rendered (AncientHelmetRenderer/GeoArmorRenderer) - no flat vanilla
            //armor-layer texture is used, so there's nothing to list here.
            List.of());

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BONE = register("bone",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.HELMET, 2);
            }),
            15, 0, 0,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            () -> Ingredient.of(Items.BONE),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(FossilsAndArcheologyRevival.MODID, "bone"))));

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue, float toughness, float knockbackResistance, Holder<net.minecraft.sounds.SoundEvent> equipSound,
            Supplier<Ingredient> repairIngredient, List<ArmorMaterial.Layer> layers) {
        EnumMap<ArmorItem.Type, Integer> fullDefense = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            fullDefense.put(type, defense.getOrDefault(type, 0));
        }
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(fullDefense, enchantmentValue, equipSound, Suppliers.memoize(repairIngredient::get), layers, toughness, knockbackResistance));
    }

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ARMOR_MATERIALS.register(modEventBus);
    }
}
