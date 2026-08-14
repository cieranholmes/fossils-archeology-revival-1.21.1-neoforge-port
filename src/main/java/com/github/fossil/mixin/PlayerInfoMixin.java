package com.github.fossil.mixin;

import com.github.fossil.client.FAPlayerCapes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Shadow
    @Final
    private GameProfile profile;

    @ModifyReturnValue(method = "getSkin", at = @At("RETURN"))
    private PlayerSkin fossil$overrideDevAndContributorCapes(PlayerSkin original) {
        if (FAPlayerCapes.DEV_UUIDS.contains(profile.getId())) {
            return new PlayerSkin(original.texture(), original.textureUrl(), FAPlayerCapes.DEVELOPER_CAPE_TEXTURE,
                    FAPlayerCapes.DEVELOPER_ELYTRA_TEXTURE, original.model(), original.secure());
        } else if (FAPlayerCapes.CONTRIBUTOR_UUIDS.contains(profile.getId())) {
            return new PlayerSkin(original.texture(), original.textureUrl(), FAPlayerCapes.CONTRIBUTOR_CAPE_TEXTURE,
                    FAPlayerCapes.CONTRIBUTOR_ELYTRA_TEXTURE, original.model(), original.secure());
        }
        return original;
    }
}
