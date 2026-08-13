package com.github.fossils.client.renderer.entity;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.model.QuaggaModel;
import com.github.fossils.entity.Quagga;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class QuaggaRenderer extends AbstractHorseRenderer<Quagga, HorseModel<Quagga>> {
    private static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/entity/quagga/quagga_saddled.png");

    public QuaggaRenderer(EntityRendererProvider.Context context) {
        super(context, new QuaggaModel<>(), 1.1f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Quagga entity) {
        return TEXTURE;
    }
}
