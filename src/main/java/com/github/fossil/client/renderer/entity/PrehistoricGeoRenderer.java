package com.github.fossil.client.renderer.entity;

import com.github.fossil.client.model.PrehistoricGeoModel;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.function.Function;

public class PrehistoricGeoRenderer<T extends Prehistoric> extends GeoEntityRenderer<T> {
    /**
     * @param model the file model name (excluding extension)
     */
    public PrehistoricGeoRenderer(EntityRendererProvider.Context renderManager, String model, Function<ResourceLocation, RenderType> renderType) {
        super(renderManager, new PrehistoricGeoModel<>(model, renderType));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = entity.getBbWidth() * 0.45F;
        poseStack.pushPose();
        if (entity.getClimbingDirection() != Direction.UP || (entity.prevClimbDirection != Direction.UP && entity.climbTick > 0)) {
            float progress = Mth.lerp(partialTick, entity.prevClimbTick, entity.climbTick) / 5f;
            Direction dir = entity.getClimbingDirection() != Direction.UP ? entity.getClimbingDirection() : entity.prevClimbDirection;
            float offset = entity.getBbWidth() / 2 * progress;
            poseStack.translate(dir.getStepX() * offset, 0.5 * progress, dir.getStepZ() * offset);
            Direction dirRot = dir.getClockWise();
            poseStack.mulPose(Axis.of(new Vector3f(dirRot.getStepX(), 0, dirRot.getStepZ())).rotationDegrees(90 * progress));
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public float getMotionAnimThreshold(T animatable) {
        return Util.SWING_ANIM_THRESHOLD;
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));
        if (animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;

            poseStack.mulPose(Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        } else if (animatable.hasCustomName()) {
            String name = ChatFormatting.stripFormatting(animatable.getName().getString());

            if (name != null && (name.equals("Dinnerbone") || name.equalsIgnoreCase("Grumm"))) {
                poseStack.translate(0, animatable.getBbHeight() + 0.1f, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        }
    }
}
