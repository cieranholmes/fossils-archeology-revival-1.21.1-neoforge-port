package com.github.fossil.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Datagen builder for {@link MultiOutputAndSlotsRecipe} subclasses (Analyzer, Sifter). Ported from the
 * source mod's {@code RecipeBuilder}/{@code FinishedRecipe}-based builder - 1.21.1 datagen dropped
 * {@code FinishedRecipe} entirely (recipes now serialize themselves via {@link net.minecraft.world.item.crafting.RecipeSerializer#codec()}),
 * so {@code save(...)} now builds the real {@link MultiOutputAndSlotsRecipe} instance directly and hands
 * it to {@link RecipeOutput#accept} instead of producing an intermediate JSON-writer object. The
 * {@code modId} constructor parameter from the source (needed there for Architectury's multi-loader
 * setup) is dropped since this project is NeoForge-only.
 */
public abstract class MultiOutputAndSlotsRecipeBuilder<T extends MultiOutputAndSlotsRecipeBuilder<T>> implements RecipeBuilder {
    protected final ItemLike itemInput;
    protected final TagKey<Item> tagInput;
    protected final NavigableMap<Double, ItemStack> weightedOutputs = new TreeMap<>();
    protected double total;
    protected double nothingWeight;

    protected MultiOutputAndSlotsRecipeBuilder(ItemLike itemInput) {
        this.itemInput = itemInput;
        this.tagInput = null;
    }

    protected MultiOutputAndSlotsRecipeBuilder(TagKey<Item> tagInput) {
        this.itemInput = null;
        this.tagInput = tagInput;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T addOutput(ItemLike itemLike, double weight) {
        return addOutput(new ItemStack(itemLike), weight);
    }

    public T addOutput(ItemLike itemLike, int count, double weight) {
        return addOutput(new ItemStack(itemLike, count), weight);
    }

    public T addOutput(ItemStack stack, double weight) {
        total += weight;
        weightedOutputs.put(total, stack);
        return self();
    }

    /** Adds a chance of no output at all - mirrors the source JSON format's {@code total > sum(weights)} trick. */
    public T nothing(double weight) {
        nothingWeight = weight;
        return self();
    }

    public double getTotal() {
        return total + nothingWeight;
    }

    protected NavigableMap<Double, ItemStack> buildOutputs() {
        if (nothingWeight <= 0) {
            return weightedOutputs;
        }
        NavigableMap<Double, ItemStack> withNothing = new TreeMap<>(weightedOutputs);
        withNothing.put(total + nothingWeight, ItemStack.EMPTY);
        return withNothing;
    }

    protected Ingredient buildIngredient() {
        return itemInput != null ? Ingredient.of(itemInput) : Ingredient.of(tagInput);
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        // Method is unused - this recipe type has no single canonical result item
        return itemInput != null ? itemInput.asItem() : Items.ENDER_PEARL;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        save(recipeOutput, getDefaultRecipeId());
    }

    protected abstract ResourceLocation getDefaultRecipeId();
}
