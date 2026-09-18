package net.talkbubbles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public interface DrawContextAccessor {

    @Invoker("<init>")
    static GuiGraphics getDrawContext(Minecraft client, PoseStack matrices, MultiBufferSource.BufferSource vertexConsumers) {
        throw new AssertionError();
    }

}
