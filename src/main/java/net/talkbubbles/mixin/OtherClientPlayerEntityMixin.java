package net.talkbubbles.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.talkbubbles.accessor.OtherClientPlayerEntityAccessor;

@Environment(EnvType.CLIENT)
@Mixin(OtherClientPlayerEntity.class)
public class OtherClientPlayerEntityMixin implements OtherClientPlayerEntityAccessor {

    @Nullable
    private List<String> chatTextList = null;
    private int oldAge = 0;
    private int width;
    private int height;

    @Override
    public void setChatText(List<String> textList, int currentAge, int width, int height) {
        this.chatTextList = textList;
        this.oldAge = currentAge;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public List<String> getChatText() {
        return this.chatTextList;
    }

    @Override
    public int getOldAge() {
        return this.oldAge;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

}
