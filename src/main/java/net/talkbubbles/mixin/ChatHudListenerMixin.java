package net.talkbubbles.mixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.talkbubbles.TalkBubbles;
import net.talkbubbles.accessor.OtherClientPlayerEntityAccessor;

@Environment(EnvType.CLIENT)
@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    private void onChatMessageMixin(MessageType type, Text message, @Nullable MessageSender sender, CallbackInfo info) {
        if (sender != null && type.chat().isPresent()) {
            List<OtherClientPlayerEntity> list = client.world.getEntitiesByClass(OtherClientPlayerEntity.class, client.player.getBoundingBox().expand(TalkBubbles.CONFIG.chatRange),
                    EntityPredicates.EXCEPT_SPECTATOR);
            for (int i = 0; i < list.size(); i++)
                if (list.get(i).getUuid().equals(sender.uuid())) {
                    String stringMessage = message.getString();
                    stringMessage = stringMessage.replace("<" + StringUtils.substringBetween(stringMessage, "<", ">") + "> ", "");
                    String[] string = stringMessage.split(" ");
                    List<String> stringList = new ArrayList<>();
                    String stringCollector = "";

                    int width = 0;
                    int height = 0;
                    for (int u = 0; u < string.length; u++) {
                        if (client.textRenderer.getWidth(stringCollector) < TalkBubbles.CONFIG.maxChatWidth
                                && client.textRenderer.getWidth(stringCollector) + client.textRenderer.getWidth(string[u]) <= TalkBubbles.CONFIG.maxChatWidth) {
                            stringCollector = stringCollector + " " + string[u];
                            if (u == string.length - 1) {
                                stringList.add(stringCollector);
                                height++;
                                if (width < client.textRenderer.getWidth(stringCollector))
                                    width = client.textRenderer.getWidth(stringCollector);
                            }
                        } else {
                            stringList.add(stringCollector);

                            height++;
                            if (width < client.textRenderer.getWidth(stringCollector))
                                width = client.textRenderer.getWidth(stringCollector);

                            stringCollector = string[u];

                            if (u == string.length - 1) {
                                stringList.add(stringCollector);
                                height++;
                                if (width < client.textRenderer.getWidth(stringCollector))
                                    width = client.textRenderer.getWidth(stringCollector);
                            }
                        }
                    }

                    if (width % 2 != 0)
                        width++;

                    ((OtherClientPlayerEntityAccessor) list.get(i)).setChatText(stringList, list.get(i).age, width, height);
                    break;
                }
        }
    }
}
