package com.github.fossil.compat.farmers.addon;

import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;

/**
 * Ported from source's forge {@code compat.farmers.addon.util.AddonConstants} (dropped the
 * {@code .util} subpackage per this repo's flatter convention). Drops the
 * {@code FoodMappingsManager} parameter that source threaded through every registered
 * {@code Consumer<FoodMappingsManager>} - each entry is now a plain no-arg {@link Runnable}, calling
 * {@code FoodMappings.addX(...)} statically (see this package's per-addon classes).
 *
 * <p>Only references the addons that actually have a published NeoForge 1.21.1 build per this
 * task's audit (18 of the original ~22 addon files) - the rest were dropped:
 * <ul>
 *   <li>{@code DelightfulCompat} - Delightful has no NeoForge 1.21.1 build yet (still 1.20.1-capped
 *   on both Modrinth/CurseForge as of the audit date, despite an in-progress upstream 1.21.1
 *   branch not yet shipped).</li>
 *   <li>{@code ArsNouveauAndDelightfulCompat} / {@code BYGAndDelightfuLCompat} - both are
 *   sub-compats that only fire from within {@code DelightfulCompat} and hard-depend on Delightful's
 *   own item/block classes, so they are moot once Delightful itself is dropped (independently, BYG
 *   also has no NeoForge 1.21.1 build).</li>
 *   <li>{@code NetherDelightCompat} (the original "Nether's Delight", modid {@code nethersdelight})
 *   - abandoned, Forge-only, capped at MC 1.20.1 since 2023. A differently-branded successor mod
 *   ("My Nether's Delight") does have active NeoForge 1.21.1 builds, but it's a distinct
 *   project/package - porting against it would mean guessing at unverified class names, so it was
 *   left out of this pass rather than silently guessed.</li>
 * </ul>
 *
 * <p>Modids below are taken from source where the web audit confirmed them unchanged, and corrected
 * where the audit found a difference (see {@link CoffeeDelightCompat}'s javadoc for the one
 * confirmed correction: {@code cofe_delight} -&gt; {@code coffee_delight}). Some entries reference
 * unofficial community NeoForge ports rather than the original author's build - see each class's
 * own javadoc for which ones and why.
 */
public class AddonConstants {
    private static final Map<String, Runnable> SUPPORTED_ADDONS = new HashMap<>();

    static {
        register("alexsdelight", AlexDelightCompat::registerFoodMappings);
        register("argentinasdelightreborn", ArgentinaDelightCompat::registerFoodMappings);
        register("brewinandchewin", BrewinAndChewinCompat::registerFoodMappings);
        register("butchersdelightfoods", ButcherDelightFoodCompat::registerFoodMappings);
        register("casualness_delight", CasualnessDelightCompat::registerFoodMappings);
        register("coffee_delight", CoffeeDelightCompat::registerFoodMappings);
        register("corn_delight", CornDelightCompat::registerFoodMappings);
        register("culturaldelights", CulturalDelightCompat::registerFoodMappings);
        register("crabbersdelight", CrabberDelightCompat::registerFoodMappings);
        register("ends_delight", EndDelightCompat::registerFoodMappings);
        register("endersdelight", EnderDelightCompat::registerFoodMappings);
        register("farmersrespite", FarmerRespiteCompat::registerFoodMappings);
        register("festive_delight", FestiveDelightCompat::registerFoodMappings);
        register("miners_delight", MinerDelightCompat::registerFoodMappings);
        register("oceansdelight", OceanDelightCompat::registerFoodMappings);
        register("pineapple_delight", PineappleDelightCompat::registerFoodMappings);
        register("seeddelight", SeedDelightCompat::registerFoodMappings);
        register("expandeddelight", ExpandedDelightCompat::registerFoodMappings);
    }

    public static void registerAddonFoodMappings() {
        //We cycle through each addon and check if it is loaded
        for (Map.Entry<String, Runnable> entry : AddonConstants.SUPPORTED_ADDONS.entrySet()) {
            if (!ModList.get().isLoaded(entry.getKey())) {
                continue;
            }
            entry.getValue().run();
        }
    }

    private static void register(String modId, Runnable function) {
        SUPPORTED_ADDONS.put(modId, function);
    }

    private AddonConstants() {
    }
}
