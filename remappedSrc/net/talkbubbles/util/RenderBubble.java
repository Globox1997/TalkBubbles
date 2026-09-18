package net.talkbubbles.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.mixin.DrawContextAccessor;

@Environment(EnvType.CLIENT)
public class RenderBubble {

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("talkbubbles:textures/gui/background.png");

    public static void renderBubble(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, Font textRenderer, EntityRenderDispatcher entityRenderDispatcher,
            List<String> textList, int width, int height, float playerHeight, int i) {
        matrixStack.pushPose();

        int backgroundWidth = width;
        int backgroundHeight = height;

        matrixStack.translate(0.0D, playerHeight + 0.9F + (backgroundHeight > 5 ? 0.1F : 0.0F) + TalkBubbles.CONFIG.chatHeight, 0.0D);
        matrixStack.mulPose(Axis.YP.rotationDegrees(toEulerXyzDegrees(entityRenderDispatcher.cameraOrientation()).y()));
        matrixStack.scale(0.025F * TalkBubbles.CONFIG.chatScale, -0.025F * TalkBubbles.CONFIG.chatScale, 0.025F);

        Matrix4f matrix4f = matrixStack.last().pose();

        RenderSystem.setShaderColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(3.0F, 3.0F);
        Minecraft client = Minecraft.getInstance();
        GuiGraphics context = DrawContextAccessor.getDrawContext(client, matrixStack, client.renderBuffers().bufferSource());
        // Top left
        context.blit(BACKGROUND, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 0.0F, 0.0F, 5, 5, 32, 32);
        // Mid left
        context.blit(BACKGROUND, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 0.0F, 6.0F, 5, 1, 32, 32);
        // Bottom left
        context.blit(BACKGROUND, -backgroundWidth / 2 - 2, 5 + (backgroundHeight - 1), 5, 5, 0.0F, 8.0F, 5, 5, 32, 32);

        // Top mid
        context.blit(BACKGROUND, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7, backgroundWidth - 4, 5, 6.0F, 0.0F, 5, 5, 32, 32);
        // Mid mid
        context.blit(BACKGROUND, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, backgroundWidth - 4, backgroundHeight + (backgroundHeight - 1) * 8, 6.0F, 6.0F,
                5, 1, 32, 32);
        // Bottom mid
        context.blit(BACKGROUND, -backgroundWidth / 2 + 3, 5 + (backgroundHeight - 1), backgroundWidth - 4, 5, 6.0F, 8.0F, 5, 5, 32, 32);

        // Top right
        context.blit(BACKGROUND, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 12.0F, 0.0F, 5, 5, 32, 32);
        // Mid right
        context.blit(BACKGROUND, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 12.0F, 6.0F, 5, 1, 32, 32);
        // Bottom right
        context.blit(BACKGROUND, backgroundWidth / 2 - 1, 5 + (backgroundHeight - 1), 5, 5, 12.0F, 8.0F, 5, 5, 32, 32);

        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        for (int u = textList.size(); u > 0; u--) {
            float h = (float) (-textRenderer.width(textList.get(u - 1))) / 2.0F;
            textRenderer.drawInBatch(textList.get(u - 1), h, ((float) textList.size() + (u - textList.size()) * 9), TalkBubbles.CONFIG.chatColor, false, matrix4f, vertexConsumerProvider,
                    Font.DisplayMode.NORMAL, 0, i);
        }
        matrixStack.popPose();

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
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
