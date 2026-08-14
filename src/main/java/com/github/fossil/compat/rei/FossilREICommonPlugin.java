package com.github.fossil.compat.rei;

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.forge.REIPluginCommon;

/**
 * Ported from the source mod's {@code FossilREICommonPlugin} + the Forge-only {@code
 * FossilREICommonPluginForge} wrapper, collapsed into one class (see {@link FossilREIClientPlugin} for why).
 * <p>
 * The source imported {@code me.shedaniel.rei.api.common.plugins.REIServerPlugin} - that interface still
 * exists under that exact name/package in REI 16.0.799, so no rename was needed here. {@code
 * registerDisplaySerializer} itself moved up from being declared directly on {@code REIServerPlugin} to the
 * shared {@code REIPlugin<P>} base interface that both {@code REIServerPlugin} and {@code REIClientPlugin}
 * extend, but it's still inherited and overridable exactly the same way.
 */
@REIPluginCommon
public class FossilREICommonPlugin implements REIServerPlugin {

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(WorktableDisplay.ID, new WithFuelDisplay.Serializer<>(WorktableDisplay::new));
        registry.register(CultureVatDisplay.ID, new WithFuelDisplay.Serializer<>(CultureVatDisplay::new));
        registry.register(AnalyzerDisplay.ID, new MultiOutputDisplay.Serializer<>(AnalyzerDisplay::new));
        registry.register(SifterDisplay.ID, new MultiOutputDisplay.Serializer<>(SifterDisplay::new));
    }
}
