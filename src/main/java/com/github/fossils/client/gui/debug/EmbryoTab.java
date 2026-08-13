package com.github.fossils.client.gui.debug;

import com.github.fossils.Config;
import com.github.fossils.capabilities.ModCapabilities;
import com.github.fossils.network.debug.SyncDebugInfoMessage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Ported from source's {@code EmbryoTab}. Source read the pregnancy-duration config via {@code FossilConfig.getInt(
 * FossilConfig.PREGNANCY_DURATION)} (a string-keyed config lookup); target's {@code Config} has no such
 * string-keyed int lookup at all - config values are plain static fields populated on config load - so this uses
 * {@link Config#pregnancyDuration} directly.
 */
public class EmbryoTab extends DebugTab<Animal> {
    private int hatchingTime;

    protected EmbryoTab(DebugScreen debugScreen, Animal entity) {
        super(debugScreen, entity);
        hatchingTime = ModCapabilities.getEmbryoProgress(entity);
    }

    @Override
    protected void init(int width, int height) {
        super.init(width, height);
        int yPos = 0;

        DebugSlider hatchingTimeSlider = new DebugSlider(20, yPos += 30, 150, 20, Component.literal("Embryo time in ticks: "), Component.literal(""), 1, Config.pregnancyDuration, hatchingTime, 5, 0, true) {
            @Override
            protected void applyValue() {
                hatchingTime = (int) (stepSize * Math.round(Mth.lerp(value, minValue, maxValue) / stepSize));
            }
        };
        addWidget(hatchingTimeSlider);
        addWidget(Button.builder(Component.literal("Set Info"), button -> {
                    PacketDistributor.sendToServer(new SyncDebugInfoMessage(entity.getId(), "", hatchingTime, 0, 0, 0, 0, 0));
                })
                .bounds(20, yPos += 30, 150, 20)
                .tooltip(Tooltip.create(Component.literal("Set the info above on the server")))
                .build());
    }
}
