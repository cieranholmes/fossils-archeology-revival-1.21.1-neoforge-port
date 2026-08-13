package com.github.fossils.client.gui.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ported from source's {@code DebugSelectionList}. 1.21.1 reworked {@code AbstractSelectionList}/
 * {@code ContainerObjectSelectionList} to extend {@code AbstractWidget} directly (they didn't in 1.20.1) - three
 * consequences handled here:
 * <ul>
 *     <li>The constructor lost its {@code y1} parameter - {@code AbstractSelectionList} now takes a single
 *     {@code height} (from {@code y} to {@code y + height}) instead of separate {@code y0}/{@code y1} bounds, so
 *     this class keeps source's exact 7-arg constructor shape for callers but converts internally
 *     ({@code height = y1 - y0}).</li>
 *     <li>{@code x0}/{@code x1} fields no longer exist - {@code setX(int)}/{@code getX()} (inherited from
 *     {@code AbstractWidget}) replace them; there's no analog for a separate {@code x1} since the base class derives
 *     row bounds from {@code getX()} + {@code width}.</li>
 *     <li>{@code AbstractWidget.render()} is now {@code final} (delegating to the abstract {@code renderWidget()}),
 *     so the extra-renderables hook moves from an override of source's {@code renderList(...)} (a method that no
 *     longer exists) to an override of {@code renderWidget(...)} that calls {@code super.renderWidget(...)} first.</li>
 * </ul>
 * {@code setRenderBackground(false)}/{@code setRenderTopAndBottom(false)} (source) become empty overrides of
 * {@code renderListBackground}/{@code renderListSeparators} (1.21.1) - same established pattern as
 * {@code instruction.EntityList}.
 */
public class DebugSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    protected final List<Renderable> renderables = new ArrayList<>();
    protected final List<GuiEventListener> listeners = new ArrayList<>();
    protected final int rowWidth;
    private GuiEventListener focused;

    public DebugSelectionList(Minecraft minecraft, int rowWidth, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, y1 - y0, y0, itemHeight);
        this.rowWidth = rowWidth;
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics) {
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        renderables.forEach(renderable -> renderable.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public @NotNull Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        for (GuiEventListener guiEventListener : listeners) {
            if (!guiEventListener.isMouseOver(mouseX, mouseY)) continue;
            return Optional.of(guiEventListener);
        }
        return super.getChildAt(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener guiEventListener : listeners) {
            if (!guiEventListener.mouseClicked(mouseX, mouseY, button)) continue;
            focused = guiEventListener;
            if (button == 0) {
                setDragging(true);
            }
            return true;
        }
        focused = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (focused != null && isDragging() && button == 0) {
            return focused.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    protected <T extends Renderable & GuiEventListener> void removeWidget(T widget) {
        renderables.remove(widget);
        listeners.remove(widget);
    }

    protected <T extends Renderable & GuiEventListener> T addWidget(T widget) {
        renderables.add(widget);
        listeners.add(widget);
        return widget;
    }
}
