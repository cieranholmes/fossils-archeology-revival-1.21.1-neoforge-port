package com.github.fossil.world.surfacerules;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.world.level.levelgen.SurfaceRules;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Puts this mod's volcanic surface rule into the overworld's rule chain directly, without relying on
 * TerraBlender's per-namespace dispatch.
 * <p>
 * TerraBlender's dispatch is only installed when a dimension's {@code NoiseGeneratorSettings} carries
 * a {@code ruleCategory} tag, and in a large enough modpack it never survives to generation time: the
 * settings instance the world generates from is not the one TerraBlender tagged, and even re-applying
 * the tag just before use does not help, because eight different mods patch
 * {@code NoiseGeneratorSettings} and something else wins the {@code surfaceRule()} call. Both of those
 * were confirmed in the affected pack. Rather than keep fighting for ownership of that one method,
 * this simply prepends the rule where it is consumed.
 * <p>
 * Prepending is safe in any dimension, because the rule is wholly gated behind
 * {@code isBiome(VOLCANO_KEY)} - it returns null and yields to the rest of the chain for every column
 * that is not the volcano biome, so a dimension without that biome is completely unaffected. This
 * also deliberately does nothing when TerraBlender's dispatch IS working and already carries the
 * "fossil" namespace, so the two never both apply.
 */
public class VolcanoSurfaceRuleInjector {
    // Keyed on the incoming rule source, which the generator reuses for every chunk, so the wrapped
    // chain is built once rather than per chunk. Identity comparison because rule sources are deep
    // records whose equals() would walk the whole tree.
    private static final Map<SurfaceRules.RuleSource, SurfaceRules.RuleSource> WRAPPED =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private static final AtomicBoolean LOGGED = new AtomicBoolean();

    public static SurfaceRules.RuleSource ensureVolcanoRule(SurfaceRules.RuleSource original) {
        if (original == null) {
            return null;
        }
        // TerraBlender is healthy here and already routes the "fossil" namespace to our rule.
        if (original instanceof NamespacedSurfaceRuleSource namespaced
                && namespaced.sources().containsKey(FossilsAndArcheologyRevival.MODID)) {
            return original;
        }
        return WRAPPED.computeIfAbsent(original, rule -> {
            if (LOGGED.compareAndSet(false, true)) {
                FossilsAndArcheologyRevival.LOGGER.info(
                        "Applying the volcano surface rule directly, since TerraBlender's namespaced dispatch is not present "
                                + "on this dimension's surface rule ({}).", rule.getClass().getName());
            }
            return SurfaceRules.sequence(ModSurfaceRules.VOLCANIC_SURFACE_RULE, rule);
        });
    }
}
