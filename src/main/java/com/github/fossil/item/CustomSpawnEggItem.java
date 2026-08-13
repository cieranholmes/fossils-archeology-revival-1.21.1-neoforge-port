package com.github.fossil.item;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class CustomSpawnEggItem extends DeferredSpawnEggItem {
    private final Component name;

    public CustomSpawnEggItem(PrehistoricEntityInfo info) {
        super(info.entitySupplier, info.backgroundEggColor, info.highlightEggColor, new Item.Properties());
        this.name = Component.translatable("item." + FossilsAndArcheologyRevival.MODID + ".spawn_egg_generic", info.displayName().get());
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return name;
    }
}
