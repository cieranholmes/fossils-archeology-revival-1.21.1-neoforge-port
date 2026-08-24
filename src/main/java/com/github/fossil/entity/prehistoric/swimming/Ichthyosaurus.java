package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.animation.PausableAnimationController;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimmingBucketable;
import com.github.fossil.entity.util.Util;
import com.github.fossil.item.ModItems;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

import java.util.EnumSet;
import java.util.Optional;

import static software.bernie.geckolib.animation.Animation.LoopType.DEFAULT;
import static software.bernie.geckolib.animation.Animation.LoopType.LOOP;

public class Ichthyosaurus extends PrehistoricSwimmingBucketable {
    private static final String JUMP_START = "animation.ichthyosaurus.jump_start";
    private static final String JUMP_HOLD = "animation.ichthyosaurus.jump_hold";
    private static final String JUMP_END = "animation.ichthyosaurus.jump_end";
    private static final EntityDataAccessor<Boolean> JUMPING = SynchedEntityData.defineId(Ichthyosaurus.class, EntityDataSerializers.BOOLEAN);
    private final IchthyosaurusAnimationLogic animationLogic = new IchthyosaurusAnimationLogic(this);

    public Ichthyosaurus(EntityType<Ichthyosaurus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.ichthyosaurus.speak", "animation.ichthyosaurus.emote_1", "animation.ichthyosaurus.emote_2"));
        goalSelector.addGoal(Util.WANDER - 1, new JumpGoal());
        targetSelector.addGoal(1, new DinoOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new DinoOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(JUMPING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Jumping", isJumping());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setJumping(compound.getBoolean("Jumping"));
    }

    public boolean isJumping() {
        return entityData.get(JUMPING);
    }

    public void setJumping(boolean jumping) {
        entityData.set(JUMPING, jumping);
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.ICHTHYOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.MAGIC_CONCH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ModSounds.ICHTHYOSAURUS_AMBIENT.get() : ModSounds.ICHTHYOSAURUS_AMBIENT_OUTSIDE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ICHTHYOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ICHTHYOSAURUS_DEATH.get();
    }

    @Override
    public AnimationLogic<Prehistoric> getAnimationLogic() {
        return animationLogic;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        var controller = new PausableAnimationController<>(this, AnimationLogic.IDLE_CTRL, 5, animationLogic::jumpPredicate);
        registerEatingListeners(controller);
        registerControllerWithTriggers(controllerRegistrar, controller);

        var attackController = new PausableAnimationController<>(this, AnimationLogic.ATTACK_CTRL, 5, animationLogic::attackPredicate);
        registerControllerWithTriggers(controllerRegistrar, attackController);
    }

    /**
     * Occasionally breaches the surface with the previously-unused jump_start/jump_hold/jump_end animation
     * sequence. Actively swims upward to the surface during jump_start so the breach lines up with the animation,
     * holds near the peak during jump_hold, then lets normal gravity/water drag pull it back down during jump_end -
     * no fall physics of our own are needed for that last part.
     */
    class JumpGoal extends Goal {
        /**
         * Matches the 3-second jump_start clip so the physical rise finishes exactly as it transitions into the hold
         */
        private static final int RISE_TICKS = 60;
        /**
         * How long to hold near the peak before falling back (roughly one jump_hold loop)
         */
        private static final int HOLD_TICKS = 20;
        private static final double FORWARD_SPEED = 0.3;
        private int cooldown;
        private int elapsed;
        private double targetY;
        private double forwardX;
        private double forwardZ;

        JumpGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (cooldown > tickCount || !isInWater() || isImmobile() || isVehicle()) {
                return false;
            }
            return !Double.isNaN(findSurfaceY()) && random.nextInt(300) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return isInWater() && !isImmobile() && !isVehicle() && elapsed < RISE_TICKS + HOLD_TICKS;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        /**
         * The Y of the first open-air block reached by going straight up without passing through anything but
         * water first - i.e. there's clear water above to breach through, not a cave ceiling. {@link Double#NaN} if
         * no such block is found within reach.
         */
        private double findSurfaceY() {
            BlockPos.MutableBlockPos pos = blockPosition().mutable();
            int range = Mth.ceil(getBbHeight()) + 3;
            for (int i = 0; i <= range; i++) {
                if (level().getBlockState(pos).isAir()) {
                    return pos.getY();
                }
                if (!level().getFluidState(pos).is(FluidTags.WATER)) {
                    return Double.NaN;
                }
                pos.move(Direction.UP);
            }
            return Double.NaN;
        }

        @Override
        public void start() {
            cooldown = 0;
            elapsed = 0;
            double surfaceY = findSurfaceY();
            targetY = Double.isNaN(surfaceY) ? getY() : surfaceY + getBbHeight() + 10;
            float yawRad = (float) Math.toRadians(getYRot());
            forwardX = -Mth.sin(yawRad) * FORWARD_SPEED;
            forwardZ = Mth.cos(yawRad) * FORWARD_SPEED;
            setJumping(true);
        }

        @Override
        public void tick() {
            elapsed++;
            if (elapsed <= RISE_TICKS) {
                double remaining = targetY - getY();
                double ticksLeft = RISE_TICKS - elapsed + 1;
                setDeltaMovement(forwardX, Mth.clamp(remaining / ticksLeft, -0.3, 0.3), forwardZ);
            } else {
                setDeltaMovement(forwardX, 0, forwardZ);
            }
        }

        @Override
        public void stop() {
            cooldown = tickCount + (random.nextInt(300) + 40) * 20;
            setJumping(false);
            animationLogic.triggerAnimation(AnimationLogic.IDLE_CTRL, getAnimation(JUMP_END), AnimationCategory.IDLE);
        }
    }

    static class IchthyosaurusAnimationLogic extends AnimationLogic<Prehistoric> {

        public IchthyosaurusAnimationLogic(Ichthyosaurus entity) {
            super(entity);
        }

        public PlayState jumpPredicate(AnimationState<Ichthyosaurus> state) {
            if (isBlocked()) return PlayState.STOP;
            AnimationController<Ichthyosaurus> controller = state.getController();
            if (tryNextAnimation(state, controller)) {
                return PlayState.CONTINUE;
            }
            Optional<ActiveAnimationInfo> activeAnimation = getActiveAnimation(controller.getName());
            if (activeAnimation.isPresent() && tryForcedAnimation(state, activeAnimation.get())) {
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().isJumping()) {
                controller.setAnimation(RawAnimation.begin().thenPlay(JUMP_START).thenLoop(JUMP_HOLD));
                return PlayState.CONTINUE;
            }
            double animationSpeed = 1;
            if (state.getAnimatable().isBeached()) {
                addActiveAnimation(controller.getName(), AnimationCategory.BEACHED);
            } else if (entity.isSleeping()) {
                addActiveAnimation(controller.getName(), AnimationCategory.SLEEP);
            } else if (state.isMoving()) {
                if (entity.isInWater()) {
                    if (entity.isSprinting()) {
                        addActiveAnimation(controller.getName(), AnimationCategory.SWIM_FAST);
                    } else {
                        addActiveAnimation(controller.getName(), AnimationCategory.SWIM);
                    }
                } else {
                    animationSpeed = addMovementAnimation(state, true);
                }
            } else if (state.getAnimatable().isWeak()) {
                addActiveAnimation(controller.getName(), AnimationCategory.KNOCKOUT);
            } else {
                addActiveAnimation(controller.getName(), AnimationCategory.IDLE);
            }
            setAnimationSpeed(controller, animationSpeed, state.getAnimationTick());
            Optional<ActiveAnimationInfo> newAnimation = getActiveAnimation(controller.getName());
            if (newAnimation.isPresent()) {
                state.setAnimation(RawAnimation.begin().then(newAnimation.get().animationName(), newAnimation.get().loop() ? LOOP : DEFAULT));
            }
            return PlayState.CONTINUE;
        }
    }
}
