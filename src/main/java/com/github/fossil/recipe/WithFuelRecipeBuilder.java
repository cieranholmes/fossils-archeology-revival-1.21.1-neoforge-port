package com.github.fossil.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

/**
 * Datagen builder base for {@link WithFuelRecipe} subclasses (Culture Vat, Worktable). See
 * {@link MultiOutputAndSlotsRecipeBuilder} for the general 1.21.1 datagen rewrite notes (no more
 * {@code FinishedRecipe}, {@code modId} constructor param dropped).
 */
public abstract class WithFuelRecipeBuilder implements RecipeBuilder {
    protected final ItemLike itemInput;
    protected final ItemLike itemFuel;
    protected final ItemLike itemOutput;
    protected final int duration;

    protected WithFuelRecipeBuilder(ItemLike itemInput, ItemLike itemFuel, ItemLike itemOutput, int duration) {
        this.itemInput = itemInput;
        this.itemFuel = itemFuel;
        this.itemOutput = itemOutput;
        this.duration = duration;
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
        // Method is unused
        return itemOutput.asItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        save(recipeOutput, getDefaultRecipeId());
    }

    protected abstract ResourceLocation getDefaultRecipeId();
}
