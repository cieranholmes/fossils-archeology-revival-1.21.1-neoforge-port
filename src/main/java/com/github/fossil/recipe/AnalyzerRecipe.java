package com.github.fossil.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;
import java.util.TreeMap;

public class AnalyzerRecipe extends MultiOutputAndSlotsRecipe {

    public AnalyzerRecipe(Ingredient input, NavigableMap<Double, ItemStack> weightedOutputs) {
        super(input, weightedOutputs);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AnalyzerRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }
    }

    public static class Serializer extends MultiOutputAndSlotsRecipe.Serializer<AnalyzerRecipe> {
        public static final AnalyzerRecipe.Serializer INSTANCE = new AnalyzerRecipe.Serializer(AnalyzerRecipe::new);

        public Serializer(Constructor<AnalyzerRecipe> constructor) {
            super(constructor);
        }
    }

    /**
     * Programmatic builder for the compile-time-known bone/DNA/food analyzer recipes registered directly
     * into {@link ModRecipes#ANALYZER_RECIPES} by {@link ModRecipes#initRecipes()} - these never go through
     * datapack loading/RecipeManager at all, so unlike {@link AnalyzerRecipeBuilder} (the datagen JSON
     * builder), this one builds a ready-to-use {@link AnalyzerRecipe} instance directly and needs no id.
     */
    public static class Builder {

        public final ItemLike item;
        private final NavigableMap<Double, ItemStack> map = new TreeMap<>();
        public double total;

        public Builder(ItemLike item) {
            this.item = item;
        }

        public Builder addOutput(ItemLike itemLike, double weight) {
            total += weight;
            map.put(total, new ItemStack(itemLike));
            return this;
        }

        public AnalyzerRecipe build() {
            return new AnalyzerRecipe(Ingredient.of(item), map);
        }
    }
}
