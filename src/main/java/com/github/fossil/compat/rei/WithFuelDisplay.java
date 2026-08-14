package com.github.fossil.compat.rei;

import com.github.fossil.recipe.WithFuelRecipe;
import com.google.common.collect.Lists;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Base display for the two input+fuel machines (Culture Vat, Worktable). No single "WithFuelCategory" base
 * exists on the category side (each of {@link CultureVatCategory}/{@link WorktableCategory} sets up its own
 * widgets independently) - this only unifies the display data model, matching the source mod's actual class
 * hierarchy.
 * <p>
 * Recipe-based constructor takes a {@link RecipeHolder} rather than a bare {@link WithFuelRecipe} - see
 * {@link MultiOutputDisplay} for why (1.21.1 moved the recipe id out of {@code Recipe} and into
 * {@code RecipeHolder}, which is also what REI 16.x's {@code registerRecipeFiller} now hands the filler).
 */
public abstract class WithFuelDisplay implements Display {
    protected final EntryIngredient input;
    protected final EntryIngredient fuel;
    protected final EntryStack<?> output;
    protected final ResourceLocation location;


    protected WithFuelDisplay(RecipeHolder<? extends WithFuelRecipe> recipeHolder) {
        this(EntryIngredients.ofIngredient(recipeHolder.value().getInput()), EntryIngredients.ofIngredient(recipeHolder.value().getFuel()),
                EntryStacks.of(recipeHolder.value().getOutput()), recipeHolder.id());
    }

    protected WithFuelDisplay(EntryIngredient input, EntryIngredient fuel, EntryStack<?> output, ResourceLocation location) {
        this.input = input;
        this.fuel = fuel;
        this.output = output;
        this.location = location;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Lists.newArrayList(input, fuel);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredient.of(output));
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(location);
    }

    public static class Serializer<T extends WithFuelDisplay> implements DisplaySerializer<T> {
        protected final Constructor<T> constructor;

        protected Serializer(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public CompoundTag save(CompoundTag tag, T display) {
            tag.put("Input", display.input.saveIngredient());
            tag.put("Fuel", display.fuel.saveIngredient());
            tag.put("Output", display.output.saveStack());
            tag.putString("Location", display.location.toString());
            return tag;
        }

        @Override
        public T read(CompoundTag tag) {
            EntryIngredient input = EntryIngredient.read(tag.getList("Input", Tag.TAG_COMPOUND));
            EntryIngredient fuel = EntryIngredient.read(tag.getList("Fuel", Tag.TAG_COMPOUND));
            EntryStack<?> output = EntryStack.read(tag.getCompound("Output"));
            ResourceLocation location = ResourceLocation.parse(tag.getString("Location"));
            return constructor.construct(input, fuel, output, location);
        }

        @FunctionalInterface
        public interface Constructor<R> {
            R construct(EntryIngredient input, EntryIngredient fuel, EntryStack<?> output, ResourceLocation location);
        }
    }
}
