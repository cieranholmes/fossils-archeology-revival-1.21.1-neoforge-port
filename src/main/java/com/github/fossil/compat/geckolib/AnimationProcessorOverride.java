package com.github.fossil.compat.geckolib;

import com.github.fossil.mixin.AnimatableManagerAccessor;
import com.github.fossil.mixin.AnimationControllerAccessor;
import com.github.fossil.mixin.AnimationProcessorAccessor;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

/**
 * Replaces {@link AnimationProcessor#tickAnimation} for models that opt into
 * {@link com.github.fossil.client.model.AdditiveAnimationModel}, so that bone transforms from multiple
 * simultaneously-running {@link AnimationController}s (e.g. a {@code Prehistoric}'s idle + attack controllers)
 * are combined additively instead of the last-processed controller simply overwriting earlier ones.
 */
public class AnimationProcessorOverride {

    public static <T extends GeoAnimatable> void tickAdditiveAnimations(AnimationProcessor<T> processor, T animatable, GeoModel<T> model,
                                                                         AnimatableManager<T> animatableManager, double animTime, AnimationState<T> state,
                                                                         boolean crashWhenCantFindBone) {
        Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(processor, animatableManager.getBoneSnapshotCollection());

        for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
            if (processor.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            ((AnimationControllerAccessor<T>) controller).setIsJustStarting(animatableManager.isFirstTick());

            state.withController(controller);
            controller.process(model, state, ((AnimationProcessorAccessor<T>) processor).getBonesByName(), boneSnapshots, animTime, crashWhenCantFindBone);
        }

        for (GeoBone bone : processor.getRegisteredBones()) {
            boolean firstRot = true;
            boolean firstPos = true;
            boolean firstScale = true;
            BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
            BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

            for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
                if (controller.getBoneAnimationQueues().isEmpty()) {
                    continue;
                }

                BoneAnimationQueue boneAnimation = controller.getBoneAnimationQueues().get(bone.getName());

                AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
                AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
                AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();

                AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
                AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
                AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();

                AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
                AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
                AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();
                EasingType easingType = ((AnimationControllerAccessor<T>) controller).getOverrideEasingTypeFunction().apply(animatable);

                if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
                    bone.setRotX((float) EasingType.lerpWithOverride(rotXPoint, easingType)
                            + (firstRot ? initialSnapshot.getRotX() : bone.getRotX()));
                    bone.setRotY((float) EasingType.lerpWithOverride(rotYPoint, easingType)
                            + (firstRot ? initialSnapshot.getRotY() : bone.getRotY()));
                    bone.setRotZ((float) EasingType.lerpWithOverride(rotZPoint, easingType)
                            + (firstRot ? initialSnapshot.getRotZ() : bone.getRotZ()));
                    snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    snapshot.startRotAnim();
                    bone.markRotationAsChanged();
                    firstRot = false;
                }

                if (posXPoint != null && posYPoint != null && posZPoint != null) {
                    bone.setPosX((float) EasingType.lerpWithOverride(posXPoint, easingType) + (firstPos ? 0 : bone.getPosX()));
                    bone.setPosY((float) EasingType.lerpWithOverride(posYPoint, easingType) + (firstPos ? 0 : bone.getPosY()));
                    bone.setPosZ((float) EasingType.lerpWithOverride(posZPoint, easingType) + (firstPos ? 0 : bone.getPosZ()));
                    snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                    snapshot.startPosAnim();
                    bone.markPositionAsChanged();
                    firstPos = false;
                }

                if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
                    bone.setScaleX((float) EasingType.lerpWithOverride(scaleXPoint, easingType) * (firstScale ? 1 : bone.getScaleX()));
                    bone.setScaleY((float) EasingType.lerpWithOverride(scaleYPoint, easingType) * (firstScale ? 1 : bone.getScaleY()));
                    bone.setScaleZ((float) EasingType.lerpWithOverride(scaleZPoint, easingType) * (firstScale ? 1 : bone.getScaleZ()));
                    snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    snapshot.startScaleAnim();
                    bone.markScaleAsChanged();
                    firstScale = false;
                }
            }
        }

        processor.reloadAnimations = false;
        double resetTickLength = animatable.getBoneResetTime();

        for (GeoBone bone : processor.getRegisteredBones()) {
            if (!bone.hasRotationChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isRotAnimInProgress()) {
                    saveSnapshot.stopRotAnim(animTime);
                }

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((animTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1);
                float initialRotX = initialSnapshot.getRotX();
                float initialRotY = initialSnapshot.getRotY();
                float initialRotZ = initialSnapshot.getRotZ();
                float lastXRot = saveSnapshot.getRotX();
                float lastYRot = saveSnapshot.getRotY();
                float lastZRot = saveSnapshot.getRotZ();

                if (percentageReset == 0) {
                    if (lastXRot != initialRotX && isSuspectedCompletedRotation(lastXRot)) {
                        lastXRot = initialRotX;
                        percentageReset = 1;
                    }

                    if (lastYRot != initialRotY && isSuspectedCompletedRotation(lastYRot)) {
                        lastYRot = initialRotY;
                        percentageReset = 1;
                    }

                    if (lastZRot != initialRotZ && isSuspectedCompletedRotation(lastZRot)) {
                        lastZRot = initialRotZ;
                        percentageReset = 1;
                    }
                }

                bone.setRotX((float) Mth.lerp(percentageReset, lastXRot, initialRotX));
                bone.setRotY((float) Mth.lerp(percentageReset, lastYRot, initialRotY));
                bone.setRotZ((float) Mth.lerp(percentageReset, lastZRot, initialRotZ));

                if (percentageReset >= 1) {
                    saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                }
            }

            if (!bone.hasPositionChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isPosAnimInProgress()) {
                    saveSnapshot.stopPosAnim(animTime);
                }

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

                bone.setPosX((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX()));
                bone.setPosY((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY()));
                bone.setPosZ((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ()));

                if (percentageReset >= 1) {
                    saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            }

            if (!bone.hasScaleChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isScaleAnimInProgress()) {
                    saveSnapshot.stopScaleAnim(animTime);
                }

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((animTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength, 1);

                bone.setScaleX((float) Mth.lerp(percentageReset, saveSnapshot.getScaleX(), initialSnapshot.getScaleX()));
                bone.setScaleY((float) Mth.lerp(percentageReset, saveSnapshot.getScaleY(), initialSnapshot.getScaleY()));
                bone.setScaleZ((float) Mth.lerp(percentageReset, saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ()));

                if (percentageReset >= 1) {
                    saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                }
            }
        }

        for (GeoBone bone : processor.getRegisteredBones()) {
            bone.resetStateChanges();
        }

        ((AnimatableManagerAccessor) animatableManager).fossil$finishFirstTick();
    }

    /**
     * Bandaid helper to try to detect suspected completed rotations in an animation frame, mirroring
     * {@link AnimationProcessor}'s own private equivalent.
     */
    private static boolean isSuspectedCompletedRotation(float lastRotation) {
        float rotations = Mth.abs(lastRotation / (360f * Mth.DEG_TO_RAD));
        float partialRotation = 1 - (rotations - (int) rotations);

        return partialRotation == 1 || partialRotation < 0.026 * rotations;
    }

    private static <T extends GeoAnimatable> Map<String, BoneSnapshot> updateBoneSnapshots(AnimationProcessor<T> processor, Map<String, BoneSnapshot> snapshots) {
        for (GeoBone bone : processor.getRegisteredBones()) {
            if (!snapshots.containsKey(bone.getName())) {
                snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
            }
        }
        return snapshots;
    }
}
