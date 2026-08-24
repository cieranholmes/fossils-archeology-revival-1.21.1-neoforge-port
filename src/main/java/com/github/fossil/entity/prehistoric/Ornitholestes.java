package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.ai.DrinkGoal;
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

public class Ornitholestes extends Prehistoric {

    public Ornitholestes(EntityType<Ornitholestes> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.ornitholestes.call"));
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, "animation.ornitholestes.drink"));
        goalSelector.addGoal(Util.NEEDS + 5, new DisplayGoal(this, "animation.ornitholestes.display"));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.ORNITHOLESTES;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Override
    public float getTargetScale() {
        return 1.5f;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ORNITHOLESTES_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ORNITHOLESTES_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ORNITHOLESTES_DEATH.get();
    }
}
