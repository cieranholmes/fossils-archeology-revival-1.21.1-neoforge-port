package com.github.fossil.compat.jei;

import com.github.fossil.recipe.WithFuelRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared JEI display for the Culture Vat and Worktable machines: one input slot, one fuel slot, one fixed
 * output. Ported from the source's {@code WithFuelCategory<T extends WithFuelRecipe>}.
 * <p>
 * Same {@link RecipeHolder} wrapping adaptation as {@link MultiOutputCategory} - JEI's
 * {@code RecipeType.createFromDeferredVanilla} hands back {@code RecipeHolder<T>}, not a bare {@code T}, so
 * this implements {@code IRecipeCategory<RecipeHolder<T>>} and unwraps with {@link RecipeHolder#value()}.
 * <p>
 * The fixed output uses {@link mezz.jei.api.gui.builder.IIngredientAcceptor#addItemStack(ItemStack)} directly
 * (available on the current API) rather than the source's {@code Ingredient.of(recipe.getResultItem(null))}
 * detour - {@link WithFuelRecipe#getOutput()} already returns the exact result {@link ItemStack}, so there's
 * no need to launder it through an {@code Ingredient} first.
 */
public abstract class WithFuelCategory<T extends WithFuelRecipe> implements IRecipeCategory<RecipeHolder<T>> {
    private final IDrawable background;
    private final IDrawable icon;

    protected WithFuelCategory(IGuiHelper guiHelper, ItemLike iconItem, ResourceLocation backgroundTexture) {
        background = guiHelper.createDrawable(backgroundTexture, 40, 16, 96, 55);
        icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
    }

    @Override
    public void draw(@NotNull RecipeHolder<T> recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> recipeHolder, @NotNull IFocusGroup focuses) {
        T recipe = recipeHolder.value();
        int centerX = 96 / 2;
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.INPUT, centerX - 8, 38).addIngredients(recipe.getFuel());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 96 - 16 - 5, 5).addItemStack(recipe.getOutput());
    }

    @SuppressWarnings("removal")
    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }
}
