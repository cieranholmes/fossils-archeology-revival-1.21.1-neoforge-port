package com.github.fossil.client.renderer.entity;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.client.model.SentryPiglinModel;
import com.github.fossil.entity.monster.SentryPiglin;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SentryPiglinRenderer extends HumanoidMobRenderer<SentryPiglin, SentryPiglinModel> {
    public static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/entity/sentry_piglin.png");

    public SentryPiglinRenderer(EntityRendererProvider.Context context) {
        super(context, new SentryPiglinModel(), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PIGLIN_BRUTE_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SentryPiglin entity) {
        return FossilsAndArcheologyRevival.location("textures/entity/sentry_piglin.png");
    }
}
