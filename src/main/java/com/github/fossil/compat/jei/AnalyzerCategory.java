package com.github.fossil.compat.jei;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.recipe.AnalyzerRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class AnalyzerCategory extends MultiOutputCategory<AnalyzerRecipe> {
    public AnalyzerCategory(IGuiHelper guiHelper) {
        super(guiHelper, ModBlocks.ANALYZER.get());
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("emi.category.fossil.analyzer");
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<AnalyzerRecipe>> getRecipeType() {
        return FossilJEIPlugin.ANALYZER.get();
    }
}
