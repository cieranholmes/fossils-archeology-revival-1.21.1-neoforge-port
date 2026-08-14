package com.github.fossil.compat.emi;

import com.github.fossil.recipe.WithFuelRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Shared base for machine recipes with one input slot and one fuel slot, producing a single fixed
 * output (Culture Vat, Worktable). Ported from source's EMI wrapper of the same name.
 * <p>
 * Takes a {@link RecipeHolder} rather than a bare recipe since target's {@link WithFuelRecipe} carries no
 * id of its own any more (see {@link MultiOutputEmiRecipe}'s class doc for the same adaptation and why).
 * Uses {@link WithFuelRecipe#getOutput()} in place of source's {@code getResultItem(null)} - target's
 * accessor needs no registry-lookup parameter at all.
 */
public abstract class WithFuelEmiRecipe<T extends WithFuelRecipe> implements EmiRecipe {
    private final ResourceLocation id;
    protected final EmiIngredient input;
    protected final EmiIngredient fuel;
    protected final EmiStack output;
    protected final T recipe;

    protected WithFuelEmiRecipe(RecipeHolder<T> holder) {
        this.recipe = holder.value();
        this.id = holder.id();
        this.input = EmiIngredient.of(recipe.getInput());
        this.fuel = EmiIngredient.of(recipe.getFuel());
        this.output = EmiStack.of(recipe.getOutput());
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 96;
    }

    @Override
    public int getDisplayHeight() {
        return 55;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(input, 0, 0).large(true);
        widgets.addSlot(fuel, 39, 37);
        widgets.addSlot(output, 70, 0).large(true).recipeContext(this);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
