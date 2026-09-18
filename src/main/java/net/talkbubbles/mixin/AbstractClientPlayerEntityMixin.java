package net.talkbubbles.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerEntityMixin implements AbstractClientPlayerEntityAccessor {

    @Unique
    @Nullable
    private List<String> chatTextList = null;
    @Unique
    private int oldAge = 0;
    @Unique
    private int width;
    @Unique
    private int height;

    @Override
    public void talkBubbles$setChatText(List<String> textList, int currentAge, int width, int height) {
        this.chatTextList = textList;
        this.oldAge = currentAge;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public List<String> talkBubbles$getChatText() {
        return this.chatTextList;
    }

    @Override
    public int talkBubbles$getOldAge() {
        return this.oldAge;
    }

    @Override
    public int talkBubbles$getWidth() {
        return this.width;
    }

    @Override
    public int talkBubbles$getHeight() {
        return this.height;
    }
}
