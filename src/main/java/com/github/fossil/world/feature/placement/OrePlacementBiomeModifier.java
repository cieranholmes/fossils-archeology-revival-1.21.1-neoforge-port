package com.github.fossil.world.feature.placement;

import com.github.fossil.Config;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * Runtime, config-gated equivalent of the source's {@code ModPlacedFeatures.register()}, which used
 * Architectury's {@code BiomeModifications.addProperties(...)} to conditionally add ore placements to
 * every vanilla biome based on mod-config toggles and (for permafrost) live biome temperature. That API
 * has no NeoForge equivalent, and the config-gating can't be expressed as static datapack JSON (a plain
 * {@code neoforge:add_features} modifier has no way to check a config flag or climate at apply-time) - so
 * this is a real custom {@link BiomeModifier} instead, applied unconditionally to every biome via a single
 * JSON entry ({@code data/fossil/neoforge/biome_modifier/ore_placement.json}), doing the actual
 * config/climate gating itself in {@link #modify}.
 */
public record OrePlacementBiomeModifier(
        Holder<PlacedFeature> oreFossilBlockUpper,
        Holder<PlacedFeature> oreFossilBlockMiddle,
        Holder<PlacedFeature> oreFossilBlockDeep,
        Holder<PlacedFeature> oreVolcanicRock,
        Holder<PlacedFeature> orePermafrostBlock,
        Holder<PlacedFeature> oreAmber,
        Holder<PlacedFeature> oreAmberBuried
) implements BiomeModifier {
    public static final MapCodec<OrePlacementBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacedFeature.CODEC.fieldOf("ore_fossil_block_upper").forGetter(OrePlacementBiomeModifier::oreFossilBlockUpper),
            PlacedFeature.CODEC.fieldOf("ore_fossil_block_middle").forGetter(OrePlacementBiomeModifier::oreFossilBlockMiddle),
            PlacedFeature.CODEC.fieldOf("ore_fossil_block_deep").forGetter(OrePlacementBiomeModifier::oreFossilBlockDeep),
            PlacedFeature.CODEC.fieldOf("ore_volcanic_rock").forGetter(OrePlacementBiomeModifier::oreVolcanicRock),
            PlacedFeature.CODEC.fieldOf("ore_permafrost_block").forGetter(OrePlacementBiomeModifier::orePermafrostBlock),
            PlacedFeature.CODEC.fieldOf("ore_amber").forGetter(OrePlacementBiomeModifier::oreAmber),
            PlacedFeature.CODEC.fieldOf("ore_amber_buried").forGetter(OrePlacementBiomeModifier::oreAmberBuried)
    ).apply(instance, OrePlacementBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) {
            return;
        }
        var generationSettings = builder.getGenerationSettings();
        if (Config.generateFossils) {
            generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreFossilBlockUpper);
            generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreFossilBlockMiddle);
            generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreFossilBlockDeep);
        }
        if (Config.generateVolcanicRock) {
            generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreVolcanicRock);
        }
        if (Config.generatePermafrost && builder.getClimateSettings().getTemperature() < 0.15) {
            generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, orePermafrostBlock);
        }
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreAmber);
        generationSettings.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreAmberBuried);
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
