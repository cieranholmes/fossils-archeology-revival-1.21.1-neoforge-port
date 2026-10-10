package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossil.item.ModItems;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Sea turtle - behaves like {@link Henodus} (semi-aquatic, defends its owner) but is too large to be bucketed.
 */
public class Archelon extends PrehistoricSwimming {

    public Archelon(EntityType<Archelon> entityType, Level level) {
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
        return PrehistoricEntityInfo.ARCHELON;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.MAGIC_CONCH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ARCHELON_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ARCHELON_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ARCHELON_DEATH.get();
    }
}
