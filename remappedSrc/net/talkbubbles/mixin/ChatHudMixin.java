package net.talkbubbles.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.world.entity.EntitySelector;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.AbstractClientPlayerEntityAccessor;

@Environment(EnvType.CLIENT)
@Mixin(ChatComponent.class)
public class ChatHudMixin {

    @Shadow
    @Final
    @Mutable
    private Minecraft minecraft;

    // onChatMessage is now done in MessageHandler.class
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"))
    private void addMessageMixin(Component message, @Nullable MessageSignature signature, @Nullable GuiMessageTag indicator, CallbackInfo info) {
        if (minecraft != null && minecraft.player != null) {
            String detectedSenderName = extractSender(message);
            if (!detectedSenderName.isEmpty()) {
                UUID senderUUID = this.minecraft.getPlayerSocialManager().getDiscoveredUUID(detectedSenderName);

                List<AbstractClientPlayer> list = minecraft.level.getEntitiesOfClass(AbstractClientPlayer.class, minecraft.player.getBoundingBox().inflate(TalkBubbles.CONFIG.chatRange),
                        EntitySelector.NO_SPECTATORS);

                if (!TalkBubbles.CONFIG.showOwnBubble) {
                    list.remove(minecraft.player);
                }
                for (int i = 0; i < list.size(); i++)
                    if (list.get(i).getUUID().equals(senderUUID)) {
                        String stringMessage = message.getString();
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
                        ((AbstractClientPlayerEntityAccessor) list.get(i)).setChatText(stringList, list.get(i).tickCount, width, height);
                        break;
                    }
            }
        }

    }

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
