package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.DrinkGoal;
import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.animation.PausableAnimationController;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.util.Util;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

import java.util.EnumSet;
import java.util.Optional;

import static software.bernie.geckolib.animation.Animation.LoopType.LOOP;
import static software.bernie.geckolib.animation.Animation.LoopType.PLAY_ONCE;

public class Dilophosaurus extends Prehistoric {
    public static final String ANIMATIONS = "dilophosaurus.animation.json";
    public static final String ATTACK1 = "animation.dilophosaurus.attack1";
    public static final String ATTACK2 = "animation.dilophosaurus.attack2";
    public static final String ATTACK3 = "animation.dilophosaurus.attack3";
    public static final String CALL = "animation.dilophosaurus.call";
    public static final String DRINK = "animation.dilophosaurus.drink";
    public static final String EAT = "animation.dilophosaurus.eat";
    public static final String FALL = "animation.dilophosaurus.jump/fall";
    public static final String IDLE = "animation.dilophosaurus.idle";
    public static final String INFLATE_START = "animation.dilophosaurus.inflate_start";
    public static final String INFLATE_HOLD = "animation.dilophosaurus.inflate_hold";
    public static final String INFLATE_END = "animation.dilophosaurus.inflate_end";
    public static final String RUN = "animation.dilophosaurus.run";
    public static final String SIT1 = "animation.dilophosaurus.sit1";
    public static final String SIT2 = "animation.dilophosaurus.sit2";
    public static final String SLEEP1 = "animation.dilophosaurus.sleep1";
    public static final String SLEEP2 = "animation.dilophosaurus.sleep2";
    public static final String SPEAK = "animation.dilophosaurus.speak";
    public static final String SWIM = "animation.dilophosaurus.swim";
    public static final String WALK = "animation.dilophosaurus.walk";

    private static final EntityDataAccessor<Boolean> DISPLAYING = SynchedEntityData.defineId(Dilophosaurus.class, EntityDataSerializers.BOOLEAN);
    private final DiloAnimationLogic animationLogic = new DiloAnimationLogic(this);

    public Dilophosaurus(EntityType<Dilophosaurus> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, DRINK));
        goalSelector.addGoal(Util.WANDER - 1, new DisplayGoal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DISPLAYING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (!level().isClientSide) {
            if (SLEEPING.equals(key) || SITTING.equals(key)) {
                //Fallback
                setDisplaying(false);
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Displaying", isDisplaying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setDisplaying(compound.getBoolean("Displaying"));
    }

    public boolean isDisplaying() {
        return entityData.get(DISPLAYING);
    }

    public void setDisplaying(boolean displaying) {
        entityData.set(DISPLAYING, displaying);
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.DILOPHOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0, -0.25, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DILOPHOSAURUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DILOPHOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DILOPHOSAURUS_DEATH.get();
    }

    @Override
    public AnimationLogic<Prehistoric> getAnimationLogic() {
        return animationLogic;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        var controller = new PausableAnimationController<>(this, AnimationLogic.IDLE_CTRL, 5, animationLogic::diloPredicate);
        registerEatingListeners(controller);
        registerControllerWithTriggers(controllerRegistrar, controller);

        var attackController = new PausableAnimationController<>(this, AnimationLogic.ATTACK_CTRL, 5, animationLogic::attackPredicate);
        registerControllerWithTriggers(controllerRegistrar, attackController);
    }

    /**
     * Randomly triggers the frill/throat-sac threat display while idle, independent of the eat/sleep/sit/attack
     * states. Mirrors {@code Parasaurolophus.ParaStandGoal}.
     */
    class DisplayGoal extends Goal {
        private int cooldown;

        DisplayGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (cooldown > tickCount || isInWater() || isImmobile()) {
                return false;
            }
            return random.nextInt(200) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !isVehicle() && !isInWater() && !isImmobile() && random.nextInt(reducedTickDelay(100)) != 0;
        }

        @Override
        public void start() {
            cooldown = 0;
            setDisplaying(true);
            playSound(ModSounds.DILOPHOSAURUS_CALL.get(), getSoundVolume(), getVoicePitch());
        }

        @Override
        public void stop() {
            cooldown = tickCount + (random.nextInt(200) + 20) * 20;
            setDisplaying(false);
            animationLogic.triggerAnimation(AnimationLogic.IDLE_CTRL, getAnimation(INFLATE_END), AnimationCategory.IDLE);
        }
    }

    static class DiloAnimationLogic extends AnimationLogic<Prehistoric> {

        public DiloAnimationLogic(Dilophosaurus entity) {
            super(entity);
        }

        public PlayState diloPredicate(AnimationState<Dilophosaurus> state) {
            if (isBlocked()) return PlayState.STOP;
            AnimationController<Dilophosaurus> controller = state.getController();
            if (tryNextAnimation(state, controller)) {
                return PlayState.CONTINUE;
            }
            Optional<ActiveAnimationInfo> activeAnimation = getActiveAnimation(controller.getName());
            if (activeAnimation.isPresent() && tryForcedAnimation(state, activeAnimation.get())) {
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().isDisplaying()) {
                controller.setAnimation(RawAnimation.begin().thenPlay(INFLATE_START).thenLoop(INFLATE_HOLD));
                return PlayState.CONTINUE;
            }
            double animationSpeed = 1;
            if (state.getAnimatable().isWeak()) {
                addActiveAnimation(controller.getName(), AnimationCategory.KNOCKOUT);
            } else if (entity.isSleeping()) {
                addActiveAnimation(controller.getName(), AnimationCategory.SLEEP);
            } else if (state.getAnimatable().sitSystem.isSitting()) {
                addActiveAnimation(controller.getName(), AnimationCategory.SIT);
            } else if (entity.isInWater()) {
                addActiveAnimation(controller.getName(), AnimationCategory.SWIM, true);
            } else if (state.isMoving()) {
                animationSpeed = addMovementAnimation(state, true);
            } else {
                addActiveAnimation(controller.getName(), AnimationCategory.IDLE);
            }
            setAnimationSpeed(controller, animationSpeed, state.getAnimationTick());
            Optional<ActiveAnimationInfo> newAnimation = getActiveAnimation(controller.getName());
            if (newAnimation.isPresent()) {
                controller.transitionLength(newAnimation.get().transitionLength());
                controller.setAnimation(RawAnimation.begin().then(newAnimation.get().animationName(), newAnimation.get().loop() ? LOOP : PLAY_ONCE));
            }
            return PlayState.CONTINUE;
        }
    }
}
