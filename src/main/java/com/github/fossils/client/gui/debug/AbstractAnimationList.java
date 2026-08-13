package com.github.fossils.client.gui.debug;

import com.github.fossils.entity.animation.AnimationInfo;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Ported from source's {@code AbstractAnimationList}. Source overrode {@code getEntryAtPosition(...)} to hit-test
 * entries against a custom {@code getEntryLeftPos()} hook (letting {@code AnimationTab}'s nested subclass shift
 * entries into a second "column" via {@code getEntryLeftPos() + rowWidth/2}). 1.21.1 sealed
 * {@code AbstractSelectionList.getEntryAtPosition(...)} as {@code final}, so that hook can no longer influence
 * hit-testing at all - entries are now always hit-tested (by the vanilla base) against the row centered in this
 * widget's own width, exactly where {@link #getEntryLeftPos()} now renders them by default (both derived from
 * {@link ContainerObjectSelectionList#getRowLeft()}), so clicks and visuals stay in sync. The one real behavior
 * loss: {@code AnimationTab}'s two-column layout hack (buttons drawn to the right of a spare left column) is no
 * longer possible - see the deviation noted on that class - but every animation is still listed and clickable.
 */
public class AbstractAnimationList extends DebugSelectionList<AbstractAnimationList.AnimationEntry> {
    private final Consumer<AnimationObject> consumer;
    protected String currentControllerName;
    protected double speed = 1;
    protected int transitionLength = 5;
    protected boolean loop;

    public AbstractAnimationList(int x0, int height, int itemHeight, int yOffset, Map<String, ? extends AnimationInfo> animations, Minecraft minecraft, Consumer<AnimationObject> function) {
        super(minecraft, 100, 215, height, yOffset, height + 60, itemHeight);
        this.setX(x0);
        List<String> sortedAnimations = animations.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getKey).toList();
        sortedAnimations.forEach(animation -> addEntry(new AnimationEntry(animation)));
        this.consumer = function;
    }

    protected int getEntryLeftPos() {
        return getRowLeft();
    }

    @Override
    protected int getScrollbarPosition() {
        return getRowLeft() + rowWidth + 6;
    }

    protected class AnimationEntry extends ContainerObjectSelectionList.Entry<AnimationEntry> {
        private final Button changeButton;

        AnimationEntry(String animation) {
            String[] split = animation.split("\\.");
            Component display = Component.literal(split.length > 0 ? StringUtils.capitalize(split[split.length - 1]) : "");
            changeButton = Button.builder(display, button -> consumer.accept(new AnimationObject(animation, currentControllerName, speed, transitionLength, loop)))
                    .bounds(0, 0, 100, 20).build();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(changeButton);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver,
                           float partialTick) {
            changeButton.setX(getEntryLeftPos());
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

    public record AnimationObject(String name, String controller, double speed, int transitionLength, boolean loop) {

    }
}
