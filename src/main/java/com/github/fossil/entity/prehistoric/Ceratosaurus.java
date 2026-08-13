package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Ceratosaurus extends Prehistoric {

    public Ceratosaurus(EntityType<Ceratosaurus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.CERATOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Override
    public float getTargetScale() {
        return 1.25f;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CERATOSAURUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.CERATOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CERATOSAURUS_DEATH.get();
    }
}
