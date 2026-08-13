package com.github.fossils.client.gui.debug;

import com.github.fossils.entity.PrehistoricSkeleton;
import com.github.fossils.entity.data.EntityDataLoader;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.network.debug.SyncDebugInfoMessage;
import com.github.fossils.util.TimePeriod;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkeletonEditTab extends DebugTab<PrehistoricSkeleton> {
    private final int maxAge;
    private int age;
    private PrehistoricEntityInfo info;


    protected SkeletonEditTab(DebugScreen debugScreen, PrehistoricSkeleton entity) {
        super(debugScreen, entity);
        this.maxAge = entity.data().adultAgeDays();
        this.age = entity.getAge();
        this.info = entity.info();
    }

    @Override
    protected void init(int width, int height) {
        DebugSlider ageSlider;
        super.init(width, height);
        ageSlider = new DebugSlider(20, 30, 150, 20, Component.literal("Age: "), Component.literal(""), 0, maxAge, age, 1, 0, true) {
            @Override
            protected void applyValue() {
                age = (int) (stepSize * Math.round(Mth.lerp(value, minValue, maxValue) / stepSize));
            }
        };
        addWidget(ageSlider);
        addWidget(new ModelsList());
        addWidget(Button.builder(Component.literal("Set Info"), button -> {
            PacketDistributor.sendToServer(new SyncDebugInfoMessage(entity.getId(), info.name(), age, 0, 0, 0, 0, 0));
            ageSlider.maxValue = EntityDataLoader.INSTANCE.getData(info.resourceName).adultAgeDays();
            age = (int) Math.min(age, ageSlider.maxValue);
            ageSlider.setValue(age);
        }).bounds(20, 210, 150, 20).build());
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(minecraft.font, Component.literal("Age: " + entity.getAge()), 175, 35, 16777215);
        guiGraphics.drawString(minecraft.font, Component.literal("Type: " + entity.info().name()), 175, 185, 16777215);
    }

    /**
     * 1.21.1's {@code ContainerObjectSelectionList} constructor dropped its {@code y1} parameter (now
     * {@code (minecraft, width, height, y, itemHeight)} instead of source's {@code (minecraft, width, height, y0,
     * y1, itemHeight)}) and no longer exposes {@code x0}/{@code x1} fields - {@code setX(int)} replaces them, and
     * {@code setRenderBackground(false)}/{@code setRenderTopAndBottom(false)} become empty overrides of
     * {@code renderListBackground}/{@code renderListSeparators} (same pattern as {@code instruction.EntityList}).
     */
    private class ModelsList extends ContainerObjectSelectionList<ModelsList.ModelEntry> {

        public ModelsList() {
            super(SkeletonEditTab.this.minecraft, 200, SkeletonEditTab.this.height - 60, 60, 25);
            List<String> sortedModels = PrehistoricEntityInfo.entitiesWithSkeleton(TimePeriod.values()).stream().map(Enum::name).toList();
            for (String typeName : sortedModels) {
                addEntry(new ModelEntry(typeName));
            }
            this.setX(SkeletonEditTab.this.width - SkeletonEditTab.this.width / 4);
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {
        }

        @Override
        protected void renderListSeparators(GuiGraphics guiGraphics) {
        }

        @Override
        protected int getScrollbarPosition() {
            return getX() + width - 6;
        }

        private class ModelEntry extends ContainerObjectSelectionList.Entry<ModelEntry> {
            private final Button changeButton;

            ModelEntry(String text) {
                changeButton = Button.builder(Component.literal(text), button -> {
                    SkeletonEditTab.this.info = PrehistoricEntityInfo.valueOf(button.getMessage().getString());
                }).bounds(0, 0, 200, 20).build();
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return ImmutableList.of(changeButton);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver,
                               float partialTick) {
                changeButton.setX(left);
                changeButton.setY(top);
                changeButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return ImmutableList.of(changeButton);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return changeButton.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public boolean mouseReleased(double mouseX, double mouseY, int button) {
                return changeButton.mouseReleased(mouseX, mouseY, button);
            }
        }
    }
}
