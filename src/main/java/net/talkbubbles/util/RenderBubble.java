package net.talkbubbles.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.talkbubbles.TalkBubbles;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderBubble {

    private static final Identifier BACKGROUND = Identifier.parse("talkbubbles:textures/gui/background.png");

    public static final RenderType BUBBLE_BACKGROUND = RenderType.create("talkbubbles_bubble_background",
            RenderSetup.builder(RenderPipelines.TEXT).withTexture("Sampler0", BACKGROUND).useLightmap().useOverlay().sortOnUpload().setOutline(RenderSetup.OutlineProperty.NONE).createRenderSetup());

    public static void renderBubble(PoseStack matrixStack, SubmitNodeCollector collector, Font textRenderer, CameraRenderState cameraState, List<String> textList, int width, int height, float playerHeight) {

        matrixStack.pushPose();

        matrixStack.translate(0.0D, playerHeight + 0.9F + TalkBubbles.CONFIG.chatHeight, 0.0D);

        float yawDegrees = toEulerXyzDegrees(new Quaternionf(cameraState.orientation)).y();
        matrixStack.rotateDegrees(Axis.YP, yawDegrees);

        matrixStack.scale(0.025F * TalkBubbles.CONFIG.chatScale, -0.025F * TalkBubbles.CONFIG.chatScale, 0.025F);

        final int bgWidth = width;
        final int bgHeight = height;
        final int lightCoords = 15728880;

        collector.submitCustomGeometry(matrixStack, BUBBLE_BACKGROUND, (pose, vertexConsumer) -> {
            // Top-left corner
            addQuad(vertexConsumer, pose, -bgWidth / 2f - 2, -bgHeight - (bgHeight - 1) * 7, 5, 5,
                    0.0f, 0.0f, 5, 5, 32, 32, lightCoords);
            // Mid-left
            addQuad(vertexConsumer, pose, -bgWidth / 2f - 2, -bgHeight - (bgHeight - 1) * 7 + 5, 5,
                    bgHeight + (bgHeight - 1) * 8,
                    0.0f, 6.0f, 5, 1, 32, 32, lightCoords);
            // Bottom-left corner
            addQuad(vertexConsumer, pose, -bgWidth / 2f - 2, 5 + (bgHeight - 1), 5, 5,
                    0.0f, 8.0f, 5, 5, 32, 32, lightCoords);
            // Top-mid
            addQuad(vertexConsumer, pose, -bgWidth / 2f + 3, -bgHeight - (bgHeight - 1) * 7,
                    bgWidth - 4, 5,
                    6.0f, 0.0f, 5, 5, 32, 32, lightCoords);
            // Mid-mid
            addQuad(vertexConsumer, pose, -bgWidth / 2f + 3,
                    -bgHeight - (bgHeight - 1) * 7 + 5, bgWidth - 4,
                    bgHeight + (bgHeight - 1) * 8,
                    6.0f, 6.0f, 5, 1, 32, 32, lightCoords);
            // Bottom-mid
            addQuad(vertexConsumer, pose, -bgWidth / 2f + 3, 5 + (bgHeight - 1),
                    bgWidth - 4, 5,
                    6.0f, 8.0f, 5, 5, 32, 32, lightCoords);
            // Top-right corner
            addQuad(vertexConsumer, pose, bgWidth / 2f - 1, -bgHeight - (bgHeight - 1) * 7,
                    5, 5,
                    12.0f, 0.0f, 5, 5, 32, 32, lightCoords);
            // Mid-right
            addQuad(vertexConsumer, pose, bgWidth / 2f - 1,
                    -bgHeight - (bgHeight - 1) * 7 + 5, 5,
                    bgHeight + (bgHeight - 1) * 8,
                    12.0f, 6.0f, 5, 1, 32, 32, lightCoords);
            // Bottom-right corner
            addQuad(vertexConsumer, pose, bgWidth / 2f - 1, 5 + (bgHeight - 1), 5, 5,
                    12.0f, 8.0f, 5, 5, 32, 32, lightCoords);
        });

        for (int u = textList.size(); u > 0; u--) {
            String line = textList.get(u - 1);
            float textX = (float) (-textRenderer.width(line)) / 2.0F;
            float textY = ((float) textList.size() + (u - textList.size()) * 9);

            FormattedCharSequence orderedText = FormattedCharSequence.forward(line, Style.EMPTY);

            collector.submitText(matrixStack, textX, textY, orderedText, false, Font.DisplayMode.NORMAL, lightCoords, ARGB.opaque(TalkBubbles.CONFIG.chatColor), 0, 0);
        }

        matrixStack.popPose();
    }

    private static final float BACKGROUND_Z_OFFSET = -1.0f;

    private static void addQuad(VertexConsumer vc, PoseStack.Pose pose, float x, float y, float w, float h, float u, float v, float uw, float vh, int textureWidth, int textureHeight, int lightCoords) {
        float u0 = u / textureWidth;
        float u1 = (u + uw) / textureWidth;
        float v0 = v / textureHeight;
        float v1 = (v + vh) / textureHeight;

        // Top-left vertex
        vc.addVertex(pose, x, y, BACKGROUND_Z_OFFSET).setColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity).setUv(u0, v0).setLight(lightCoords).setOverlay(0).setNormal(0, 0, 1);

        // Bottom-left vertex
        vc.addVertex(pose, x, y + h, BACKGROUND_Z_OFFSET).setColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity).setUv(u0, v1).setLight(lightCoords).setOverlay(0).setNormal(0, 0, 1);

        // Bottom-right vertex
        vc.addVertex(pose, x + w, y + h, BACKGROUND_Z_OFFSET).setColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity).setUv(u1, v1).setLight(lightCoords).setOverlay(0).setNormal(0, 0, 1);

        // Top-right vertex
        vc.addVertex(pose, x + w, y, BACKGROUND_Z_OFFSET).setColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity).setUv(u1, v0).setLight(lightCoords).setOverlay(0).setNormal(0, 0, 1);
    }

    private static Vector3f toEulerXyz(Quaternionf quaternionf) {
        float f = quaternionf.w() * quaternionf.w();
        float g = quaternionf.x() * quaternionf.x();
        float h = quaternionf.y() * quaternionf.y();
        float i = quaternionf.z() * quaternionf.z();
        float j = f + g + h + i;
        float k = 2.0f * quaternionf.w() * quaternionf.x() - 2.0f * quaternionf.y() * quaternionf.z();
        float l = (float) Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vector3f(l, 2.0f * (float) Math.atan2(quaternionf.y(), quaternionf.w()), 0.0f);
        }
        return new Vector3f(l, (float) Math.atan2(2.0f * quaternionf.x() * quaternionf.z() + 2.0f * quaternionf.y() * quaternionf.w(), f - g - h + i),
                (float) Math.atan2(2.0f * quaternionf.x() * quaternionf.y() + 2.0f * quaternionf.w() * quaternionf.z(), f - g + h - i));
    }

    private static Vector3f toEulerXyzDegrees(Quaternionf quaternionf) {
        Vector3f vec3f = RenderBubble.toEulerXyz(quaternionf);
        return new Vector3f((float) Math.toDegrees(vec3f.x()), (float) Math.toDegrees(vec3f.y()), (float) Math.toDegrees(vec3f.z()));
    }

}
