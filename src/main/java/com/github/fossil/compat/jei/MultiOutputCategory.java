package com.github.fossil.compat.jei;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.recipe.MultiOutputAndSlotsRecipe;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Shared JEI display for the Analyzer and Sifter machines: single item input, weighted-random ItemStack
 * output. Ported from the source mod's {@code MultiOutputCategory<T extends MultiOutputAndSlotsRecipe>}
 * implementing JEI's {@code IRecipeCategory<T>} directly.
 * <p>
 * API adaptation: JEI 19.24.0.317's {@link mezz.jei.api.recipe.RecipeType#createFromVanilla}/
 * {@code createFromDeferredVanilla} - the intended way to expose a vanilla-{@code Recipe}-backed recipe
 * type to JEI - wrap the recipe in {@link RecipeHolder}, so every recipe JEI hands back to this category is
 * a {@code RecipeHolder<T>}, not a bare {@code T} like the 1.20.1 source used. This class now implements
 * {@code IRecipeCategory<RecipeHolder<T>>} and unwraps with {@link RecipeHolder#value()} wherever the
 * source touched the recipe directly.
 * <p>
 * Weighted-output display: {@link MultiOutputAndSlotsRecipe#getWeightedOutputs()} returns a cumulative-weight
 * {@code NavigableMap<Double, ItemStack>} - this is never resolved via {@code assemble()} (which just picks
 * one random output), instead every entry is walked to compute each output's percentage chance
 * (consecutive cumulative-weight deltas / total), then laid out into a paged 5x3 grid of output slots
 * (identical layout math to source) so JEI shows every possible output at once, each with its own
 * probability label drawn on top of the slot.
 */
public abstract class MultiOutputCategory<T extends MultiOutputAndSlotsRecipe> implements IRecipeCategory<RecipeHolder<T>> {
    private static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/gui/multi_output.png");
    private static final DecimalFormat FORMAT = new DecimalFormat("#.#'%'");
    private final IDrawable background;
    private final IDrawable icon;
    private final LoadingCache<RecipeHolder<T>, List<SlotItem>> probabilities = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                private static final int COLUMNS = 5;
                private static final int ROWS = 3;

                @Override
                public @NotNull List<SlotItem> load(@NotNull RecipeHolder<T> recipeHolder) {//TODO: Cycle duration
                    T recipe = recipeHolder.value();
                    var iterator = recipe.getWeightedOutputs().entrySet().iterator();
                    List<WeightedItem> sortedOutputs = new ArrayList<>();
                    if (iterator.hasNext()) {
                        double total = recipe.getWeightedOutputs().lastKey();
                        var entry = iterator.next();
                        double previous = entry.getKey();
                        sortedOutputs.add(new WeightedItem(previous / total * 100, entry.getValue()));
                        while (iterator.hasNext()) {
                            entry = iterator.next();
                            sortedOutputs.add(new WeightedItem(((entry.getKey() - previous) / total) * 100, entry.getValue()));
                            previous = entry.getKey();
                        }
                    }
                    sortedOutputs.sort((o1, o2) -> DoubleComparators.OPPOSITE_COMPARATOR.compare(o1.probability, o2.probability));

                    int numPerPage = COLUMNS * ROWS;
                    Map<Integer, List<WeightedItem>> groupedOutputs = sortedOutputs.stream().collect(Collectors.groupingBy(weightedItem -> sortedOutputs.indexOf(weightedItem) % numPerPage));
                    List<SlotItem> newOutputs = new ArrayList<>();
                    for (int i = 0; i < groupedOutputs.size(); i++) {
                        int x = i % COLUMNS;// index - (row * width)
                        int y = i / COLUMNS;// int/int
                        newOutputs.add(new SlotItem(groupedOutputs.get(i), 8 + x * 30, 21 + y * 25));
                    }
                    return newOutputs;
                }
            });

    protected MultiOutputCategory(IGuiHelper guiHelper, ItemLike iconItem) {
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 152, 106);
        icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
    }

    @Override
    public void draw(@NotNull RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        List<SlotItem> slotItems = probabilities.getUnchecked(recipe);
        List<IRecipeSlotView> slots = recipeSlotsView.getSlotViews(RecipeIngredientRole.OUTPUT);
        for (int i = 0; i < slots.size(); i++) {
            Optional<ItemStack> currentStack = slots.get(i).getDisplayedItemStack();
            var slotItem = slotItems.get(i);
            if (currentStack.isEmpty()) {
                //Empty = Air = Nothing which is a valid output. We just need to check if there is a probability for nothing
                slotItem.items.stream().filter(weightedItem -> weightedItem.stack.isEmpty()).findFirst().ifPresent(weightedItem -> {
                    renderProbability(guiGraphics, weightedItem.probability, slotItem.x, slotItem.y);
                });
                continue;
            }
            for (WeightedItem item : slotItem.items) {
                if (ItemStack.isSameItem(currentStack.get(), item.stack)) {
                    renderProbability(guiGraphics, item.probability, slotItem.x, slotItem.y);
                    break;
                }
            }
        }
    }

    /**
     * Ported from the source's manual {@code Tesselator}/{@code MultiBufferSource} draw call - simplified to
     * {@link GuiGraphics#drawString} (this project's established GUI-rendering convention, see e.g.
     * {@code DinopediaScreen}), which handles its own buffer source and still honors the pushed pose-stack
     * z-translation used here to draw the percentage label above the item icon.
     */
    private void renderProbability(GuiGraphics guiGraphics, double percentage, int xPosition, int yPosition) {
        Font font = Minecraft.getInstance().font;
        String string = FORMAT.format(percentage);
        float xOffset = Math.min(11, font.width(string) / 2f);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
        guiGraphics.drawString(font, string, (int) (xPosition + 8 - xOffset), yPosition + 16 + 1, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> recipeHolder, IFocusGroup focuses) {
        T recipe = recipeHolder.value();
        int centerX = 150 / 2;
        builder.addSlot(RecipeIngredientRole.INPUT, centerX - 7, 2).addIngredients(recipe.getInput());
        List<SlotItem> outputs = probabilities.getUnchecked(recipeHolder);
        for (SlotItem output : outputs) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, output.x, output.y + 1)
                    .addItemStacks(output.items.stream().map(WeightedItem::stack).toList());
        }
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    public record WeightedItem(double probability, ItemStack stack) {

    }

    public record SlotItem(List<WeightedItem> items, int x, int y) {

    }
}
