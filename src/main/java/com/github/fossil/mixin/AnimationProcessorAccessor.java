package com.github.fossil.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Map;

@Mixin(AnimationProcessor.class)
public interface AnimationProcessorAccessor<T extends GeoAnimatable> {

    @Accessor("bones")
    Map<String, GeoBone> getBonesByName();
}
