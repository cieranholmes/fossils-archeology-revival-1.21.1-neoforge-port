package com.github.fossil.compat.rei;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.client.gui.AnalyzerScreen;
import com.github.fossil.client.gui.CultureVatScreen;
import com.github.fossil.client.gui.SifterScreen;
import com.github.fossil.client.gui.WorktableScreen;
import com.github.fossil.item.ModTabs;
import com.github.fossil.recipe.*;
import com.github.fossil.tags.ModItemTags;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Ported from the source mod's {@code FossilREIClientPlugin} + the Forge-only {@code
 * FossilREIClientPluginForge} wrapper - this project is NeoForge-only and single-source-set, so the two
 * collapse into one class. REI 16.x's NeoForge plugin loader (see {@code
 * me.shedaniel.rei.forge.PluginDetectorImpl}) discovers plugins by scanning mod-file class annotations
 * ({@code @REIPluginClient}/{@code @REIPluginCommon}) directly, the same way {@code @Mod} classes are
 * found - there is no {@code META-INF/services} ServiceLoader step to wire up on NeoForge (that's a
 * Fabric-only mechanism for REI), so this class is registered simply by carrying the annotation below.
 */
@REIPluginClient
public class FossilREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WorktableCategory());
        registry.addWorkstations(WorktableDisplay.ID, EntryStacks.of(ModBlocks.WORKTABLE.get()));
        registry.add(new CultureVatCategory());
        registry.addWorkstations(CultureVatDisplay.ID, EntryStacks.of(ModBlocks.CULTURE_VAT.get()));
        registry.add(new AnalyzerCategory());
        registry.addWorkstations(AnalyzerDisplay.ID, EntryStacks.of(ModBlocks.ANALYZER.get()));
        registry.add(new SifterCategory());
        registry.addWorkstations(SifterDisplay.ID, EntryStacks.of(ModBlocks.SIFTER.get()));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(80, 21, 23, 11), AnalyzerScreen.class, AnalyzerDisplay.ID);
        registry.registerContainerClickArea(new Rectangle(78, 22, 21, 9), CultureVatScreen.class, CultureVatDisplay.ID);
        registry.registerContainerClickArea(new Rectangle(75, 19, 26, 16), WorktableScreen.class, WorktableDisplay.ID);
        registry.registerContainerClickArea(new Rectangle(75, 33, 26, 26), SifterScreen.class, SifterDisplay.ID);
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(CreativeModeInventoryScreen.class, screen -> {
            CreativeModeTab selected = getSelectedTab();
            if (selected == ModTabs.FA_MOB_ITEM_TAB.get() || selected == ModTabs.FA_BLOCK_TAB.get()) {
                int leftPos = (screen.width - 195) / 2;
                int topPos = (screen.height - 125) / 2;
                return List.of(new Rectangle(leftPos - 26, topPos, 26, 118), new Rectangle(leftPos + 195, topPos, 28, 118));
            }
            return new ArrayList<>();
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(WorktableRecipe.class, ModRecipes.WORKTABLE_TYPE.get(), WorktableDisplay::new);
        registry.registerRecipeFiller(CultureVatRecipe.class, ModRecipes.CULTURE_VAT_TYPE.get(), CultureVatDisplay::new);
        registry.registerRecipeFiller(AnalyzerRecipe.class, ModRecipes.ANALYZER_TYPE.get(), AnalyzerDisplay::new);
        registry.registerRecipeFiller(SifterRecipe.class, ModRecipes.SIFTER_TYPE.get(), SifterDisplay::new);
        registry.registerFiller(AnalyzerTagRecipe.class, AnalyzerDisplay::new);

        registry.add(new AnalyzerTagRecipe.Builder(ModItemTags.ALL_BONES)
                .addOutput(Items.BONE_MEAL, 30)
                .addOutput(Items.BONE, 35)
                .addOutput(ModItemTags.BONES_DNA, 35).build());
        registry.add(new AnalyzerTagRecipe.Builder(ModItemTags.UNCOOKED_MEAT)
                .addOutput(ModItemTags.MEAT_DNA, 100).build());
        registry.add(new AnalyzerTagRecipe.Builder(ModItemTags.DINO_EGGS)
                .addOutput(ModItemTags.DINO_DNA, 100).build());
        registry.add(new AnalyzerTagRecipe.Builder(ModItemTags.FISH_EGGS)
                .addOutput(ModItemTags.FISH_DNA, 100).build());
        registry.add(new AnalyzerTagRecipe.Builder(ModItemTags.EMBRYOS)
                .addOutput(ModItemTags.EMBRYO_DNA, 100).build());
    }

    /**
     * {@code CreativeModeInventoryScreen#selectedTab} is {@code private static} in 1.21.1 (it was {@code
     * public static} in the 1.20.1 source), with no public accessor added in its place. The exclusion zones
     * this guards (drawn by the source mod's {@code CreativeTabFilters} sidebar widgets, which live outside
     * this REI port's scope and haven't been ported to this repo yet) are cosmetic-only, so reflection with
     * a silent fallback is used rather than pulling in a mixin for one field read.
     */
    private static final MethodHandle SELECTED_TAB_GETTER = resolveSelectedTabGetter();

    private static MethodHandle resolveSelectedTabGetter() {
        try {
            Field field = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
            field.setAccessible(true);
            return MethodHandles.lookup().unreflectGetter(field);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static CreativeModeTab getSelectedTab() {
        if (SELECTED_TAB_GETTER == null) {
            return null;
        }
        try {
            return (CreativeModeTab) SELECTED_TAB_GETTER.invoke();
        } catch (Throwable t) {
            return null;
        }
    }
}
