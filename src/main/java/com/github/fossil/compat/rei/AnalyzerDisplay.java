package com.github.fossil.compat.rei;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.recipe.AnalyzerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class AnalyzerDisplay extends MultiOutputDisplay {
    public static final CategoryIdentifier<AnalyzerDisplay> ID = CategoryIdentifier.of(FossilsAndArcheologyRevival.location("analyzer"));

    public AnalyzerDisplay(RecipeHolder<AnalyzerRecipe> recipeHolder) {
        super(recipeHolder);
    }

    public AnalyzerDisplay(EntryIngredient input, NavigableMap<Double, EntryIngredient> output, @Nullable ResourceLocation location) {
        super(input, output, location);
    }

    public AnalyzerDisplay(AnalyzerTagRecipe analyzerTagRecipe) {
        super(EntryIngredients.ofItemTag(analyzerTagRecipe.input()), map(analyzerTagRecipe.weightedOutputs()), null);
    }

    private static NavigableMap<Double, EntryIngredient> map(NavigableMap<Double, Ingredient> outputs) {
        NavigableMap<Double, EntryIngredient> newMap = new TreeMap<>();
        for (Map.Entry<Double, Ingredient> entry : outputs.entrySet()) {
            newMap.put(entry.getKey(), EntryIngredients.ofItemStacks(List.of(entry.getValue().getItems())));
        }
        return newMap;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
