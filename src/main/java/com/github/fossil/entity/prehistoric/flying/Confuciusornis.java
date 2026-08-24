package com.github.fossil.entity.prehistoric.flying;

import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.ai.DrinkGoal;
import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossil.entity.util.Util;
import com.github.fossil.sounds.ModSounds;
import com.github.fossil.util.Gender;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Confuciusornis extends PrehistoricFlying {

    public Confuciusornis(EntityType<Confuciusornis> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.confuciusornis.call"));
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, "animation.confuciusornis.drink"));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.CONFUCIUSORNIS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Override
    public float getGenderedScale() {
        return getGender() == Gender.MALE ? 0.8f : super.getGenderedScale();
    }

    @Override
    public boolean hasTakeOffAnimation() {
        return false;
    }

    @Override
    public @NotNull AnimationInfo nextTakeOffAnimation() {
        return getAnimation(AnimationCategory.FLY);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CONFUCIUSORNIS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.CONFUCIUSORNIS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CONFUCIUSORNIS_DEATH.get();
    }
}
