package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.ChargeAttackGoal;
import com.github.fossil.entity.ai.DelayedAttackGoal;
import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.ai.DrinkGoal;
import com.github.fossil.entity.ai.FleeBattleGoal;
import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.animation.PausableAnimationController;
import com.github.fossil.entity.prehistoric.base.Charging;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

import java.util.Optional;

import static software.bernie.geckolib.animation.Animation.LoopType.LOOP;
import static software.bernie.geckolib.animation.Animation.LoopType.PLAY_ONCE;

public class Triceratops extends Prehistoric implements Charging {
    private static final String CHARGE_WINDUP = "animation.triceratops.ram_windup";
    private static final String CHARGE_LOOP = "animation.triceratops.ram";
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(Triceratops.class, EntityDataSerializers.BOOLEAN);
    private final TriceratopsAnimationLogic animationLogic = new TriceratopsAnimationLogic(this);

    public Triceratops(EntityType<Triceratops> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.triceratops.call", "animation.triceratops.speak"));
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, "animation.triceratops.drink"));
        goalSelector.addGoal(Util.IMMOBILE + 3, new FleeBattleGoal(this, 1.5));
    }

    @Override
    protected DelayedAttackGoal<? extends Prehistoric> createAttackGoal() {
        return new ChargeAttackGoal<>(this, attributes().sprintMod(), 14, 1.4f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Charging", isCharging());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setCharging(compound.getBoolean("Charging"));
    }

    @Override
    public boolean isCharging() {
        return entityData.get(CHARGING);
    }

    @Override
    public void setCharging(boolean charging) {
        entityData.set(CHARGING, charging);
    }

    @Override
    public String getChargeWindupAnimation() {
        return CHARGE_WINDUP;
    }

    @Override
    public String getChargeLoopAnimation() {
        return CHARGE_LOOP;
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.TRICERATOPS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TRICERATOPS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.TRICERATOPS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TRICERATOPS_DEATH.get();
    }

    @Override
    public AnimationLogic<Prehistoric> getAnimationLogic() {
        return animationLogic;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        var controller = new PausableAnimationController<>(this, AnimationLogic.IDLE_CTRL, 5, animationLogic::chargePredicate);
        registerEatingListeners(controller);
        registerControllerWithTriggers(controllerRegistrar, controller);

        var attackController = new PausableAnimationController<>(this, AnimationLogic.ATTACK_CTRL, 5, animationLogic::attackPredicate);
        registerControllerWithTriggers(controllerRegistrar, attackController);
    }

    static class TriceratopsAnimationLogic extends AnimationLogic<Prehistoric> {

        public TriceratopsAnimationLogic(Triceratops entity) {
            super(entity);
        }

        public PlayState chargePredicate(AnimationState<Triceratops> state) {
            if (isBlocked()) return PlayState.STOP;
            AnimationController<Triceratops> controller = state.getController();
            if (tryNextAnimation(state, controller)) {
                return PlayState.CONTINUE;
            }
            Optional<ActiveAnimationInfo> activeAnimation = getActiveAnimation(controller.getName());
            if (activeAnimation.isPresent() && tryForcedAnimation(state, activeAnimation.get())) {
                return PlayState.CONTINUE;
            }
            if (state.getAnimatable().isCharging()) {
                controller.setAnimation(RawAnimation.begin().thenPlay(CHARGE_WINDUP).thenLoop(CHARGE_LOOP));
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
