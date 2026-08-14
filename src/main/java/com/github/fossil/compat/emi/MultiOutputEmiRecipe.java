package com.github.fossil.compat.emi;

import com.github.fossil.recipe.MultiOutputAndSlotsRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleComparators;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Shared base for machine recipes with a single item input slot and a weighted-random single
 * output (Analyzer, Sifter). Ported from the source mod's EMI wrapper of the same name.
 * <p>
 * <b>API adaptation notes (EMI 1.1.22, {@code compileOnly ...:api} jar only):</b> {@code dev.emi.emi.EmiUtil}
 * and {@code dev.emi.emi.registry.EmiTags} are internal/non-api classes, present in the runtime jar but
 * stripped from the {@code api} classifier this project compiles against - unlike source's forge/fabric
 * split, which had compile-time access to EMI's full internal jar and used those classes (plus directly
 * constructed {@code TagEmiIngredient}/{@code ListEmiIngredient}/{@code ItemEmiStack}, also internal-only)
 * via the forge-only {@code MultiOutputEmiRecipeImpl}. That whole class plus source's
 * {@code @ExpectPlatform values}/{@code toIngredient} methods are collapsed into the plain private methods
 * below, built entirely from EMI's public factories ({@link EmiStack#of}, {@link EmiIngredient#of}) plus a
 * vanilla {@link BuiltInRegistries#ITEM} tag lookup in place of {@code EmiTags.getRawValues}.
 * <p>
 * Also: source's real-recipe constructor read {@code recipe.getId()} directly; target's
 * {@link MultiOutputAndSlotsRecipe} carries no id field at all any more (id/recipe pairing now lives on
 * {@link RecipeHolder}, and 1.21.1's {@code RecipeManager.getAllRecipesFor} returns
 * {@code List<RecipeHolder<T>>} instead of source's {@code List<T>}), so this takes a
 * {@code RecipeHolder<T>} in that constructor instead of a bare recipe.
 */
public abstract class MultiOutputEmiRecipe<T extends MultiOutputAndSlotsRecipe> implements EmiRecipe {
    private static final int COLUMNS = 5;
    private static final int ROWS = 3;
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final List<SlotItem> slots;

    protected MultiOutputEmiRecipe(RecipeHolder<T> holder) {
        T recipe = holder.value();
        this.id = holder.id();
        this.input = List.of(EmiIngredient.of(recipe.getInput()));
        this.output = recipe.getWeightedOutputs().values().stream().map(EmiStack::of).toList();
        NavigableMap<Double, Pair<ItemStack, TagKey<Item>>> newMap = new TreeMap<>();
        recipe.getWeightedOutputs().forEach((weight, itemStack) -> newMap.put(weight, Pair.of(itemStack, null)));
        this.slots = createOutputSlots(newMap);
    }

    protected MultiOutputEmiRecipe(@Nullable ResourceLocation id, TagKey<Item> input, NavigableMap<Double, Pair<ItemStack, TagKey<Item>>> map) {
        this.id = id;
        this.input = List.of(EmiIngredient.of(input));
        // Matches source exactly: for these Builder-based (compile-time-known) recipes, getOutputs() reuses
        // the *input* tag's members rather than anything derived from the output map - this looks like an
        // upstream copy-paste quirk (it only affects EMI's "used to craft"/"produced by" sidebar
        // cross-referencing, not the widget rendering below), kept faithful to source rather than silently
        // changed.
        this.output = tagValues(input);
        this.slots = createOutputSlots(map);
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 152;
    }

    @Override
    public int getDisplayHeight() {
        return 106;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        int centerX = 150 / 2;
        widgetHolder.addSlot(input.get(0), centerX - 7, 2);

        for (SlotItem slot : slots) {
            widgetHolder.add(new MultiOutputSlotWidget(slot.items, slot.x, slot.y)).recipeContext(this);
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    public record WeightedItem(double probability, ItemStack stack, @Nullable TagKey<Item> tagKey) {
    }

    public record SlotItem(EmiIngredient items, int x, int y) {
    }

    private static List<SlotItem> createOutputSlots(NavigableMap<Double, Pair<ItemStack, TagKey<Item>>> map) {
        var iterator = map.entrySet().iterator();
        List<WeightedItem> sortedOutputs = new ArrayList<>();
        if (iterator.hasNext()) {
            double total = map.lastKey();
            var entry = iterator.next();
            double previous = entry.getKey();
            sortedOutputs.add(new WeightedItem(previous / total, entry.getValue().key(), entry.getValue().value()));
            while (iterator.hasNext()) {
                entry = iterator.next();
                sortedOutputs.add(new WeightedItem((entry.getKey() - previous) / total, entry.getValue().key(), entry.getValue().value()));
                previous = entry.getKey();
            }
        }
        sortedOutputs.sort((o1, o2) -> DoubleComparators.OPPOSITE_COMPARATOR.compare(o1.probability, o2.probability));

        int numPerPage = COLUMNS * ROWS;
        Map<Integer, List<WeightedItem>> groupedOutputs = sortedOutputs.stream()
                .collect(Collectors.groupingBy(weightedItem -> sortedOutputs.indexOf(weightedItem) % numPerPage));
        List<SlotItem> newOutputs = new ArrayList<>();
        for (int i = 0; i < groupedOutputs.size(); i++) {
            int x = i % COLUMNS; // index - (row * width)
            int y = i / COLUMNS; // int/int
            newOutputs.add(new SlotItem(toIngredient(groupedOutputs.get(i)), 8 + x * 30, 21 + y * 27));
        }
        return newOutputs;
    }

    /** Vanilla-only replacement for source's {@code EmiTags.getRawValues(key)} (EMI-internal, non-api). */
    private static List<EmiStack> tagValues(TagKey<Item> tag) {
        List<EmiStack> stacks = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            stacks.add(EmiStack.of(holder.value()));
        }
        return stacks;
    }

    /**
     * Replacement for source's forge-only {@code MultiOutputEmiRecipeImpl.toIngredient}/{@code toStack},
     * built entirely from EMI's public {@link EmiIngredient#of} factories instead of directly constructing
     * the internal {@code TagEmiIngredient}/{@code ListEmiIngredient} classes (not on this project's
     * api-only compile classpath). Displays every possible weighted output for the slot, each stamped with
     * its own chance so {@link MultiOutputSlotWidget} can show the correct probability for whichever item
     * it's currently cycling through.
     */
    private static EmiIngredient toIngredient(List<WeightedItem> list) {
        if (list.isEmpty()) {
            return EmiStack.EMPTY;
        } else if (list.size() == 1) {
            WeightedItem weightedItem = list.get(0);
            if (weightedItem.tagKey() == null) {
                return EmiStack.of(weightedItem.stack()).setChance((float) weightedItem.probability());
            } else {
                // For custom recipe display: every item in the tag gets the same probability (the
                // probability of getting *anything* out of the tag), and the slot cycles through them all.
                List<EmiStack> stacks = tagValues(weightedItem.tagKey()).stream()
                        .map(stack -> stack.setChance((float) weightedItem.probability()))
                        .toList();
                return stacks.isEmpty() ? EmiStack.EMPTY : EmiIngredient.of(stacks, 1);
            }
        } else {
            return EmiIngredient.of(toIngredients(list), 1);
        }
    }

    private static List<EmiIngredient> toIngredients(List<WeightedItem> list) {
        return list.stream().<EmiIngredient>map(weightedItem -> {
            if (weightedItem.tagKey() == null) {
                return EmiStack.of(weightedItem.stack()).setChance((float) weightedItem.probability());
            } else {
                return EmiIngredient.of(weightedItem.tagKey()).setChance((float) weightedItem.probability());
            }
        }).toList();
    }

    private static class MultiOutputSlotWidget extends SlotWidget {

        public MultiOutputSlotWidget(EmiIngredient stack, int x, int y) {
            super(stack, x, y);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            super.render(guiGraphics, mouseX, mouseY, delta);
            Bounds bounds = getBounds();
            if (!getStack().getEmiStacks().isEmpty()) {
                int item = (int) (System.currentTimeMillis() / 1000 % getStack().getEmiStacks().size());
                EmiStack emiStack = getStack().getEmiStacks().get(item);
                renderProbability(guiGraphics, Minecraft.getInstance().font, emiStack.getChance() * 100, bounds.x() + 1, bounds.y() + 1);
            }
        }

        /**
         * Ported from source's manual {@code Tesselator}/{@code MultiBufferSource} batching (that API no
         * longer exists in 1.21.1 - {@code Tesselator} dropped its persistent {@code getBuilder()} in favor
         * of per-draw {@code begin(...)} calls). Uses {@link GuiGraphics#drawString} instead, which already
         * handles batching internally; the pose push/z-translate is kept so the percentage text still draws
         * on top of the item icon underneath it, matching source's intent.
         */
        private void renderProbability(GuiGraphics guiGraphics, Font font, double probability, int xPosition, int yPosition) {
            String string = new DecimalFormat("#.#'%'").format(probability);
            float xOffset = Math.min(11, font.width(string) / 2f);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0, 0.0, 300.0);
            guiGraphics.drawString(font, string, (int) (xPosition + 8 - xOffset), yPosition + 16 + 1, 4210752, false);
            guiGraphics.pose().popPose();
        }
    }
}
