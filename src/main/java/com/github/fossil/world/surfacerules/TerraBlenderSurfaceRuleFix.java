package com.github.fossil.world.surfacerules;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.util.LevelUtils;
import terrablender.worldgen.IExtendedNoiseGeneratorSettings;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Restores TerraBlender's per-namespace surface rules when its dimension tag goes missing - which is
 * what leaves the volcano biome with plain grass ground in large modpacks.
 * <p>
 * TerraBlender only swaps its namespaced surface-rule dispatch into a dimension's
 * {@link NoiseGeneratorSettings} when that settings object carries a
 * {@link SurfaceRuleManager.RuleCategory} tag (see {@code MixinNoiseGeneratorSettings#surfaceRule},
 * which returns the untouched vanilla rule while the tag is null). {@code LevelUtils.initializeBiomes}
 * applies that tag at ServerAboutToStartEvent, and it lives on the settings <em>instance</em>.
 * <p>
 * The instance is the problem. TerraBlender genuinely succeeds - it logs "Initialized TerraBlender
 * biomes for level stem minecraft:overworld" - but the dynamic registries are rebuilt afterwards and
 * the object the dimension actually generates from is a different one whose tag is back to null.
 * Verified directly: the settings identity differs between server start and level load, and
 * re-tagging at either of those points was still observed to be discarded later in a large enough
 * pack.
 * <p>
 * So this deliberately does not try to pick the "right" lifecycle hook. It re-applies the tag
 * just-in-time at the head of surface building, against whichever settings object the generator is
 * holding right then, which is immune to the object being replaced any number of times. The check is
 * a single {@code instanceof} on an already-cached value, so the steady-state cost per chunk is
 * nil.
 * <p>
 * The symptom this fixes: biome placement works perfectly - {@code fossil:volcano} generates and
 * reports correctly in F3 - while the surface rules that give it volcanic rock never run, so every
 * column falls through to vanilla's base rule, which places plain grass_block for a biome vanilla
 * doesn't recognise. It is not specific to this mod either: every mod that registered surface rules
 * through TerraBlender loses them together (observed in the affected pack: mysticsbiomes, spawn,
 * nomansland, fossil, natures_spirit, ars_elemental), and re-applying the tag restores all of them.
 */
public class TerraBlenderSurfaceRuleFix {
    private static final Set<ResourceLocation> REPAIRED = ConcurrentHashMap.newKeySet();
    private static final Set<ResourceLocation> UNREPAIRABLE = ConcurrentHashMap.newKeySet();

    public static void ensureDispatch(NoiseBasedChunkGenerator generator, ServerLevel level) {
        NoiseGeneratorSettings settings = generator.generatorSettings().value();
        // Fast path, and the only one that runs once things are healthy: while the tag is null
        // surfaceRule() hands back the plain vanilla rule, and once it is set TerraBlender caches its
        // wrapper, so this is just a field read plus an instanceof.
        if (settings.surfaceRule() instanceof NamespacedSurfaceRuleSource) {
            return;
        }

        ResourceLocation dimension = level.dimension().location();
        RegionType regionType = LevelUtils.getRegionTypeForDimension(level.dimensionTypeRegistration());
        SurfaceRuleManager.RuleCategory category = categoryFor(regionType);
        // Only step in for dimensions TerraBlender is meant to manage - the same dimension-type tag
        // it checks itself - and only when regions were actually registered for them.
        if (category == null || Regions.get(regionType).isEmpty()) {
            return;
        }

        // Surface building runs on worker threads, so keep the repair itself single-threaded rather
        // than racing several threads through TerraBlender's lazy wrapper construction.
        synchronized (TerraBlenderSurfaceRuleFix.class) {
            if (settings.surfaceRule() instanceof NamespacedSurfaceRuleSource) {
                return;
            }
            // NoiseGeneratorSettings is a final record; it only implements this interface at runtime,
            // courtesy of TerraBlender's mixin, so the cast has to launder through Object.
            ((IExtendedNoiseGeneratorSettings) (Object) settings).setRuleCategory(category);

            if (settings.surfaceRule() instanceof NamespacedSurfaceRuleSource) {
                if (REPAIRED.add(dimension)) {
                    FossilsAndArcheologyRevival.LOGGER.info(
                            "{} was generating without a TerraBlender rule category, which silently disables every mod's {} "
                                    + "surface rules. Re-applied the missing tag, so they now take effect.",
                            dimension, category);
                }
            } else if (UNREPAIRABLE.add(dimension)) {
                // Worth shouting about: it means something other than the missing tag is suppressing
                // the dispatch, and the volcano biome will still generate with vanilla ground.
                FossilsAndArcheologyRevival.LOGGER.warn(
                        "{} still has no TerraBlender surface-rule dispatch after re-applying the {} tag (surface rule is {}). "
                                + "Modded surface rules, including this mod's volcanic ground, will not apply here.",
                        dimension, category, settings.surfaceRule().getClass().getName());
            }
        }
    }

    private static SurfaceRuleManager.RuleCategory categoryFor(RegionType regionType) {
        if (regionType == null) {
            return null;
        }
        return switch (regionType) {
            case OVERWORLD -> SurfaceRuleManager.RuleCategory.OVERWORLD;
            case NETHER -> SurfaceRuleManager.RuleCategory.NETHER;
        };
    }
}
