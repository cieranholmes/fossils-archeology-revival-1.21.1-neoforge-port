package com.github.fossil.data.providers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.item.ModItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Ported from the source mod's {@code FossilCreateRecipeProvider} (Forge-only, never had a NeoForge/Fabric
 * equivalent - Phase 9 task 7 datagen-only compat, no runtime Create integration exists elsewhere in this mod).
 * <p>
 * Create's own recipe-datagen API was reworked between the source's Create version and 6.0.10-280: there is no
 * more {@code com.simibubi.create.api.data.recipe.BaseRecipeProvider} + hand-rolled {@code IRecipeTypeInfo}
 * dispatch to subclass from directly - instead Create now ships a dedicated {@link CrushingRecipeGen} base
 * class per-processing-type (see {@code com.simibubi.create.foundation.data.recipe.CreateCrushingRecipeGen}
 * for Create's own equivalent usage) that already extends {@code StandardProcessingRecipeGen} /
 * {@code ProcessingRecipeGen} / {@code BaseRecipeProvider}, which in turn extends vanilla's
 * {@link net.minecraft.data.recipes.RecipeProvider} directly and auto-registers every recipe produced by its
 * {@code create(...)} helper - no manual {@code .register(consumer)} call needed as in source.
 * <p>
 * The condition gating is also far simpler now: {@code ProcessingRecipeBuilder.whenModLoaded(String)} (added
 * directly to Create's own builder) wraps NeoForge's real {@code net.neoforged.neoforge.common.conditions.ModLoadedCondition}
 * and threads it straight through to {@code RecipeOutput.accept(id, recipe, advancement, ICondition...)} -
 * NeoForge's actual current recipe-condition datagen mechanism. Source's Forge-{@code ICondition}/
 * {@code CraftingHelper} plumbing plus its hand-rolled {@code fabric:load_conditions} JSON shim
 * ({@code CustomProcessingRecipeBuilder}/{@code toFabric}) is entirely gone - NeoForge 1.21.1 has no Fabric
 * loader to shim for, and Create's builder now natively emits NeoForge's own conditions array.
 * <p>
 * The 2-arg {@code create(Supplier<ItemLike>, transform)} helper on {@code ProcessingRecipeGen} defaults its
 * recipe-id namespace to {@code Create.ID} (correct for Create's own in-house recipes) - this mod must use the
 * 3-arg {@code create(String namespace, Supplier<ItemLike>, transform)} overload with this mod's own id so the
 * generated recipe ids live under {@code fossil:crushing/...} rather than {@code create:crushing/...}, matching
 * source's {@code new ResourceLocation(FossilMod.MOD_ID, ...)} intent.
 */
public class ModCreateRecipeProvider extends CrushingRecipeGen {

    public ModCreateRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, FossilsAndArcheologyRevival.MODID);
        baseFossil(ModBlocks.CALCITE_FOSSIL::get, Blocks.CALCITE);
        baseFossil(ModBlocks.DRIPSTONE_FOSSIL::get, Blocks.DRIPSTONE_BLOCK);
        baseFossil(ModBlocks.RED_SANDSTONE_FOSSIL::get, Blocks.RED_SANDSTONE);
        baseFossil(ModBlocks.SANDSTONE_FOSSIL::get, Blocks.SANDSTONE);
        baseFossil(ModBlocks.STONE_FOSSIL::get, Blocks.COBBLESTONE);
        deepslateFossil(ModBlocks.DEEPSLATE_FOSSIL::get, Blocks.COBBLED_DEEPSLATE);
        deepslateFossil(ModBlocks.TUFF_FOSSIL::get, Blocks.TUFF);
    }

    private void baseFossil(Supplier<ItemLike> singleIngredient, Block block) {
        create(FossilsAndArcheologyRevival.MODID, singleIngredient, builder -> builder.duration(250)
                .output(0.2f, ModItems.BIO_FOSSIL.get())
                .output(0.1f, ModItems.PLANT_FOSSIL.get())
                .output(0.1f, ModItems.RELIC_SCRAP.get())
                .output(0.35f, Items.BONE)
                .output(0.125f, ModBlocks.SKULL_BLOCK.get())
                .output(0.125f, block)
                .whenModLoaded(Create.ID));
    }

    private void deepslateFossil(Supplier<ItemLike> singleIngredient, Block block) {
        create(FossilsAndArcheologyRevival.MODID, singleIngredient, builder -> builder.duration(250)
                .output(0.25f, ModItems.SHALE_FOSSIL.get())
                .output(0.05f, ModItems.PLANT_FOSSIL.get())
                .output(0.05f, ModItems.RELIC_SCRAP.get())
                .output(0.35f, Items.BONE)
                .output(0.125f, ModBlocks.SKULL_BLOCK.get())
                .output(0.175f, block)
                .whenModLoaded(Create.ID));
    }
}
