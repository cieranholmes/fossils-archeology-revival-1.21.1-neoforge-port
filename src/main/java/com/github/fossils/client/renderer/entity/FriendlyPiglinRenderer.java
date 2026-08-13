package com.github.fossils.client.renderer.entity;

import com.github.fossils.client.model.FriendlyPiglinModel;
import com.github.fossils.entity.monster.FriendlyPiglin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FriendlyPiglinRenderer extends HumanoidMobRenderer<FriendlyPiglin, FriendlyPiglinModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

    public FriendlyPiglinRenderer(EntityRendererProvider.Context context) {
        super(context, new FriendlyPiglinModel(), 0.5f);
    }

    @Override
    public @NotNull Vec3 getRenderOffset(FriendlyPiglin entity, float partialTicks) {
        if (entity.isInSittingPose()) {
            return new Vec3(0.0, entity.isBaby() ? -0.2 : -0.4, 0.0);
        }
        return super.getRenderOffset(entity, partialTicks);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(FriendlyPiglin entity) {
        return TEXTURE;
    }
}
