package com.github.fossil.recipe;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.inventory.WorktableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class WorktableRecipe extends WithFuelRecipe {
    protected WorktableRecipe(Ingredient input, Ingredient fuel, ItemStack output, int duration, int fuelDuration) {
        super(input, fuel, output, duration, fuelDuration);
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.WORKTABLE.get());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<WorktableRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }
    }

    public static class Serializer extends WithFuelRecipe.WithFuelRecipeSerializer<WorktableRecipe> {
        public static final Serializer INSTANCE = new Serializer(WorktableRecipe::new);

        public Serializer(Constructor<WorktableRecipe> constructor) {
            super(constructor);
        }

        @Override
        int defaultDuration() {
            return WorktableMenu.DEFAULT_DURATION;
        }

        @Override
        int defaultFuelDuration() {
            return WorktableMenu.DEFAULT_FUEL_DURATION;
        }
    }
}
