package net.talkbubbles.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "talkbubbles")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TalkBubblesConfig implements ConfigData {

    public float test = 0.0F;
    // public float test2 = 128.0F;
    // public float test3 = 128.0F;
    // public float test4 = 128.0F;
    public float test5 = -3.0F;
    public int testi1 = 0;
    public int testi2 = 0;
    public int testi3 = 0;
    public int testi4 = 0;
    public int testi5 = 0;

    public int chatColor = 1842204;
    public float backgroundOpacity = 0.8F;
    @Comment("Time in ticks")
    public int chatTime = 200;
    public int maxChatWidth = 180;

}