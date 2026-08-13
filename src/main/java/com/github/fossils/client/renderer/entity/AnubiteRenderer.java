package com.github.fossils.client.renderer.entity;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.model.AnubiteModel;
import com.github.fossils.entity.monster.Anubite;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AnubiteRenderer extends MobRenderer<Anubite, AnubiteModel> {
    public static final ResourceLocation TEXTURE = FossilsAndArcheologyRevival.location("textures/entity/anubite.png");

    public AnubiteRenderer(EntityRendererProvider.Context context) {
        super(context, new AnubiteModel(), 0.3f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Anubite entity) {
        return FossilsAndArcheologyRevival.location("textures/entity/anubite.png");
    }
}
