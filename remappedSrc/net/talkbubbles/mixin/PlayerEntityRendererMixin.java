package net.talkbubbles.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.fabricmc.api.EnvType;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import net.talkbubbles.util.RenderBubble;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerEntityRendererMixin(Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i,
            CallbackInfo info) {
        if (!abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isAlive()) {
            int oldAge = ((AbstractClientPlayerEntityAccessor) abstractClientPlayerEntity).getOldAge();
            if (oldAge != 0 && oldAge != -1) {
                if (abstractClientPlayerEntity.tickCount - oldAge > TalkBubbles.CONFIG.chatTime)
                    ((AbstractClientPlayerEntityAccessor) abstractClientPlayerEntity).setChatText(null, 0, 0, 0);
                List<String> textList = ((AbstractClientPlayerEntityAccessor) abstractClientPlayerEntity).getChatText();
                if (textList != null && !textList.isEmpty()) {
                    RenderBubble.renderBubble(matrixStack, vertexConsumerProvider, this.getFont(), this.entityRenderDispatcher, textList,
                            ((AbstractClientPlayerEntityAccessor) abstractClientPlayerEntity).getWidth(), ((AbstractClientPlayerEntityAccessor) abstractClientPlayerEntity).getHeight(),
                            abstractClientPlayerEntity.getBbHeight(), i);
                }
            }
        }
    }
}
