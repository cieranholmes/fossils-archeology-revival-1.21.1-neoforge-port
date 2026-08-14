package com.github.fossil.compat.rei;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.recipe.SifterRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.NavigableMap;

public class SifterDisplay extends MultiOutputDisplay {
    public static final CategoryIdentifier<SifterDisplay> ID = CategoryIdentifier.of(FossilsAndArcheologyRevival.location("sifter"));

    public SifterDisplay(RecipeHolder<SifterRecipe> recipeHolder) {
        super(recipeHolder);
    }

    public SifterDisplay(EntryIngredient input, NavigableMap<Double, EntryIngredient> output, @Nullable ResourceLocation location) {
        super(input, output, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}
