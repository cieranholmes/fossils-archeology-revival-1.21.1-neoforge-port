package com.github.fossils.item;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.material.ModFluids;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FossilsAndArcheologyRevival.MODID);

    public static final DeferredItem<Item> TAR_BUCKET = register("tar_bucket",
            () -> new BucketItem(ModFluids.TAR.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> SKULL_STICK = register("skull_stick",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> WHIP = register("whip",
            WhipItem::new,
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> CHICKEN_ESSENCE = register("essence_chicken",
            () -> new DescriptiveItem(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> STUNTED_ESSENCE = register("essence_stunted",
            () -> new DescriptiveItem(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> SCARAB_GEM = register("scarab_gem",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> AQUATIC_SCARAB_GEM = register("scarab_gem_aquatic",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> MAGIC_CONCH = register("magic_conch",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> ELASMOTHERIUM_FUR = register("fur_elasmotherium",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> MAMMOTH_FUR = register("fur_mammoth",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    //Placeholders pending the real AncientSwordItem/AncientHelmetItem/ModArmorMaterials.ANCIENT port (Phase 2/Items unstarted)
    public static final DeferredItem<Item> ANCIENT_SWORD = register("ancient_sword",
            () -> new SwordItem(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4f))),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> ANCIENT_HELMET = register("ancient_helmet",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> ANCIENT_KEY = register("ancient_key",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    private static DeferredItem<Item> register(String name, Supplier<Item> item, Consumer<DeferredItem<Item>> tab) {
        DeferredItem<Item> registered = ITEMS.register(name, item);
        tab.accept(registered);
        return registered;
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
