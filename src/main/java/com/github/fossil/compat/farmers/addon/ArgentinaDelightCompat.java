package com.github.fossil.compat.farmers.addon;

import com.github.fossil.food.FoodMappings;
import net.mcreator.argentinasdelightreborn.init.ArgentinasdelightrebornModItems;

/**
 * Ported from source's forge {@code ArgentinaDelightCompat} (Argentina's Delight addon, source
 * modid {@code argentinas_delight}, 67 items mapped). Drops the {@code FoodMappingsManager}
 * parameter - calls the static {@link FoodMappings} methods directly instead.
 *
 * <p><b>This addon underwent a full rebrand/rewrite, not just a rename</b> - the only NeoForge
 * 1.21.1 build found is "Argentina's Delight Reborn" (CurseForge {@code argentinasdelightreborn},
 * modid {@code argentinasdelightreborn}, confirmed via the resolved jar's neoforge.mods.toml).
 * Verified via javap that this is a <b>different MCreator project</b> from source's, not a
 * recompiled continuation: the package changed from {@code net.mcreator.cambiandoversion} to
 * {@code net.mcreator.argentinasdelightreborn}, the class is
 * {@code ArgentinasdelightrebornModItems} (not {@code ArgentinasDelightModItems}), and - critically
 * - its ~44-field item roster shares almost no field names with source's 67-item list (source's
 * Spanish dish names like {@code NALGA}/{@code MILANESA}/{@code COSTILLAR} are gone; the new roster
 * uses different English names like {@code VACIO}, {@code BEEF_RIBS}, {@code CHORIPAN}).
 *
 * <p>Given the scale of the rewrite, this maps only the subset of the new roster confidently
 * identifiable as finished, edible dishes (by name) rather than guessing at a 1:1 correspondence
 * with source's old Spanish-named list - intermediate cooking-state items ({@code *_ONBOARD}/
 * {@code *_ONGRILL} placed blocks), tools ({@code FACON} knife, {@code BULLHORN}), guide books, and
 * the automation/sawing-machine blocks are deliberately excluded as non-food. <b>This is a
 * conservative best-effort mapping, not a verified 1:1 port</b> - if precise fidelity to the new
 * mod's actual food list matters, it should be re-audited directly against the addon's own recipe/
 * food-value JSON rather than inferred from field names alone.
 */
public class ArgentinaDelightCompat {
    public static void registerFoodMappings() {
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.BEEF_RIBS.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.VACIO.get()); //Flank steak
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.ENTRECOTE.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.MARDELPLATASTEAK.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.SKEWERITEM.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.HALFPORK.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.PORKLOINSLICE.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.PORKLOINITEM.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.PORKRIBSITEM.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.PORKLEGSLICE.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.CHITTERLINGS.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.CHORIZO.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.CHORIPAN.get());
        FoodMappings.addMeat(ArgentinasdelightrebornModItems.CHORIZOS.get());
    }
}
