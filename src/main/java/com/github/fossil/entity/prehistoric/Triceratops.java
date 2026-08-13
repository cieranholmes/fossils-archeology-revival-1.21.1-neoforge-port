package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.FleeBattleGoal;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.util.Util;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Triceratops extends Prehistoric {

    public Triceratops(EntityType<Triceratops> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.IMMOBILE + 3, new FleeBattleGoal(this, 1.5));
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
}
