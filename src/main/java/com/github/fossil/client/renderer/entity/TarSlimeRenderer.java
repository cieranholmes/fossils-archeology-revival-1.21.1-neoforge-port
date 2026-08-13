package com.github.fossil.client.renderer.entity;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import org.jetbrains.annotations.NotNull;

public class TarSlimeRenderer extends SlimeRenderer {
    private static final ResourceLocation TAR_SLIME_LOCATION = FossilsAndArcheologyRevival.location("textures/entity/tar_slime.png");

    public TarSlimeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Slime entity) {
        return TAR_SLIME_LOCATION;
    }
}
