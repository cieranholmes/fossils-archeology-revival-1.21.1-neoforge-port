package com.github.fossil.compat.rei;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.scroll.ScrollingContainer;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.widgets.*;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base category for the two "single item input, weighted-random output" machines (Analyzer, Sifter).
 * <p>
 * Ported from the source mod's REI plugin nearly verbatim, with one modernization: the source's
 * {@code renderProbability}/{@code renderFixedScrollbar} methods hand-rolled raw {@code Tesselator}/
 * {@code BufferBuilder}/{@code RenderSystem} vertex-buffer code (working around a z-fighting bug in Cloth
 * Config's own {@code ScrollingContainer#renderScrollBar} at the time). 1.21.1's rendering pipeline no
 * longer supports that immediate-mode vertex API in this shape, and Cloth Config's current
 * {@code ScrollingContainer#renderScrollBar} is itself now implemented with plain {@code GuiGraphics#fill}
 * calls (no z-fighting issue left to work around) - so the probability label now uses
 * {@code GuiGraphics#drawString} and the scrollbar delegates directly to the library's own renderer,
 * instead of re-implementing either by hand.
 */
public abstract class MultiOutputCategory implements DisplayCategory<MultiOutputDisplay> {

    @Override
    public int getDisplayHeight() {
        return 106;
    }

    @Override
    public List<Widget> setupDisplay(MultiOutputDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getMinX(), bounds.getMinY());
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(new Point(bounds.getCenterX() - 8, startPoint.y + 5)).entries(display.getInputEntries().get(0)).markInput());

        Rectangle rectangle = new Rectangle(bounds.getCenterX() - (bounds.width / 2) + 4, bounds.y + 23, bounds.width - 8, bounds.height - 28);
        widgets.add(Widgets.createSlotBase(rectangle));

        widgets.add(new ScrollableSlotsWidget(rectangle, display.outputs));
        return widgets;
    }


    private static class ScrollableSlotsWidget extends WidgetWithBounds {
        private static final float DEFAULT_SPACE = 25;
        private static final DecimalFormat FORMAT = new DecimalFormat("#.#'%'");
        private final Rectangle boundsRect;
        private final Map<Slot, Double> probabilities;
        private final List<Slot> widgets;
        private final ScrollingContainer scrolling = new ScrollingContainer() {
            private boolean draggingScrollBar;

            @Override
            public Rectangle getBounds() {
                Rectangle bounds = MultiOutputCategory.ScrollableSlotsWidget.this.getBounds();
                return new Rectangle(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
            }

            @Override
            public int getMaxScrollHeight() {
                double numPerRow = Math.floor(boundsRect.width / DEFAULT_SPACE);
                return Mth.ceil(widgets.size() / numPerRow) * 24 + 4;
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy, boolean snapToRows, double rowSize) {
                if (button == 0 && this.draggingScrollBar) {
                    float height = getMaxScrollHeight();
                    Rectangle bounds = getBounds();
                    int actualHeight = bounds.height;
                    if (mouseY >= bounds.y && mouseY <= bounds.getMaxY()) {
                        double maxScroll = Math.max(1, getMaxScroll());
                        double int3 = Mth.clamp((double) (actualHeight * actualHeight) / (double) height, 32.0, (actualHeight - 8));
                        double double6 = Math.max(1.0, maxScroll / (actualHeight - int3));
                        float to = Mth.clamp((float) (scrollAmount() + dy * double6), 0.0F, getMaxScroll());
                        if (snapToRows) {
                            double nearestRow = Math.round(to / rowSize) * rowSize;
                            scrollTo(nearestRow, false);
                        } else {
                            scrollTo(to, false);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean updateDraggingState(double mouseX, double mouseY, int button) {
                if (!hasScrollBar()) {
                    return false;
                } else {
                    //Fix cloth config scrolling
                    int height = getMaxScroll() + boundsRect.height;
                    Rectangle bounds = getBounds();
                    int actualHeight = bounds.height;
                    if (height > actualHeight && mouseY >= bounds.y && mouseY <= bounds.getMaxY()) {
                        double scrollbarPositionMinX = this.getScrollBarX(bounds.getMaxX());
                        if (mouseX >= scrollbarPositionMinX - 1.0 && mouseX <= scrollbarPositionMinX + 8.0) {
                            this.draggingScrollBar = true;
                            return true;
                        }
                    }

                    this.draggingScrollBar = false;
                    return false;
                }
            }
        };

        public ScrollableSlotsWidget(Rectangle bounds, List<MultiOutputDisplay.WeightedItem> outputs) {
            this.boundsRect = Objects.requireNonNull(bounds);
            this.probabilities = new Object2DoubleOpenHashMap<>();
            this.widgets = new ArrayList<>();
            for (MultiOutputDisplay.WeightedItem weightedItem : outputs) {
                Slot slot = Widgets.createSlot(new Point(0, 0)).disableBackground().entries(weightedItem.item()).markOutput();
                widgets.add(slot);
                probabilities.put(slot, weightedItem.probability());
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            if (containsMouse(mouseX, mouseY)) {
                scrolling.offset(ClothConfigInitializer.getScrollStep() * -scrollY, true);
                return true;
            }
            return false;
        }

        @Override
        public Rectangle getBounds() {
            return boundsRect;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (scrolling.updateDraggingState(mouseX, mouseY, button))
                return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (scrolling.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                return true;
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        private double calcActualSpace(Rectangle innerBounds, double numPerRow) {
            if (widgets.size() <= numPerRow) {
                return DEFAULT_SPACE;
            }
            return Math.floor(innerBounds.width / numPerRow);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            scrolling.updatePosition(delta);
            Rectangle innerBounds = scrolling.getScissorBounds();
            try (CloseableScissors ignored = scissor(guiGraphics, innerBounds)) {
                double numPerRow = Math.floor(innerBounds.width / DEFAULT_SPACE);
                double actualSpace = calcActualSpace(innerBounds, numPerRow);
                double xOffset = (actualSpace - 18f) / 2;
                for (int y = 0; y < Math.ceil(widgets.size() / numPerRow); y++) {
                    for (int x = 0; x < numPerRow; x++) {
                        int index = (int) (y * numPerRow + x);
                        if (widgets.size() <= index)
                            break;
                        Slot widget = widgets.get(index);
                        widget.getBounds().setLocation(boundsRect.x + xOffset + x * actualSpace, boundsRect.y + 1 + y * DEFAULT_SPACE - scrolling.scrollAmountInt());
                        widget.render(guiGraphics, mouseX, mouseY, delta);
                        renderProbability(guiGraphics, probabilities.get(widget), widget.getBounds().x, widget.getBounds().y);
                    }
                }
            }
            try (CloseableScissors ignored = scissor(guiGraphics, scrolling.getBounds())) {
                scrolling.renderScrollBar(guiGraphics, 0xff000000, 1, REIRuntime.getInstance().isDarkThemeEnabled() ? 0.8f : 1f);
            }
        }

        /**
         * Renders the drop-chance percentage label under an output slot.
         */
        private void renderProbability(GuiGraphics guiGraphics, double probability, int xPosition, int yPosition) {
            String string = FORMAT.format(probability);
            Minecraft minecraft = Minecraft.getInstance();
            float xOffset = Math.min(11, minecraft.font.width(string) / 2f);
            guiGraphics.drawString(minecraft.font, string, xPosition + 8 - xOffset, yPosition + 16 + 1, 16777215, true);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return widgets;
        }
    }
}
