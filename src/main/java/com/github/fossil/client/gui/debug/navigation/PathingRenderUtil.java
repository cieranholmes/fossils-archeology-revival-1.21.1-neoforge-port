package com.github.fossil.client.gui.debug.navigation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import software.bernie.geckolib.util.Color;

/**
 * Ported from source's {@code PathingRenderUtil}. The manual immediate-mode line/text drawing had to be rewritten
 * against 1.21.1's rebuilt vertex-buffer API: {@code Tesselator.getBuilder()} + {@code begin/vertex/color/endVertex}
 * no longer exists - a {@code BufferBuilder} is now obtained fresh from {@code Tesselator.begin(mode, format)} per
 * draw call, vertices are appended with the {@code VertexConsumer} default methods {@code addVertex}/{@code setColor}
 * (no explicit {@code endVertex}), and the finished mesh is flushed via {@code BufferUploader.drawWithShader}
 * instead of {@code Tesselator.end()}. {@code PoseStack.mulPoseMatrix(Matrix4f)} was also renamed to
 * {@code mulPose(Matrix4f)}. Functionally equivalent to source - draws the same single GL_LINES segment.
 */
public class PathingRenderUtil {

    public static void renderLineBox(PoseStack poseStack, MultiBufferSource buffer, BlockPos blockPos) {
        LevelRenderer.renderLineBox(poseStack, buffer.getBuffer(RenderType.LINES), new AABB(blockPos), 1, 1, 1, 0.75f);
    }

    public static void renderLine(PoseStack poseStack, double x0, double y0, double z0, double x1, double y1, double z1, Color color) {
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f pose = poseStack.last().pose();
        bufferBuilder.addVertex(pose, (float) x0, (float) y0, (float) z0)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.addVertex(pose, (float) x1, (float) y1, (float) z1)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        poseStack.popPose();
    }

    public static void renderLine(PoseStack poseStack, Vec3 start, Vec3 end) {
        renderLine(poseStack, start.x, start.y, start.z, end.x, end.y, end.z, Color.WHITE);
    }

    public static void renderLine(PoseStack poseStack, Node start, Node end) {
        renderLine(poseStack, start.x + 0.5f, start.y, start.z + 0.5f, end.x + 0.5f, end.y, end.z + 0.5f, Color.WHITE);
    }

    public static void renderTextBatch(PoseStack poseStack, Minecraft minecraft, Node[] nodes, int end) {
        RenderSystem.depthMask(true);
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new ByteBufferBuilder(1536));

        poseStack.pushPose();
        for (int i = 0; i < end; i++) {
            poseStack.pushPose();
            poseStack.translate(nodes[i].x + 0.5, nodes[i].y + 0.5, nodes[i].z + 0.5);
            poseStack.mulPose(new Matrix4f().rotation(minecraft.gameRenderer.getMainCamera().rotation()));
            poseStack.scale(0.02f, -0.02f, 0.02f);
            poseStack.scale(-1, 1, 1);
            String string = String.format("%s", nodes[i].type);
            float g = -minecraft.font.width(string) / 2f;
            minecraft.font.drawInBatch(string, g, 0, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
            poseStack.popPose();
        }
        bufferSource.endBatch();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
