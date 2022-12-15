package net.talkbubbles.util;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.talkbubbles.TalkBubbles;

@Environment(EnvType.CLIENT)
public class RenderBubble {

    private static final Identifier BACKGROUND = new Identifier("talkbubbles:textures/gui/background.png");

    public static void renderBubble(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, TextRenderer textRenderer, EntityRenderDispatcher entityRenderDispatcher,
            List<String> textList, int width, int height, float playerHeight, int i) {
        matrixStack.push();

        int backgroundWidth = width;
        int backgroundHeight = height;

        matrixStack.translate(0.0D, playerHeight + 0.9F + (backgroundHeight > 5 ? 0.1F : 0.0F), 0.0D);
        // matrixStack.multiply(RotationAxis.POSITIVE_Y.getDegreesQuaternion(entityRenderDispatcher.getRotation().toEulerXyzDegrees().getY()));´
        // Not sure if this is now the right way
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(entityRenderDispatcher.getRotation().getEulerAnglesZXY(new Vector3f()).y()));

        matrixStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        RenderSystem.setShaderColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(3.0F, 3.0F);

        RenderSystem.setShaderTexture(0, BACKGROUND);

        // Top left
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 0.0F, 0.0F, 5, 5, 32, 32);
        // Mid left
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 0.0F, 6.0F, 5, 1, 32,
                32);
        // Bottom left
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, 5 + (backgroundHeight - 1), 5, 5, 0.0F, 8.0F, 5, 5, 32, 32);

        // Top mid
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7, backgroundWidth - 4, 5, 6.0F, 0.0F, 5, 5, 32, 32);
        // Mid mid
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, backgroundWidth - 4, backgroundHeight + (backgroundHeight - 1) * 8, 6.0F,
                6.0F, 5, 1, 32, 32);
        // Bottom mid
        DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, 5 + (backgroundHeight - 1), backgroundWidth - 4, 5, 6.0F, 8.0F, 5, 5, 32, 32);

        // Top right
        DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 12.0F, 0.0F, 5, 5, 32, 32);
        // Mid right
        DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 12.0F, 6.0F, 5, 1, 32,
                32);
        // Bottom right
        DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, 5 + (backgroundHeight - 1), 5, 5, 12.0F, 8.0F, 5, 5, 32, 32);

        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        for (int u = textList.size(); u > 0; u--) {
            float h = (float) (-textRenderer.getWidth(textList.get(u - 1))) / 2.0F;
            textRenderer.draw(textList.get(u - 1), h, ((float) textList.size() + (u - textList.size()) * 9), TalkBubbles.CONFIG.chatColor, false, matrix4f, vertexConsumerProvider, false, 0, i);
        }
        matrixStack.pop();

        RenderSystem.disableBlend();
    }

}
