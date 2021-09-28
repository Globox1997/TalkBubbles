package net.talkbubbles.mixin;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.system.CallbackI.I;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.OtherClientPlayerEntityAccessor;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
            CallbackInfo info) {
        if (abstractClientPlayerEntity instanceof OtherClientPlayerEntity) {
            int oldAge = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getOldAge();
            if (oldAge != 0) {
                if (abstractClientPlayerEntity.age - oldAge > TalkBubbles.CONFIG.chatTime)
                    ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).setChatText(null, 0, 0, 0);
                List<String> textList = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getChatText();
                if (textList != null && !textList.isEmpty()) {
                    matrixStack.push();
                    float height = abstractClientPlayerEntity.getHeight() + 1.1F + TalkBubbles.CONFIG.test;
                    matrixStack.translate(0.0D, height, 0.0D);
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.dispatcher.getRotation().toEulerXyzDegrees().getY()));

                    matrixStack.scale(-0.025F, -0.025F, 0.025F);

                    Matrix4f matrix4f = matrixStack.peek().getModel();
                    TextRenderer textRenderer = this.getTextRenderer();

                    int backgroundWidth = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getWidth();
                    int backgroundHeight = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getHeight();

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, TalkBubbles.CONFIG.backgroundOpacity);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableDepthTest();

                    RenderSystem.enablePolygonOffset();
                    RenderSystem.polygonOffset(TalkBubbles.CONFIG.test5, TalkBubbles.CONFIG.test5);

                    RenderSystem.setShaderTexture(0, new Identifier("talkbubbles:textures/gui/background.png"));

                    // Top left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 0.0F, 0.0F, 5, 5, 32, 32);
                    // Mid left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5,
                            backgroundHeight + (backgroundHeight - 1) * TalkBubbles.CONFIG.testi4, 0.0F, 6.0F, 5, 1, 32, 32);
                    // Bottom left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2, 4 + backgroundHeight * TalkBubbles.CONFIG.testi5, 5, 5, 0.0F, 8.0F, 5, 5, 32, 32);

                    // // Top mid
                    // DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 5, -backgroundHeight, backgroundWidth - 5 + TalkBubbles.CONFIG.testi2, 5, 6.0F, 0.0F, 5, 5, 32, 32);
                    // // Mid mid
                    // DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 5, -backgroundHeight + 5, backgroundWidth - 5 + TalkBubbles.CONFIG.testi2, 1, 6.0F, 6.0F, 5, 1, 32, 32);
                    // // Bottom mid
                    // DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 5, 5, backgroundWidth - 5 + TalkBubbles.CONFIG.testi2, 5, 6.0F, 8.0F, 5, 5, 32, 32);

                    // // Top right
                    // DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 + TalkBubbles.CONFIG.testi2, -backgroundHeight, 5, 5, 12.0F, 0.0F, 5, 5, 32, 32);
                    // // Mid right
                    // DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 + TalkBubbles.CONFIG.testi2, -backgroundHeight + 5, 5, 1, 12.0F, 6.0F, 5, 1, 32, 32);
                    // // Bottom right
                    // DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 + TalkBubbles.CONFIG.testi2, 5, 5, 5, 12.0F, 8.0F, 5, 5, 32, 32);

                    RenderSystem.polygonOffset(0.0F, 0.0F);
                    RenderSystem.disablePolygonOffset();
                    for (int u = textList.size(); u > 0; u--) {
                        float h = (float) (-textRenderer.getWidth(textList.get(u - 1)) / 2);
                        textRenderer.draw(textList.get(u - 1), h, (float) textList.size() + (u - textList.size()) * 9, TalkBubbles.CONFIG.chatColor, false, matrix4f, vertexConsumerProvider, false, 0,
                                i);
                    }
                    matrixStack.pop();

                    RenderSystem.disableBlend();
                }
            }
        } else {

            matrixStack.push();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, new Identifier("talkbubbles:textures/gui/background.png"));

            float height = abstractClientPlayerEntity.getHeight() + 1.1F + TalkBubbles.CONFIG.test;
            matrixStack.translate(0.0D, height, 0.0D);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.dispatcher.getRotation().toEulerXyzDegrees().getY()));
            matrixStack.scale(-0.025F, -0.025F, 0.025F);

            // Top left
            DrawableHelper.drawTexture(matrixStack, 0, -5, 5, 5, 0.0F, 0.0F, 5, 5, 32, 32);
            // Mid left
            DrawableHelper.drawTexture(matrixStack, 0, 0, 5, 5, 0.0F, 6.0F, 5, 5, 32, 32);
            // Bottom left
            DrawableHelper.drawTexture(matrixStack, 0, 5, 5, 5, 0.0F, 12.0F, 5, 5, 32, 32);
            // Top mid
            DrawableHelper.drawTexture(matrixStack, 5, -5, 5, 5, 6.0F, 0.0F, 5, 5, 32, 32);
            // Mid mid
            DrawableHelper.drawTexture(matrixStack, 5, 0, 5, 5, 6.0F, 6.0F, 5, 5, 32, 32);
            // Bottom mid
            DrawableHelper.drawTexture(matrixStack, 5, 5, 5, 5, 6.0F, 12.0F, 5, 5, 32, 32);
            // Top right
            DrawableHelper.drawTexture(matrixStack, 10, -5, 5, 5, 12.0F, 0.0F, 5, 5, 32, 32);
            // Mid right
            DrawableHelper.drawTexture(matrixStack, 10, 0, 5, 5, 12.0F, 6.0F, 5, 5, 32, 32);
            // Bottom right
            DrawableHelper.drawTexture(matrixStack, 10, 5, 5, 5, 12.0F, 12.0F, 5, 5, 32, 32);
            RenderSystem.disableBlend();
            matrixStack.pop();
        }
    }

    // private void AddTopBackground(int gridYSpace, PlayerQuestPanel plainPanel) {
    // // Top Left
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0F, 0.59375F, 0.01953125F, 0.61328125F), 0, gridYSpace - 3, 10, 10);
    // // Top Mid
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0234375F, 0.59375F, 0.04296875F, 0.61328125F), 10, gridYSpace - 3, 245 - 10, 10);
    // // Top Right
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.046875F, 0.59375F, 0.06640625F, 0.61328125F), 245, gridYSpace - 3, 10, 10);
    // }

    // private void AddMidBottomBackground(int topBackGround, int gridYSpace, PlayerQuestPanel plainPanel) {
    // // Mid Left
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0F, 0.6171875F, 0.01953125F, 0.634765625F), 0, topBackGround, 10, gridYSpace - topBackGround);
    // // Mid Mid
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0234375F, 0.6171875F, 0.04296875F, 0.634765625F), 10, topBackGround, 245 - 10, gridYSpace - topBackGround);
    // // Mid Right
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.046875F, 0.6171875F, 0.06640625F, 0.634765625F), 245, topBackGround, 10, gridYSpace - topBackGround);
    // // Bottom Left
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0F, 0.640625F, 0.01953125F, 0.66015625F), 0, gridYSpace, 10, 10);
    // // Bottom Mid
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.0234375F, 0.640625F, 0.04296875F, 0.66015625F), 10, gridYSpace, 245 - 10, 10);
    // // Bottom Right
    // plainPanel.add(new WSprite(QuestScreenHandler.GUI_ICONS, 0.046875F, 0.640625F, 0.06640625F, 0.66015625F), 245, gridYSpace, 10, 10);
    // }

    // Matrix4f matrix4f = matrixStack.peek().getModel();
    // VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getTextSeeThrough(new Identifier("")));
    // vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();

    // vertexConsumer.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);

    // public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
    // vertexConsumer.vertex(matrix4f, 0.0F, TalkBubbles.CONFIG.test2, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, TalkBubbles.CONFIG.test3, TalkBubbles.CONFIG.test4, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, TalkBubbles.CONFIG.test5, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
    // vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();

}
