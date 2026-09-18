package net.talkbubbles.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import net.talkbubbles.mixin.accessor.AvatarRenderStateAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel> {

    public AvatarRendererMixin(Context context, PlayerModel model, float shadow) {
        super(context, model, shadow);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void extractRenderStateMixin(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo info) {
        AvatarRenderStateAccessor access = (AvatarRenderStateAccessor) state;

        int oldAge = ((AbstractClientPlayerEntityAccessor) entity).talkBubbles$getOldAge();
        if (oldAge == 0 || oldAge == -1) {
            access.talkBubbles$setChatText(null);
            return;
        }

        if (entity.tickCount - oldAge > TalkBubbles.CONFIG.chatTime) {
            ((AbstractClientPlayerEntityAccessor) entity).talkBubbles$setChatText(null, 0, 0, 0);
            access.talkBubbles$setChatText(null);
            return;
        }

        List<String> chat = ((AbstractClientPlayerEntityAccessor) entity).talkBubbles$getChatText();
        if (chat == null || chat.isEmpty()) {
            access.talkBubbles$setChatText(null);
            return;
        }

        access.talkBubbles$setChatText(List.copyOf(chat));
        access.talkBubbles$setWidth(((AbstractClientPlayerEntityAccessor) entity).talkBubbles$getWidth());
        access.talkBubbles$setHeight(((AbstractClientPlayerEntityAccessor) entity).talkBubbles$getHeight());
    }
}
