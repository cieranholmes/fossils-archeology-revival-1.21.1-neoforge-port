package com.github.fossil.compat.jei;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.recipe.SifterRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class SifterCategory extends MultiOutputCategory<SifterRecipe> {
    public SifterCategory(IGuiHelper guiHelper) {
        super(guiHelper, ModBlocks.SIFTER.get());
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("emi.category.fossil.sifter");
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<SifterRecipe>> getRecipeType() {
        return FossilJEIPlugin.SIFTER.get();
    }
}
