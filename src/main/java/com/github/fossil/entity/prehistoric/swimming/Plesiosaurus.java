package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimmingBucketable;
import com.github.fossil.item.ModItems;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Plesiosaurus extends PrehistoricSwimmingBucketable {

    public Plesiosaurus(EntityType<Plesiosaurus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.PLESIOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.MAGIC_CONCH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isInWater() ? ModSounds.PLESIOSAURUS_AMBIENT_INSIDE.get() : ModSounds.PLESIOSAURUS_AMBIENT_OUTSIDE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.PLESIOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.PLESIOSAURUS_DEATH.get();
    }
}
