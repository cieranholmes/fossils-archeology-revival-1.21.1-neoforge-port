package com.github.fossil.client.renderer;

import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.custom_blocks.AncientChestBlock;
import com.github.fossil.block.custom_blocks.AnuStatueBlock;
import com.github.fossil.block.custom_blocks.AnubiteStatueBlock;
import com.github.fossil.block.custom_blocks.SarcophagusBlock;
import com.github.fossil.block.entity.AncientChestBlockEntity;
import com.github.fossil.block.entity.AnuStatueBlockEntity;
import com.github.fossil.block.entity.AnubiteStatueBlockEntity;
import com.github.fossil.block.entity.SarcophagusBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Renders the held/GUI form of the "builtin/entity" item models (anu_statue, anubite_statue,
 * ancient_chest, sarcophagus) by dispatching to the same BlockEntityRenderer used for the
 * placed block, via dummy off-world BlockEntity instances. Without this registered as each
 * item's custom renderer, "builtin/entity" has no baked quads of its own, so the item renders
 * as nothing at all (not a missing-texture placeholder).
 */
public class CustomItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final CustomItemRenderer INSTANCE = new CustomItemRenderer();
    private final AnuStatueBlockEntity anuStatue = new AnuStatueBlockEntity(BlockPos.ZERO, ModBlocks.ANU_STATUE.get().defaultBlockState());
    private final AnubiteStatueBlockEntity anubiteStatue = new AnubiteStatueBlockEntity(BlockPos.ZERO,
            ModBlocks.ANUBITE_STATUE.get().defaultBlockState());
    private final AncientChestBlockEntity ancientChest = new AncientChestBlockEntity(BlockPos.ZERO,
            ModBlocks.ANCIENT_CHEST.get().defaultBlockState());
    private final SarcophagusBlockEntity sarcophagus = new SarcophagusBlockEntity(BlockPos.ZERO, ModBlocks.SARCOPHAGUS.get().defaultBlockState());
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

    protected CustomItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer,
                              int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof AnuStatueBlock) {
                blockEntityRenderDispatcher.renderItem(anuStatue, poseStack, buffer, packedLight, packedOverlay);
            } else if (block instanceof AnubiteStatueBlock) {
                blockEntityRenderDispatcher.renderItem(anubiteStatue, poseStack, buffer, packedLight, packedOverlay);
            } else if (block instanceof AncientChestBlock) {
                blockEntityRenderDispatcher.renderItem(ancientChest, poseStack, buffer, packedLight, packedOverlay);
            } else if (block instanceof SarcophagusBlock) {
                poseStack.pushPose();
                poseStack.scale(1, 0.65f, 1);
                blockEntityRenderDispatcher.renderItem(sarcophagus, poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
            }
            return;
        }
        super.renderByItem(stack, itemDisplayContext, poseStack, buffer, packedLight, packedOverlay);
    }
}
