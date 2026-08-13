package com.github.fossils.entity.prehistoric;

import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Protoceratops extends Prehistoric {

    public Protoceratops(EntityType<Protoceratops> type, Level level) {
        super(type, level);
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.PROTOCERATOPS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DRYOSAURUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DRYOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DRYOSAURUS_DEATH.get();
    }
}
