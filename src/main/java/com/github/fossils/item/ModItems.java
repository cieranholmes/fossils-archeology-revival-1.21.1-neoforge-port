package com.github.fossils.item;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.material.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

    private static DeferredItem<Item> register(String name, Supplier<Item> item, Consumer<DeferredItem<Item>> tab) {
        DeferredItem<Item> registered = ITEMS.register(name, item);
        tab.accept(registered);
        return registered;
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
