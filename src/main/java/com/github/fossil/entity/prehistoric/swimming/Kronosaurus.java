package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossil.item.ModItems;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Kronosaurus extends PrehistoricSwimming {
    public static final String GRAB = "animation.kronosaurus.grab";
    public static final String IDLE1 = "animation.kronosaurus.idle/sleep";
    public static final String IDLE2 = "animation.kronosaurus.idle";

    public Kronosaurus(EntityType<Kronosaurus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.KRONOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.SKULL_STICK.get();
    }

    @Override
    public @NotNull AnimationInfo nextGrabbingAnimation() {
        return getAllAnimations().get(GRAB);
    }

    @Override
    public @NotNull AnimationInfo nextIdleAnimation() {
        return random.nextFloat() > 0.1 ? getAllAnimations().get(IDLE1) : getAllAnimations().get(IDLE2);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ModSounds.LIOPLEURODON_AMBIENT_INSIDE.get() : ModSounds.LIOPLEURODON_AMBIENT_OUTSIDE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.LIOPLEURODON_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.LIOPLEURODON_DEATH.get();
    }
}
