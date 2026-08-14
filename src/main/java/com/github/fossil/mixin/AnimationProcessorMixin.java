package com.github.fossil.mixin;

import com.github.fossil.client.model.AdditiveAnimationModel;
import com.github.fossil.compat.geckolib.AnimationProcessorOverride;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@Mixin(AnimationProcessor.class)
public class AnimationProcessorMixin<T extends GeoAnimatable> {

    @Inject(method = "tickAnimation", remap = false, at = @At("HEAD"), cancellable = true)
    private void tickAdditiveAnimations(T animatable, GeoModel<T> model, AnimatableManager<T> animatableManager, double animTime,
                                         AnimationState<T> state, boolean crashWhenCantFindBone, CallbackInfo ci) {
        if (model instanceof AdditiveAnimationModel) {
            //noinspection unchecked
            AnimationProcessorOverride.tickAdditiveAnimations((AnimationProcessor<T>) (Object) this, animatable, model, animatableManager, animTime, state, crashWhenCantFindBone);
            ci.cancel();
        }
    }
}
