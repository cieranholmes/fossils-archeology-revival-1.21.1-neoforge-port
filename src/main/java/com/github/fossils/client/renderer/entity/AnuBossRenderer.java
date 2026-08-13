package com.github.fossils.client.renderer.entity;

import com.github.fossils.client.model.AnuBossModel;
import com.github.fossils.client.renderer.entity.layers.AnuBossGlowLayer;
import com.github.fossils.entity.monster.AnuBoss;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AnuBossRenderer extends MobRenderer<AnuBoss, AnuBossModel> {
    public AnuBossRenderer(EntityRendererProvider.Context context) {
        super(context, new AnuBossModel(), 0.5f);
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        addLayer(new AnuBossGlowLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AnuBoss entity) {
        return AnuBossModel.TEXTURE;
    }

}
