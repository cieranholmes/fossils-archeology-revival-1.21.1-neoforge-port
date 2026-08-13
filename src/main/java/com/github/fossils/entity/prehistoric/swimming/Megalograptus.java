package com.github.fossils.entity.prehistoric.swimming;

import com.github.fossils.entity.ai.DinoHurtByTargetGoal;
import com.github.fossils.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossils.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricSwimmingBucketable;
import com.github.fossils.item.ModItems;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Megalograptus extends PrehistoricSwimmingBucketable {

    public Megalograptus(EntityType<Megalograptus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new DinoOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new DinoOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }
    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.MEGALOGRAPTUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.MAGIC_CONCH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.MEGALOGRAPTUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MEGALOGRAPTUS_DEATH.get();
    }
}
