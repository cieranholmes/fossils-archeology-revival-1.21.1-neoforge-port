package com.github.fossil.mixin;

import com.github.fossil.client.gui.debug.instruction.InstructionRenderer;
import com.github.fossil.client.gui.debug.navigation.PathingRenderer;
import com.github.fossil.util.Version;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class DebugLevelRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    private Frustum cullingFrustum;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V",
            shift = At.Shift.AFTER))
    private void renderDebugPaths(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                   LightTexture lightTexture, Matrix4f projectionMatrix, Matrix4f frustumMatrix, CallbackInfo ci,
                                   @Local(ordinal = 0) PoseStack poseStack) {
        if (Version.debugEnabled()) {
            MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            long nanoTime = System.nanoTime();
            final Minecraft mc = Minecraft.getInstance();
            final Vec3 viewPosition = mc.gameRenderer.getMainCamera().getPosition();
            poseStack.pushPose();
            poseStack.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
            PathingRenderer.render(poseStack, bufferSource, partialTick, nanoTime);
            InstructionRenderer.render(poseStack, bufferSource, partialTick, nanoTime, cullingFrustum);
            bufferSource.endBatch();
            poseStack.popPose();
        }
    }
}
