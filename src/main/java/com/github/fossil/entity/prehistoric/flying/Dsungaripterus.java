package com.github.fossil.entity.prehistoric.flying;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Dsungaripterus extends PrehistoricFlying {
    public static final String TAKEOFF = "animation.dsungaripterus.takeoff";

    public Dsungaripterus(EntityType<Dsungaripterus> entityType, Level level) {
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
        return PrehistoricEntityInfo.DSUNGARIPTERUS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Override
    public @NotNull AnimationInfo nextTakeOffAnimation() {
        return getAllAnimations().get(TAKEOFF);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DSUNGARIPTERUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DSUNGARIPTERUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DSUNGARIPTERUS_DEATH.get();
    }
}
