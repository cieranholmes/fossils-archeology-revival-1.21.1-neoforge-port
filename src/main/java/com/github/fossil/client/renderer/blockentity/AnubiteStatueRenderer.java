package com.github.fossil.client.renderer.blockentity;

import com.github.fossil.Config;
import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.custom_blocks.AnubiteStatueBlock;
import com.github.fossil.block.entity.AnubiteStatueBlockEntity;
import com.github.fossil.client.model.AnubiteModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class AnubiteStatueRenderer implements BlockEntityRenderer<AnubiteStatueBlockEntity> {
    public static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/entity/anubite_statue.png");

    private final ModelPart anubiteModel;

    public AnubiteStatueRenderer(BlockEntityRendererProvider.Context context) {
        anubiteModel = AnubiteModel.createBodyLayer().bakeRoot();
    }

    @Override
    public void render(AnubiteStatueBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getBlockState().getValue(AnubiteStatueBlock.FACING).getOpposite().toYRot()));
        var c = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        if (blockEntity.getBlockState().getValue(AnubiteStatueBlock.LIT)) {
            anubiteModel.getChild("right_arm").xRot = -130 * Mth.DEG_TO_RAD;
        } else{
            anubiteModel.getChild("right_arm").xRot = Mth.lerp((float) blockEntity.getCooldown() / Config.anubiteCooldown,
                    -130, 0) * Mth.DEG_TO_RAD;
        }
        anubiteModel.render(poseStack, c, packedLight, packedOverlay);
        poseStack.popPose();
    }


}
