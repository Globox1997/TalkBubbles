package net.talkbubbles.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Shadow
    @Final
    @Mutable
    private Minecraft minecraft;

    @Inject(method = "addMessage", at = @At("HEAD"))
    private void addMessageMixin(Component contents, @Nullable MessageSignature signature, GuiMessageSource source, @Nullable GuiMessageTag tag, CallbackInfo info) {
        if (minecraft != null && minecraft.player != null && minecraft.level != null) {
            String detectedSenderName = extractSender(contents);
            if (!detectedSenderName.isEmpty()) {
                UUID senderUUID = this.minecraft.getPlayerSocialManager().getDiscoveredUUID(detectedSenderName);

                List<AbstractClientPlayer> list = minecraft.level.getEntitiesOfClass(AbstractClientPlayer.class, minecraft.player.getBoundingBox().inflate(TalkBubbles.CONFIG.chatRange),
                        EntitySelector.NO_SPECTATORS);

                if (!TalkBubbles.CONFIG.showOwnBubble) {
                    list.remove(minecraft.player);
                }
                for (AbstractClientPlayer abstractClientPlayer : list) {
                    if (abstractClientPlayer.getUUID().equals(senderUUID)) {
                        String stringMessage = contents.getString();
                        stringMessage = stringMessage.replaceFirst("[\\s\\S]*" + detectedSenderName + "([^\\p{L}§]|(§.)?)+\\s+", "");
                        String[] string = stringMessage.split(" ");
                        List<String> stringList = new ArrayList<>();
                        String stringCollector = "";

                        int width = 0;
                        int height = 0;
                        for (int u = 0; u < string.length; u++) {
                            if (minecraft.font.width(stringCollector) < TalkBubbles.CONFIG.maxChatWidth
                                    && minecraft.font.width(stringCollector) + minecraft.font.width(string[u]) <= TalkBubbles.CONFIG.maxChatWidth) {
                                stringCollector = stringCollector + " " + string[u];
                                if (u == string.length - 1) {
                                    stringList.add(stringCollector);
                                    height++;
                                    if (width < minecraft.font.width(stringCollector)) {
                                        width = minecraft.font.width(stringCollector);
                                    }
                                }
                            } else {
                                stringList.add(stringCollector);

                                height++;
                                if (width < minecraft.font.width(stringCollector)) {
                                    width = minecraft.font.width(stringCollector);
                                }

                                stringCollector = string[u];

                                if (u == string.length - 1) {
                                    stringList.add(stringCollector);
                                    height++;
                                    if (width < minecraft.font.width(stringCollector)) {
                                        width = minecraft.font.width(stringCollector);
                                    }
                                }
                            }
                        }

                        if (width % 2 != 0) {
                            width++;
                        }
                        ((AbstractClientPlayerEntityAccessor) abstractClientPlayer).talkBubbles$setChatText(stringList, abstractClientPlayer.tickCount, width, height);
                        break;
                    }
                }
            }
        }

    }

    @Unique
    private String extractSender(Component text) {
        String[] words = text.getString().split("(§.)|[^\\w§]+");
        String[] parts = text.toString().split("key='");

        if (parts.length > 1) {
            String translationKey = parts[1].split("'")[0];
            if (translationKey.contains("commands")) {
                return "";
            } else if (translationKey.contains("advancement")) {
                return "";
            }
        }

        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) {
                continue;
            }
            if (TalkBubbles.CONFIG.maxUUIDWordCheck != 0 && i >= TalkBubbles.CONFIG.maxUUIDWordCheck) {
                return "";
            }

            UUID possibleUUID = this.minecraft.getPlayerSocialManager().getDiscoveredUUID(words[i]);
            if (possibleUUID != Util.NIL_UUID) {
                return words[i];
            }
        }

        return "";
    }
}
