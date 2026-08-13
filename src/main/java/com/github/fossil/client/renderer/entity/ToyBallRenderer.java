package com.github.fossil.client.renderer.entity;

import com.github.fossil.client.model.ToyBallModel;
import com.github.fossil.entity.ToyBall;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ToyBallRenderer extends ToyBaseRenderer<ToyBall> {

    public ToyBallRenderer(EntityRendererProvider.Context context) {
        super(context, new ToyBallModel(), 0.3f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ToyBall entity) {
        return ToyBallModel.TEXTURE;
    }
}
