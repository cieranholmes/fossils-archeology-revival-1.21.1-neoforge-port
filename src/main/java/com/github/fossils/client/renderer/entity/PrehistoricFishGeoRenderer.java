package com.github.fossils.client.renderer.entity;

import com.github.fossils.client.model.PrehistoricFishGeoModel;
import com.github.fossils.entity.prehistoric.base.PrehistoricFish;
import com.github.fossils.entity.prehistoric.fish.Nautilus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrehistoricFishGeoRenderer<T extends PrehistoricFish> extends GeoEntityRenderer<T> {

    public PrehistoricFishGeoRenderer(EntityRendererProvider.Context renderManager, String model) {
        super(renderManager, new PrehistoricFishGeoModel<>(model));
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        if (animatable instanceof Nautilus) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        }
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = entity.getBbWidth() * 0.45F;
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
