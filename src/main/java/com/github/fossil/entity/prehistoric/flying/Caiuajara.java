package com.github.fossil.entity.prehistoric.flying;

import com.github.fossil.entity.ai.DinoHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtByTargetGoal;
import com.github.fossil.entity.ai.DinoOwnerHurtTargetGoal;
import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossil.entity.util.Util;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Caiuajara extends PrehistoricFlying {
    public static final String TAKEOFF = "animation.caiuajara.takeoff";

    public Caiuajara(EntityType<Caiuajara> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.caiuajara.call", "animation.caiuajara.speak"));
        targetSelector.addGoal(1, new DinoOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new DinoOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DinoHurtByTargetGoal(this));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.CAIUAJARA;
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
        return ModSounds.COMPSOGNATHUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.COMPSOGNATHUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.COMPSOGNATHUS_DEATH.get();
    }
}
