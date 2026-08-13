package com.github.fossils.entity.prehistoric;

import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.item.ModItems;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Allosaurus extends Prehistoric {

    public Allosaurus(EntityType<Allosaurus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.ALLOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return ModItems.SKULL_STICK.get();
    }

    @Override
    public float getTargetScale() {
        return 2;
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() * 1.25f;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ALLOSAURUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ALLOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ALLOSAURUS_DEATH.get();
    }
}
