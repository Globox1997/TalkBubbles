package net.talkbubbles.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin {

    @Unique
    private @Nullable List<String> talkBubbles$chatText = null;

    @Unique
    private int talkBubbles$width = 0;

    @Unique
    private int talkBubbles$height = 0;

}
