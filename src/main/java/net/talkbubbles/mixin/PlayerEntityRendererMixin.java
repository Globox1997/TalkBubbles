package net.talkbubbles.mixin;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
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
            if (oldAge != 0 && oldAge != -1) {
                if (abstractClientPlayerEntity.age - oldAge > TalkBubbles.CONFIG.chatTime)
                    ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).setChatText(null, 0, 0, 0);
                List<String> textList = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getChatText();
                if (textList != null && !textList.isEmpty()) {
                    matrixStack.push();

                    int backgroundWidth = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getWidth();
                    int backgroundHeight = ((OtherClientPlayerEntityAccessor) abstractClientPlayerEntity).getHeight();

                    float height = abstractClientPlayerEntity.getHeight() + 0.9F + (backgroundHeight > 5 ? 0.1F : 0.0F);
                    matrixStack.translate(0.0D, height, 0.0D);
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.dispatcher.getRotation().toEulerXyzDegrees().getY()));

                    matrixStack.scale(-0.025F, -0.025F, 0.025F);

                    Matrix4f matrix4f = matrixStack.peek().getModel();
                    TextRenderer textRenderer = this.getTextRenderer();

                    RenderSystem.setShaderColor(TalkBubbles.CONFIG.backgroundRed, TalkBubbles.CONFIG.backgroundGreen, TalkBubbles.CONFIG.backgroundBlue, TalkBubbles.CONFIG.backgroundOpacity);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableDepthTest();

                    RenderSystem.enablePolygonOffset();
                    RenderSystem.polygonOffset(3.0F, 3.0F);

                    RenderSystem.setShaderTexture(0, new Identifier("talkbubbles:textures/gui/background.png"));

                    // Top left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 0.0F, 0.0F, 5, 5, 32, 32);
                    // Mid left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 0.0F, 6.0F,
                            5, 1, 32, 32);
                    // Bottom left
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 - 2, 5 + (backgroundHeight - 1), 5, 5, 0.0F, 8.0F, 5, 5, 32, 32);

                    // Top mid
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7, backgroundWidth - 4, 5, 6.0F, 0.0F, 5, 5, 32, 32);
                    // Mid mid
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, backgroundWidth - 4,
                            backgroundHeight + (backgroundHeight - 1) * 8, 6.0F, 6.0F, 5, 1, 32, 32);
                    // Bottom mid
                    DrawableHelper.drawTexture(matrixStack, -backgroundWidth / 2 + 3, 5 + (backgroundHeight - 1), backgroundWidth - 4, 5, 6.0F, 8.0F, 5, 5, 32, 32);

                    // Top right
                    DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7, 5, 5, 12.0F, 0.0F, 5, 5, 32, 32);
                    // Mid right
                    DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, -backgroundHeight - (backgroundHeight - 1) * 7 + 5, 5, backgroundHeight + (backgroundHeight - 1) * 8, 12.0F, 6.0F,
                            5, 1, 32, 32);
                    // Bottom right
                    DrawableHelper.drawTexture(matrixStack, backgroundWidth / 2 - 1, 5 + (backgroundHeight - 1), 5, 5, 12.0F, 8.0F, 5, 5, 32, 32);

                    RenderSystem.polygonOffset(0.0F, 0.0F);
                    RenderSystem.disablePolygonOffset();
                    for (int u = textList.size(); u > 0; u--) {
                        float h = (float) (-textRenderer.getWidth(textList.get(u - 1))) / 2.0F;
                        textRenderer.draw(textList.get(u - 1), h, ((float) textList.size() + (u - textList.size()) * 9), TalkBubbles.CONFIG.chatColor, false, matrix4f, vertexConsumerProvider, false,
                                0, i);
                    }
                    matrixStack.pop();

                    RenderSystem.disableBlend();
                }
            }
        }
    }
}
