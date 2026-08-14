package com.github.fossil.mixin;

import com.github.fossil.compat.geckolib.AnimationControllerOverride;
import com.github.fossil.entity.prehistoric.base.PrehistoricAnimatable;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

@Mixin(AnimationController.class)
public class AnimationControllerMixin<T extends GeoAnimatable> {

    @Shadow
    @Final
    protected Map<String, BoneAnimationQueue> boneAnimationQueues;

    @Shadow
    @Final
    protected Map<String, BoneSnapshot> boneSnapshots;

    @Shadow
    protected boolean isJustStarting;

    @Shadow
    protected double transitionLength;

    @Shadow
    protected AnimationProcessor.QueuedAnimation currentAnimation;

    @Inject(method = "process", remap = false, at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/loading/math/MathParser;setVariable(Ljava/lang/String;Ljava/util/function/DoubleSupplier;)V"))
    public void tickAdditiveAnimations(GeoModel<T> model, AnimationState<T> state, Map<String, GeoBone> bones,
                                       Map<String, BoneSnapshot> snapshots, double seekTime, boolean crashWhenCantFindBone, CallbackInfo ci,
                                       @Local(name = "adjustedTick") double adjustedTick) {
        if (state.getAnimatable() instanceof PrehistoricAnimatable<?>) {
            AnimationControllerOverride.fixTransitions(bones, currentAnimation, boneSnapshots, adjustedTick, adjustedTick == 0 || isJustStarting, snapshots, transitionLength, boneAnimationQueues);
        }
    }
}
