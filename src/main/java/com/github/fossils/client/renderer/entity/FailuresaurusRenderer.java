package com.github.fossils.client.renderer.entity;

import com.github.fossils.client.model.FailuresaurusModel;
import com.github.fossils.entity.monster.Failuresaurus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FailuresaurusRenderer extends GeoEntityRenderer<Failuresaurus> {
    public FailuresaurusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FailuresaurusModel());
    }
}
