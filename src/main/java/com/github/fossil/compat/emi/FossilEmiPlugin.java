package com.github.fossil.compat.emi;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.recipe.*;
import com.github.fossil.tags.ModItemTags;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * Ported from source's {@code compat/emi/FossilEmiPlugin.java}.
 * <p>
 * <b>Plugin discovery (API drift from source's javadoc):</b> {@link EmiPlugin}'s own javadoc describes
 * Fabric/Quilt discovery via a mod-json "emi" entrypoint and Forge discovery via {@link EmiEntrypoint} -
 * it says nothing about NeoForge. Decompiling emi-neoforge 1.1.22 itself
 * ({@code dev.emi.emi.platform.neoforge.EmiAgnosNeoForge#getPluginsAgnos}) shows NeoForge still uses the
 * exact same {@link EmiEntrypoint}-annotation mechanism, scanned via NeoForge's {@code ModFileScanData}/
 * {@code ModList} - there is no {@code META-INF/services} ServiceLoader path anywhere in EMI's own loading
 * code for any loader. The {@code @EmiEntrypoint} annotation below is therefore what actually makes this
 * plugin get loaded; see the final report for the (harmless, but functionally inert) services file also
 * requested for this task.
 */
@EmiEntrypoint
public class FossilEmiPlugin implements EmiPlugin {
    public static final EmiStack WORKTABLE = EmiStack.of(ModBlocks.WORKTABLE.get());
    public static final EmiStack CULTURE_VAT = EmiStack.of(ModBlocks.CULTURE_VAT.get());
    public static final EmiStack ANALYZER = EmiStack.of(ModBlocks.ANALYZER.get());
    public static final EmiStack SIFTER = EmiStack.of(ModBlocks.SIFTER.get());
    public static final EmiRecipeCategory WORKTABLE_CATEGORY = new EmiRecipeCategory(FossilsAndArcheologyRevival.location("worktable"), WORKTABLE);
    public static final EmiRecipeCategory CULTURE_VAT_CATEGORY = new EmiRecipeCategory(FossilsAndArcheologyRevival.location("culture_vat"), CULTURE_VAT);
    public static final EmiRecipeCategory ANALYZER_CATEGORY = new EmiRecipeCategory(FossilsAndArcheologyRevival.location("analyzer"), ANALYZER);
    public static final EmiRecipeCategory SIFTER_CATEGORY = new EmiRecipeCategory(FossilsAndArcheologyRevival.location("sifter"), SIFTER);

    @Override
    public void register(EmiRegistry registry) {
        // Tell EMI to add a tab for your category
        registry.addCategory(WORKTABLE_CATEGORY);
        registry.addCategory(CULTURE_VAT_CATEGORY);
        registry.addCategory(ANALYZER_CATEGORY);
        registry.addCategory(SIFTER_CATEGORY);

        // Add all the workstations your category uses
        registry.addWorkstation(WORKTABLE_CATEGORY, WORKTABLE);
        registry.addWorkstation(CULTURE_VAT_CATEGORY, CULTURE_VAT);
        registry.addWorkstation(ANALYZER_CATEGORY, ANALYZER);
        registry.addWorkstation(SIFTER_CATEGORY, SIFTER);

        // 1.21.1's RecipeManager#getAllRecipesFor returns List<RecipeHolder<T>> (not source's List<T> -
        // RecipeHolder now owns the id/recipe pairing), so each wrapper takes the holder itself.
        RecipeManager recipeManager = registry.getRecipeManager();
        for (var holder : recipeManager.getAllRecipesFor(ModRecipes.WORKTABLE_TYPE.get())) {
            registry.addRecipe(new WorktableEmiRecipe(holder));
        }
        for (var holder : recipeManager.getAllRecipesFor(ModRecipes.CULTURE_VAT_TYPE.get())) {
            registry.addRecipe(new CultureVatEmiRecipe(holder));
        }
        for (var holder : recipeManager.getAllRecipesFor(ModRecipes.ANALYZER_TYPE.get())) {
            registry.addRecipe(new AnalyzerEmiRecipe(holder));
        }
        for (var holder : recipeManager.getAllRecipesFor(ModRecipes.SIFTER_TYPE.get())) {
            registry.addRecipe(new SifterEmiRecipe(holder));
        }

        registry.addRecipe(new AnalyzerEmiRecipe.Builder(ModItemTags.ALL_BONES)
                .addOutput(Items.BONE_MEAL, 30)
                .addOutput(Items.BONE, 35)
                .addOutput(ModItemTags.BONES_DNA, 35).build());
        registry.addRecipe(new AnalyzerEmiRecipe.Builder(ModItemTags.UNCOOKED_MEAT)
                .addOutput(ModItemTags.MEAT_DNA, 100).build());
        registry.addRecipe(new AnalyzerEmiRecipe.Builder(ModItemTags.DINO_EGGS)
                .addOutput(ModItemTags.DINO_DNA, 100).build());
        registry.addRecipe(new AnalyzerEmiRecipe.Builder(ModItemTags.FISH_EGGS)
                .addOutput(ModItemTags.FISH_DNA, 100).build());
        registry.addRecipe(new AnalyzerEmiRecipe.Builder(ModItemTags.EMBRYOS)
                .addOutput(ModItemTags.EMBRYO_DNA, 100).build());
    }
}
