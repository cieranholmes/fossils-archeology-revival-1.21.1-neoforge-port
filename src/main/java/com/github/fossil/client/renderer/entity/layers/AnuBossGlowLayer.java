package com.github.fossil.client.renderer.entity.layers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.client.model.AnuBossModel;
import com.github.fossil.entity.monster.AnuBoss;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class AnuBossGlowLayer extends RenderLayer<AnuBoss, AnuBossModel> {
    private static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/entity/anu_boss_overlay.png");

    public AnuBossGlowLayer(RenderLayerParent<AnuBoss, AnuBossModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AnuBoss livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.WHITE_OVERLAY_V, FastColor.ARGB32.colorFromFloat(1, 1, 1, 1));
    }
}
