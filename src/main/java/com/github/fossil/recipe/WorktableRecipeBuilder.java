package com.github.fossil.recipe;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.inventory.WorktableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class WorktableRecipeBuilder extends WithFuelRecipeBuilder {
    public WorktableRecipeBuilder(ItemLike itemInput, ItemLike itemFuel, ItemLike itemOutput) {
        this(itemInput, itemFuel, itemOutput, WorktableMenu.DEFAULT_DURATION);
    }

    public WorktableRecipeBuilder(ItemLike itemInput, ItemLike itemFuel, ItemLike itemOutput, int duration) {
        super(itemInput, itemFuel, itemOutput, duration);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, new WorktableRecipe(Ingredient.of(itemInput), Ingredient.of(itemFuel),
                new ItemStack(itemOutput), duration, WorktableMenu.DEFAULT_FUEL_DURATION), null);
    }

    @Override
    protected ResourceLocation getDefaultRecipeId() {
        return FossilsAndArcheologyRevival.location("worktable/" + BuiltInRegistries.ITEM.getKey(itemInput.asItem()).getPath()
                + "_with_" + BuiltInRegistries.ITEM.getKey(itemFuel.asItem()).getPath());
    }
}
