package com.github.fossil.recipe;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class AnalyzerRecipeBuilder extends MultiOutputAndSlotsRecipeBuilder<AnalyzerRecipeBuilder> {
    public AnalyzerRecipeBuilder(ItemLike itemInput) {
        super(itemInput);
    }

    public AnalyzerRecipeBuilder(TagKey<Item> tagInput) {
        super(tagInput);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, new AnalyzerRecipe(buildIngredient(), buildOutputs()), null);
    }

    @Override
    protected ResourceLocation getDefaultRecipeId() {
        if (itemInput != null) {
            return FossilsAndArcheologyRevival.location("analyzer/" + BuiltInRegistries.ITEM.getKey(itemInput.asItem()).getPath());
        }
        return FossilsAndArcheologyRevival.location("analyzer/" + tagInput.location().getPath());
    }
}
