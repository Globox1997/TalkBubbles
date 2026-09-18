package net.talkbubbles.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.talkbubbles.mixin.accessor.AvatarRenderStateAccessor;
import net.talkbubbles.util.RenderBubble;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends EntityRenderer<T, S> {

    public LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void renderMixin(S renderState, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo info) {
        if (!(renderState instanceof AvatarRenderState avatarState)) {
            return;
        }

        AvatarRenderStateAccessor access = (AvatarRenderStateAccessor) avatarState;
        List<String> textList = access.talkBubbles$getChatText();

        if (textList == null || textList.isEmpty()) {
            return;
        }
        if (avatarState.isInvisible) {
            return;
        }

        RenderBubble.renderBubble(matrixStack, collector, Minecraft.getInstance().font, cameraState, textList, access.talkBubbles$getWidth(), access.talkBubbles$getHeight(), avatarState.boundingBoxHeight);
    }
}
