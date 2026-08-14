package com.github.fossil.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Base class for machine recipes that take a single item input and produce one of several weighted
 * outputs (Analyzer, Sifter). Ported from the source mod's {@code Recipe<Container>}-based class - the
 * generic bound is now {@link SingleRecipeInput} (1.21.1's {@code RecipeInput} replaces {@code Container}),
 * and the {@code ResourceLocation id} field is gone entirely since {@link net.minecraft.world.item.crafting.RecipeHolder}
 * now owns the id/recipe pairing.
 * <p>
 * Behavior simplification vs. source: {@link #matches} no longer scans every slot of an arbitrary-size
 * container looking for a match (the source's block entities passed their whole multi-slot inventory in);
 * it now only tests the single item carried by the {@link SingleRecipeInput}. Multi-slot scanning (e.g. an
 * Analyzer block entity checking all 9 of its input slots) is expected to loop externally, calling
 * {@link ModRecipes#getAnalyzerRecipeForItem(ItemStack, Level)} (or the Sifter equivalent) once per slot.
 */
public abstract class MultiOutputAndSlotsRecipe implements Recipe<SingleRecipeInput> {
    private static final RandomSource RANDOM = RandomSource.create();

    protected final Ingredient input;
    private final NavigableMap<Double, ItemStack> weightedOutputs;

    protected MultiOutputAndSlotsRecipe(Ingredient input, NavigableMap<Double, ItemStack> weightedOutputs) {
        this.input = input;
        this.weightedOutputs = weightedOutputs;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(input);
        return nonNullList;
    }

    public Ingredient getInput() {
        return input;
    }

    public NavigableMap<Double, ItemStack> getWeightedOutputs() {
        return weightedOutputs;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        ItemStack stack = input.item();
        return !stack.isEmpty() && this.input.test(stack);
    }

    @Override
    public @NotNull ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        // Source picked a weighted output using the block entity's own Level#random (the container passed
        // to assemble() *was* the block entity). RecipeInput carries no level/random access at all in
        // 1.21.1, so this uses a shared static RandomSource instead - functionally equivalent, since the
        // source's random pick was never seeded/synced to the world seed either.
        return weightedOutputs.higherEntry(RANDOM.nextDouble() * weightedOutputs.lastKey()).getValue().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * Per-output entry as authored in JSON: {@code {"weight": <double>, "item": {...ItemStack...}}}.
     * {@code item} uses {@link ItemStack#OPTIONAL_CODEC}/{@link ItemStack#OPTIONAL_STREAM_CODEC} (not the
     * plain non-air codec) specifically so an empty {@code {}} object can represent a "nothing" outcome -
     * mirroring the source JSON format's {@code total > sum(weights)} trick for weighting in a chance of no
     * output at all, without needing a special-cased sentinel item.
     */
    public record WeightedOutput(double weight, ItemStack item) {
        public static final Codec<WeightedOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("weight").forGetter(WeightedOutput::weight),
                ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(WeightedOutput::item)
        ).apply(instance, WeightedOutput::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WeightedOutput> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, WeightedOutput::weight,
                ItemStack.OPTIONAL_STREAM_CODEC, WeightedOutput::item,
                WeightedOutput::new);
    }

    /**
     * Codec for the cumulative-weight {@code NavigableMap<Double, ItemStack>} runtime shape used by
     * {@link #assemble}. JSON representation is a list of per-output weight deltas (human-authorable, each
     * output just states its own {@code weight}); converted to/from the cumulative-weight map at decode/
     * encode time. No vanilla codec covers this shape, so it's hand-rolled.
     */
    protected static final Codec<NavigableMap<Double, ItemStack>> WEIGHTED_OUTPUTS_CODEC = WeightedOutput.CODEC.listOf().xmap(
            list -> {
                NavigableMap<Double, ItemStack> map = new TreeMap<>();
                double total = 0;
                for (WeightedOutput output : list) {
                    total += output.weight();
                    map.put(total, output.item());
                }
                return map;
            },
            map -> {
                List<WeightedOutput> list = new ArrayList<>();
                double previous = 0;
                for (Map.Entry<Double, ItemStack> entry : map.entrySet()) {
                    list.add(new WeightedOutput(entry.getKey() - previous, entry.getValue()));
                    previous = entry.getKey();
                }
                return list;
            });

    /** Network representation just round-trips the cumulative weights directly - no need for human-readable deltas. */
    protected static final StreamCodec<RegistryFriendlyByteBuf, NavigableMap<Double, ItemStack>> WEIGHTED_OUTPUTS_STREAM_CODEC =
            WeightedOutput.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
                    list -> {
                        NavigableMap<Double, ItemStack> map = new TreeMap<>();
                        for (WeightedOutput output : list) {
                            map.put(output.weight(), output.item());
                        }
                        return map;
                    },
                    map -> {
                        List<WeightedOutput> list = new ArrayList<>();
                        for (Map.Entry<Double, ItemStack> entry : map.entrySet()) {
                            list.add(new WeightedOutput(entry.getKey(), entry.getValue()));
                        }
                        return list;
                    });

    public abstract static class Serializer<T extends MultiOutputAndSlotsRecipe> implements RecipeSerializer<T> {
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        protected Serializer(Constructor<T> constructor) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(MultiOutputAndSlotsRecipe::getInput),
                    WEIGHTED_OUTPUTS_CODEC.fieldOf("outputs").forGetter(MultiOutputAndSlotsRecipe::getWeightedOutputs)
            ).apply(instance, constructor::construct));
            this.streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, MultiOutputAndSlotsRecipe::getInput,
                    WEIGHTED_OUTPUTS_STREAM_CODEC, MultiOutputAndSlotsRecipe::getWeightedOutputs,
                    constructor::construct);
        }

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
            R construct(Ingredient input, NavigableMap<Double, ItemStack> outputs);
        }
    }
}
