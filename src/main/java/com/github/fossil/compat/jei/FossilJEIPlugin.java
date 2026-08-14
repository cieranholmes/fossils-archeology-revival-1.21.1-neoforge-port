package com.github.fossil.compat.jei;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.client.gui.AnalyzerScreen;
import com.github.fossil.client.gui.CultureVatScreen;
import com.github.fossil.client.gui.SifterScreen;
import com.github.fossil.client.gui.WorktableScreen;
import com.github.fossil.inventory.AnalyzerMenu;
import com.github.fossil.inventory.CultureVatMenu;
import com.github.fossil.inventory.ModMenus;
import com.github.fossil.inventory.SifterMenu;
import com.github.fossil.inventory.WorktableMenu;
import com.github.fossil.recipe.AnalyzerRecipe;
import com.github.fossil.recipe.CultureVatRecipe;
import com.github.fossil.recipe.ModRecipes;
import com.github.fossil.recipe.SifterRecipe;
import com.github.fossil.recipe.WorktableRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * JEI integration entry point. Ported from the source mod's {@code FossilJEIPlugin}, adapted to JEI
 * 19.24.0.317's current {@link IModPlugin} API and to this project's actual recipe/menu/block classes.
 * <p>
 * API version adaptation notes (source was written against a 1.20.1-era JEI):
 * <ul>
 *     <li>{@link IModPlugin}, {@link IRecipeCategoryRegistration}, {@link IRecipeRegistration},
 *     {@link IRecipeCatalystRegistration} and {@link IGuiHandlerRegistration} are structurally unchanged
 *     from the source's usage - same method names/shapes, just re-verified against the 19.24.0.317 sources
 *     jar.</li>
 *     <li>The recipe types themselves changed shape: {@link RecipeType#create(String, String, Class)} (a
 *     bare {@code RecipeType<AnalyzerRecipe>}, as the source used) has been superseded by
 *     {@link RecipeType#createFromDeferredVanilla}, which is purpose-built for a vanilla
 *     {@code net.minecraft.world.item.crafting.RecipeType<R>} that lives behind a NeoForge
 *     {@code DeferredHolder} (exactly {@link ModRecipes}'s {@code ANALYZER_TYPE} etc.). It returns a
 *     {@code Supplier<RecipeType<RecipeHolder<R>>>} - JEI now always wraps vanilla-{@code Recipe}-backed
 *     recipes in {@link RecipeHolder} rather than handing back the bare recipe instance, so every category
 *     in this package is generic over {@code RecipeHolder<TheirRecipe>}, not the recipe type directly.</li>
 *     <li>{@code registerRecipeTransferHandlers} is new versus the source (which didn't implement recipe
 *     transfer at all) - added here using each {@code Menu}'s real slot ranges so shift-clicking a JEI
 *     recipe fills the machine GUI.</li>
 * </ul>
 */
@JeiPlugin
public class FossilJEIPlugin implements IModPlugin {
    public static final Supplier<RecipeType<RecipeHolder<AnalyzerRecipe>>> ANALYZER =
            RecipeType.createFromDeferredVanilla(ModRecipes.ANALYZER_TYPE::get);
    public static final Supplier<RecipeType<RecipeHolder<CultureVatRecipe>>> CULTURE_VAT =
            RecipeType.createFromDeferredVanilla(ModRecipes.CULTURE_VAT_TYPE::get);
    public static final Supplier<RecipeType<RecipeHolder<SifterRecipe>>> SIFTER =
            RecipeType.createFromDeferredVanilla(ModRecipes.SIFTER_TYPE::get);
    public static final Supplier<RecipeType<RecipeHolder<WorktableRecipe>>> WORKTABLE =
            RecipeType.createFromDeferredVanilla(ModRecipes.WORKTABLE_TYPE::get);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return FossilsAndArcheologyRevival.location("jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AnalyzerCategory(guiHelper));
        registration.addRecipeCategories(new CultureVatCategory(guiHelper));
        registration.addRecipeCategories(new SifterCategory(guiHelper));
        registration.addRecipeCategories(new WorktableCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            // No world joined yet (e.g. main menu) - nothing to gather. JEI re-runs plugin recipe
            // registration once the RecipeManager becomes available (joining a world/server).
            return;
        }
        RecipeManager recipeManager = level.getRecipeManager();

        List<RecipeHolder<AnalyzerRecipe>> analyzerRecipes = new ArrayList<>(recipeManager.getAllRecipesFor(ModRecipes.ANALYZER_TYPE.get()));
        Set<Item> datapackAnalyzerInputs = new HashSet<>();
        for (RecipeHolder<AnalyzerRecipe> holder : analyzerRecipes) {
            for (ItemStack stack : holder.value().getInput().getItems()) {
                datapackAnalyzerInputs.add(stack.getItem());
            }
        }
        // ModRecipes.ANALYZER_RECIPES holds the compile-time-known bone/DNA/food recipes registered by
        // ModRecipes#initRecipes() - these never go through datapack loading/RecipeManager, so they need
        // their own synthetic RecipeHolder ids to be shown here. Skip any item already covered by a
        // datapack recipe above (getAnalyzerRecipeForItem() also lazily caches datapack lookups into this
        // same map at runtime, so an entry here isn't necessarily compile-time-only).
        for (Map.Entry<Item, AnalyzerRecipe> entry : ModRecipes.ANALYZER_RECIPES.entrySet()) {
            if (datapackAnalyzerInputs.contains(entry.getKey())) {
                continue;
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(entry.getKey());
            analyzerRecipes.add(new RecipeHolder<>(FossilsAndArcheologyRevival.location("analyzer_compiled/" + itemId.getPath()), entry.getValue()));
        }
        registration.addRecipes(ANALYZER.get(), analyzerRecipes);

        registration.addRecipes(CULTURE_VAT.get(), recipeManager.getAllRecipesFor(ModRecipes.CULTURE_VAT_TYPE.get()));
        registration.addRecipes(SIFTER.get(), recipeManager.getAllRecipesFor(ModRecipes.SIFTER_TYPE.get()));
        registration.addRecipes(WORKTABLE.get(), recipeManager.getAllRecipesFor(ModRecipes.WORKTABLE_TYPE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANALYZER.get()), ANALYZER.get());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CULTURE_VAT.get()), CULTURE_VAT.get());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SIFTER.get()), SIFTER.get());
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WORKTABLE.get()), WORKTABLE.get());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // AnalyzerMenu: 9 input slots (0-8), then 1 primary + 3 secondary output slots (9-12), then player
        // inventory (13-48).
        registration.addRecipeTransferHandler(AnalyzerMenu.class, ModMenus.ANALYZER.get(), ANALYZER.get(), 0, 9, 13, 36);
        // SifterMenu: 1 input slot (0), then 5 output slots (1-5), then player inventory (6-41).
        registration.addRecipeTransferHandler(SifterMenu.class, ModMenus.SIFTER.get(), SIFTER.get(), 0, 1, 6, 36);
        // CultureVatMenu/WorktableMenu: input (0), fuel (1), output (2), then player inventory (3-38).
        registration.addRecipeTransferHandler(CultureVatMenu.class, ModMenus.CULTURE_VAT.get(), CULTURE_VAT.get(), 0, 2, 3, 36);
        registration.addRecipeTransferHandler(WorktableMenu.class, ModMenus.WORKTABLE.get(), WORKTABLE.get(), 0, 2, 3, 36);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AnalyzerScreen.class, 80, 21, 23, 11, ANALYZER.get());
        registration.addRecipeClickArea(CultureVatScreen.class, 78, 22, 21, 9, CULTURE_VAT.get());
        registration.addRecipeClickArea(WorktableScreen.class, 75, 19, 26, 16, WORKTABLE.get());
        registration.addRecipeClickArea(SifterScreen.class, 75, 33, 26, 26, SIFTER.get());
        // Source also reserved extra Rect2i space beside CreativeModeInventoryScreen when
        // ModTabs.FA_MOB_ITEM_TAB/FA_BLOCK_TAB were selected, to keep JEI's ingredient list from
        // overlapping that source mod's (not yet ported) CreativeTabFilters sidebar buttons. Left out here:
        // CreativeModeInventoryScreen#selectedTab is private in 1.21.1 (public in the 1.20.1 source), and
        // with no CreativeTabFilters-equivalent yet in this repo there is nothing on screen to protect from
        // overlap. Revisit this if/when that filter-sidebar feature gets ported.
    }
}
