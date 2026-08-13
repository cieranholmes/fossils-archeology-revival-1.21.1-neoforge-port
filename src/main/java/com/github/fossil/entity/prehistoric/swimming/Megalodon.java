package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.damagesource.ModDamageTypes;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossil.item.ModItems;
import com.github.fossil.sounds.ModSounds;
import com.github.fossil.util.Gender;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Megalodon extends PrehistoricSwimming {
    public static final String GRAB = "animation.megalodon.grab";

    public Megalodon(EntityType<Megalodon> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        //goalSelector.addGoal(Util.ATTACK + 1, new BreachAttackGoal(this, 1));
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    protected void handleAirSupply(int airSupply) {
        if (isAlive() && !isInWaterOrBubble() && !isNoAi()) {
            setAirSupply(airSupply - 1);
            if (getAirSupply() == -20) {
                setAirSupply(0);
                hurt(damageSources().source(ModDamageTypes.SUFFOCATE_KEY), 2.0f);
            }
        } else {
            setAirSupply(1000);
        }
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.MEGALODON;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.SKULL_STICK.get();
    }

    @Override
    public float getGenderedScale() {
        return getGender() == Gender.MALE ? 0.8f : super.getGenderedScale();
    }

    @Override
    public float getTargetScale() {
        return 2;
    }

    @Override
    public @NotNull AnimationInfo nextGrabbingAnimation() {
        return getAllAnimations().get(GRAB);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ModSounds.MEGALODON_AMBIENT.get() : ModSounds.MOSASAURUS_AMBIENT_OUTSIDE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isInWater() ? ModSounds.MEGALODON_HURT.get() : ModSounds.MEGALODON_HURT_OUTSIDE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MEGALODON_DEATH.get();
    }
}
