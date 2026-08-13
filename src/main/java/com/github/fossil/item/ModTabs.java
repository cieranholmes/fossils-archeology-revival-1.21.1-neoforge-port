package com.github.fossil.item;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FossilsAndArcheologyRevival.MODID);

    private static final List<Supplier<? extends ItemLike>> BLOCK_TAB_ITEMS = new ArrayList<>();
    private static final List<Supplier<? extends ItemLike>> MOB_ITEM_TAB_ITEMS = new ArrayList<>();
    private static final List<Supplier<? extends ItemLike>> OTHER_ITEM_TAB_ITEMS = new ArrayList<>();

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FA_BLOCK_TAB = register("fa_block_tab",
            BLOCK_TAB_ITEMS, () -> ModBlocks.WORKTABLE.get().asItem().getDefaultInstance());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FA_MOB_ITEM_TAB = register("fa_mob_item_tab",
            MOB_ITEM_TAB_ITEMS, () -> Items.EGG.getDefaultInstance());
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FA_OTHER_ITEM_TAB = register("fa_other_item_tab",
            OTHER_ITEM_TAB_ITEMS, () -> ModItems.TAR_BUCKET.get().getDefaultInstance());

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(String id, List<Supplier<? extends ItemLike>> items,
                                                                              Supplier<net.minecraft.world.item.ItemStack> icon) {
        return TABS.register(id, () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + FossilsAndArcheologyRevival.MODID + "." + id))
                .icon(icon)
                .displayItems((parameters, output) -> items.forEach(item -> output.accept(item.get())))
                .build());
    }

    public static void addToBlockTab(Supplier<? extends ItemLike> item) {
        BLOCK_TAB_ITEMS.add(item);
    }

    public static void addToMobItemTab(Supplier<? extends ItemLike> item) {
        MOB_ITEM_TAB_ITEMS.add(item);
    }

    public static void addToOtherItemTab(Supplier<? extends ItemLike> item) {
        OTHER_ITEM_TAB_ITEMS.add(item);
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
