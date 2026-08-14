package com.github.fossil.mixin;

import com.github.fossil.Config;
import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static final ResourceLocation LAYER_TEXTURE_BACK = FossilsAndArcheologyRevival.location("textures/gui/parallax/layer_0.png");
    @Unique
    private static final ResourceLocation LAYER_TEXTURE_FRONT = FossilsAndArcheologyRevival.location("textures/gui/parallax/layer_1.png");
    @Unique
    private final int fossil$initialOffsetFront = new Random().nextInt(1027);
    @Unique
    private final int fossil$initialOffsetBack = new Random().nextInt(2047);
    @Unique
    private int fossil$layerTick;

    private TitleScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    protected void renderCustomTitleScreen(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (Config.customMainMenu) {
            fossil$layerTick++;
            float u = fossil$initialOffsetBack + ((fossil$layerTick + partialTick) / 2f) + 1;
            guiGraphics.blit(LAYER_TEXTURE_BACK, 0, 0, u / (960f / width), 0, width, height, (int) (1024 * (height / 128f)), height);

            u = fossil$initialOffsetFront + fossil$layerTick + partialTick + 2 + 512;
            guiGraphics.blit(LAYER_TEXTURE_FRONT, 0, 0, u / (960f / width), 0, width, height, (int) (2048 * (height / 128f)), height);
            ci.cancel();
        }
    }
}
