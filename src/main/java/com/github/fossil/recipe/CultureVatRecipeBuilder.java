package com.github.fossil.recipe;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.inventory.CultureVatMenu;
import com.github.fossil.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CultureVatRecipeBuilder extends WithFuelRecipeBuilder {
    public CultureVatRecipeBuilder(ItemLike itemInput, ItemLike itemOutput) {
        //TODO: Duration not actually used - matches source, which never serialized a custom duration/
        // fuel_duration for Culture Vat recipes either (see WithFuelRecipeSerializer's optionalFieldOf
        // defaults), so datagen-authored Culture Vat recipes always run at CultureVatMenu's defaults.
        this(itemInput, ModItems.BIO_GOO.get(), itemOutput, CultureVatMenu.CULTIVATION_DURATION);
    }

    public CultureVatRecipeBuilder(ItemLike itemInput, ItemLike itemFuel, ItemLike itemOutput, int duration) {
        super(itemInput, itemFuel, itemOutput, duration);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        recipeOutput.accept(id, new CultureVatRecipe(Ingredient.of(itemInput), Ingredient.of(itemFuel),
                new ItemStack(itemOutput), duration, CultureVatMenu.DEFAULT_FUEL_DURATION), null);
    }

    @Override
    protected ResourceLocation getDefaultRecipeId() {
        return FossilsAndArcheologyRevival.location("culture_vat/" + BuiltInRegistries.ITEM.getKey(itemInput.asItem()).getPath());
    }
}
