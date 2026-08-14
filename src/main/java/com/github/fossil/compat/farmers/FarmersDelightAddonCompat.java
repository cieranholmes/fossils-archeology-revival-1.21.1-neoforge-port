package com.github.fossil.compat.farmers;

import com.github.fossil.compat.farmers.addon.AddonConstants;

/**
 * New top-level coordinating class for the ~22-file source package
 * {@code compat.farmers.addon}/this repo's {@link com.github.fossil.compat.farmers.addon} - source
 * had no single equivalent entry point (each addon's registration was reached only through
 * {@code AddonConstants#registerAddonFoodMappings}). This class is the one call site the main class
 * should invoke.
 *
 * <p>{@link #register()} delegates to {@link AddonConstants#registerAddonFoodMappings()}, which
 * holds the modid -&gt; registration-method map and does the actual
 * {@code net.neoforged.fml.ModList.get().isLoaded(modId)} gating internally, one check per ported
 * addon, before invoking that addon's {@code registerFoodMappings()} - functionally identical to
 * writing out 18 explicit {@code if (ModList.get().isLoaded(...))} checks by hand here, just without
 * duplicating the modid table in two places. See {@link AddonConstants}'s class javadoc for exactly
 * which of the original ~22 addons are wired in here vs. dropped, and why.
 *
 * <p>Not called anywhere yet - the main class ({@code com.github.fossil.FossilsAndArcheologyRevival})
 * needs a single new line wiring this in, on the same {@code FMLCommonSetupEvent} lifecycle hook
 * already used for {@code FossilFoodMappings.register()}:
 * {@code modEventBus.addListener((FMLCommonSetupEvent event) -> FarmersDelightAddonCompat.register());}
 * That edit is intentionally left to whoever integrates this package, per this task's scope (this
 * package must not touch anything outside {@code farmers/addon/}, and the main class is explicitly
 * out of scope here too).
 */
public class FarmersDelightAddonCompat {
    public static void register() {
        AddonConstants.registerAddonFoodMappings();
    }
}
