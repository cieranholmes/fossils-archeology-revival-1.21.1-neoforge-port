package com.github.fossil.client.renderer.entity;

import com.github.fossil.client.model.ToyTetheredLogModel;
import com.github.fossil.entity.ToyTetheredLog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

public class ToyTetheredLogRenderer extends ToyBaseRenderer<ToyTetheredLog> {


    public ToyTetheredLogRenderer(EntityRendererProvider.Context context) {
        super(context, new ToyTetheredLogModel(), 0.4f);
    }

    @Override
    protected void setupRotations(PoseStack poseStack, float rotationYaw) {

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ToyTetheredLog entity) {
        if (!ToyTetheredLogModel.TEXTURES.containsKey(entity.getWoodTypeName())) {
            return ToyTetheredLogModel.TEXTURES.get(WoodType.OAK.name());
        }
        return ToyTetheredLogModel.TEXTURES.get(entity.getWoodTypeName());
    }
}
