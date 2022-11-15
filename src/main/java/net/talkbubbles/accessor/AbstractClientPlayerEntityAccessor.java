package net.talkbubbles.accessor;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public interface AbstractClientPlayerEntityAccessor {

    public void setChatText(List<String> text, int currentAge, int width, int height);

    @Nullable
    public List<String> getChatText();

    public int getOldAge();

    public int getWidth();

    public int getHeight();
}
