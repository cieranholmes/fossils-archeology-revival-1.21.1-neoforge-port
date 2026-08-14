package com.github.fossil.recipe;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class SifterRecipeBuilder extends MultiOutputAndSlotsRecipeBuilder<SifterRecipeBuilder> {
    public SifterRecipeBuilder(ItemLike itemInput) {
        super(itemInput);
    }

    public SifterRecipeBuilder(TagKey<Item> tagInput) {
        super(tagInput);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, new SifterRecipe(buildIngredient(), buildOutputs()), null);
    }

    @Override
    protected ResourceLocation getDefaultRecipeId() {
        if (itemInput != null) {
            return FossilsAndArcheologyRevival.location("sifter/" + BuiltInRegistries.ITEM.getKey(itemInput.asItem()).getPath());
        }
        return FossilsAndArcheologyRevival.location("sifter/" + tagInput.location().getPath());
    }
}
