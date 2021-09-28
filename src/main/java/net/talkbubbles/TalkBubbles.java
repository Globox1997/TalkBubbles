package net.talkbubbles;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.talkbubbles.config.TalkBubblesConfig;

public class TalkBubbles implements ClientModInitializer {

    public static TalkBubblesConfig CONFIG = new TalkBubblesConfig();

    @Override
    public void onInitializeClient() {
        AutoConfig.register(TalkBubblesConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TalkBubblesConfig.class).getConfig();
    }

}
