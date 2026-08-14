package com.github.fossil.compat.rei;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.recipe.WorktableRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WorktableDisplay extends WithFuelDisplay {
    public static final CategoryIdentifier<WorktableDisplay> ID = CategoryIdentifier.of(FossilsAndArcheologyRevival.location("worktable"));

    public WorktableDisplay(RecipeHolder<WorktableRecipe> recipeHolder) {
        super(recipeHolder);
    }

    protected WorktableDisplay(EntryIngredient input, EntryIngredient fuel, EntryStack<?> output, ResourceLocation location) {
        super(input, fuel, output, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
