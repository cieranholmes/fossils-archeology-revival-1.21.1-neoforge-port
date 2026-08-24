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

public class Gastornis extends Prehistoric {

    public Gastornis(EntityType<Gastornis> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.gastornis.speak"));
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, "animation.gastornis.drink"));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.GASTORNIS;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.TERROR_BIRD_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.TERROR_BIRD_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TERROR_BIRD_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.25f;
    }
}
