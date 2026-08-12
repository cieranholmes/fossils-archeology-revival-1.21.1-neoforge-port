package com.github.fossils.entity.prehistoric.base;

import com.github.fossils.util.DinopediaInfo;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum OrderType implements DinopediaInfo {
    STAY, FOLLOW, WANDER;
    private final Component name = Component.translatable("pedia.fossils.order." + name().toLowerCase(Locale.ROOT));
    private final Component description = Component.translatable("pedia.fossils.order." + name().toLowerCase(Locale.ROOT) + ".desc");

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription() {
        return description;
    }
}
