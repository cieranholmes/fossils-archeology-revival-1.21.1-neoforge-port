package com.github.fossils.entity.prehistoric.swimming;

import com.github.fossils.entity.ai.DinoHurtByTargetGoal;
import com.github.fossils.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossils.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossils.entity.ai.LeaveWaterGoal;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossils.entity.util.Util;
import com.github.fossils.item.ModItems;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Sarcosuchus extends PrehistoricSwimming {

    public Sarcosuchus(EntityType<Sarcosuchus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 1, new LeaveWaterGoal<>(this, 1));
        targetSelector.addGoal(1, new DinoOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new DinoOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.SARCOSUCHUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.SKULL_STICK.get();
    }

    @Override
    public float getTargetScale() {
        return 2;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isBaby() ? ModSounds.SARCOSUCHUS_BABY_AMBIENT.get() : ModSounds.SARCOSUCHUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.SARCOSUCHUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SARCOSUCHUS_DEATH.get();
    }
}
