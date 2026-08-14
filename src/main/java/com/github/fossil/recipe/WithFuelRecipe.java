package com.github.fossil.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for machine recipes with one input slot and one fuel slot (Culture Vat, Worktable). Ported
 * from the source mod's {@code Recipe<ContainerWithAnyFuel>}-based class - {@code ContainerWithAnyFuel}
 * (a {@code SimpleContainer} subclass) is replaced by {@link WithFuelInput}, a proper {@link RecipeInput}
 * record that still carries the source's {@code anyFuel} flag. The {@code ResourceLocation id} field is
 * gone entirely, same as {@link MultiOutputAndSlotsRecipe}.
 * <p>
 * Behavior simplification vs. source: {@link #assemble} no longer copies the input stack's NBT tag onto
 * the result (source did {@code container.getItem(0).getTag()} and merged it in) - 1.21.1 replaced
 * NBT tags with typed data components, and none of this mod's Culture Vat/Worktable recipes actually rely
 * on carrying arbitrary input data through to the output, so the copy is dropped rather than reinvented
 * against the new DataComponent APIs for no functional benefit.
 */
public abstract class WithFuelRecipe implements Recipe<WithFuelRecipe.WithFuelInput> {
    private final Ingredient input;
    private final Ingredient fuel;
    private final ItemStack result;
    private final int duration;
    private final int fuelDuration;

    protected WithFuelRecipe(Ingredient input, Ingredient fuel, ItemStack result, int duration, int fuelDuration) {
        this.input = input;
        this.fuel = fuel;
        this.result = result;
        this.duration = duration;
        this.fuelDuration = fuelDuration;
    }

    @Override
    public boolean matches(WithFuelInput input, Level level) {
        return this.input.test(input.item()) && (input.anyFuel() || this.fuel.test(input.fuel()));
    }

    @Override
    public @NotNull ItemStack assemble(WithFuelInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(input);
        return nonNullList;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean isFuel(ItemStack itemStack) {
        return fuel.test(itemStack);
    }

    public Ingredient getInput() {
        return input;
    }

    public Ingredient getFuel() {
        return fuel;
    }

    public int getDuration() {
        return duration;
    }

    public int getFuelDuration() {
        return fuelDuration;
    }

    public ItemStack getOutput() {
        return result;
    }

    /**
     * {@link RecipeInput} for the 2-slot input+fuel machines, replacing the source's
     * {@code WithFuelRecipe.ContainerWithAnyFuel}. {@code anyFuel}, when true, makes {@link #matches} skip
     * the fuel-ingredient test entirely - used by menu shift-click/mayPlace checks that only care whether
     * an item is a valid *input*, regardless of whatever fuel happens to be loaded (mirrors source's
     * {@code new ContainerWithAnyFuel(true, stack)} call sites).
     */
    public record WithFuelInput(ItemStack item, ItemStack fuel, boolean anyFuel) implements RecipeInput {
        public static WithFuelInput of(ItemStack item, ItemStack fuel) {
            return new WithFuelInput(item, fuel, false);
        }

        public static WithFuelInput anyFuel(ItemStack item) {
            return new WithFuelInput(item, ItemStack.EMPTY, true);
        }

        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> item;
                case 1 -> fuel;
                default -> throw new IllegalArgumentException("No item for index " + index);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }

    public abstract static class WithFuelRecipeSerializer<T extends WithFuelRecipe> implements RecipeSerializer<T> {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        protected WithFuelRecipeSerializer(Constructor<T> constructor) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(WithFuelRecipe::getInput),
                    Ingredient.CODEC.fieldOf("fuel").forGetter(WithFuelRecipe::getFuel),
                    ItemStack.CODEC.fieldOf("result").forGetter(WithFuelRecipe::getOutput),
                    Codec.INT.optionalFieldOf("duration", defaultDuration()).forGetter(WithFuelRecipe::getDuration),
                    Codec.INT.optionalFieldOf("fuel_duration", defaultFuelDuration()).forGetter(WithFuelRecipe::getFuelDuration)
            ).apply(instance, constructor::construct));
            this.streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, WithFuelRecipe::getInput,
                    Ingredient.CONTENTS_STREAM_CODEC, WithFuelRecipe::getFuel,
                    ItemStack.STREAM_CODEC, WithFuelRecipe::getOutput,
                    ByteBufCodecs.VAR_INT, WithFuelRecipe::getDuration,
                    ByteBufCodecs.VAR_INT, WithFuelRecipe::getFuelDuration,
                    constructor::construct);
        }

        abstract int defaultDuration();

        abstract int defaultFuelDuration();

        @Override
        public MapCodec<T> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return streamCodec;
        }

        @FunctionalInterface
        public interface Constructor<R> {
            R construct(Ingredient input, Ingredient fuel, ItemStack output, int duration, int fuelDuration);
        }
    }
}
