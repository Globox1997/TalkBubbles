package net.talkbubbles.accessor;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public interface AbstractClientPlayerEntityAccessor {

    void talkBubbles$setChatText(List<String> text, int currentAge, int width, int height);

    @Nullable
    List<String> talkBubbles$getChatText();

    int talkBubbles$getOldAge();

    int talkBubbles$getWidth();

    int talkBubbles$getHeight();
}
