package net.talkbubbles.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.ClientPlayerEntityAccessor;
import net.talkbubbles.util.RenderBubble;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
            CallbackInfo info) {
        if (!abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isAlive()) {
            int oldAge = ((ClientPlayerEntityAccessor) abstractClientPlayerEntity).getOldAge();
            if (oldAge != 0 && oldAge != -1) {
                if (abstractClientPlayerEntity.age - oldAge > TalkBubbles.CONFIG.chatTime)
                    ((ClientPlayerEntityAccessor) abstractClientPlayerEntity).setChatText(null, 0, 0, 0);
                List<String> textList = ((ClientPlayerEntityAccessor) abstractClientPlayerEntity).getChatText();
                if (textList != null && !textList.isEmpty()) {
                    RenderBubble.renderBubble(matrixStack, vertexConsumerProvider, this.getTextRenderer(), this.dispatcher, textList,
                            ((ClientPlayerEntityAccessor) abstractClientPlayerEntity).getWidth(), ((ClientPlayerEntityAccessor) abstractClientPlayerEntity).getHeight(),
                            abstractClientPlayerEntity.getHeight(), i);
                }
            }
        }
    }
}
