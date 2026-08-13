package com.github.fossils.client.renderer.entity;

import com.github.fossils.client.model.ToyScratchingPostModel;
import com.github.fossils.entity.ToyScratchingPost;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;

public class ToyScratchingPostRenderer extends ToyBaseRenderer<ToyScratchingPost> {

    public ToyScratchingPostRenderer(EntityRendererProvider.Context context) {
        super(context, new ToyScratchingPostModel(), 0.4f);
    }

    @Override
    protected void setupRotations(PoseStack poseStack, float rotationYaw) {

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ToyScratchingPost entity) {
        if (!ToyScratchingPostModel.TEXTURES.containsKey(entity.getWoodTypeName())) {
            return ToyScratchingPostModel.TEXTURES.get(WoodType.OAK.name());
        }
        return ToyScratchingPostModel.TEXTURES.get(entity.getWoodTypeName());
    }
}
