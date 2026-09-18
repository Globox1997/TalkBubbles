package net.talkbubbles.mixin.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderState.class)
public interface AvatarRenderStateAccessor {

    @Accessor("talkBubbles$chatText")
    void talkBubbles$setChatText(List<String> text);

    @Accessor("talkBubbles$chatText")
    @Nullable
    List<String> talkBubbles$getChatText();

    @Accessor("talkBubbles$width")
    void talkBubbles$setWidth(int width);

    @Accessor("talkBubbles$width")
    int talkBubbles$getWidth();

    @Accessor("talkBubbles$height")
    void talkBubbles$setHeight(int height);

    @Accessor("talkBubbles$height")
    int talkBubbles$getHeight();

}
