package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.DisplayGoal;
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

public class Citipati extends Prehistoric {

    public Citipati(EntityType<Citipati> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.citipati.call", "animation.citipati.speak"));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.CITIPATI;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CITIPATI_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.CITIPATI_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CITIPATI_DEATH.get();
    }
}
