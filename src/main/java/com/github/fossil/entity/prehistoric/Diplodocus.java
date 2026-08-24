package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.DisplayGoal;
import com.github.fossil.entity.ai.DrinkGoal;
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

public class Diplodocus extends Prehistoric {

    public Diplodocus(EntityType<Diplodocus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.WANDER + 10, new DisplayGoal(this, "animation.diplodocus.speak"));
        goalSelector.addGoal(Util.NEEDS + 4, new DrinkGoal(this, "animation.diplodocus.drink"));
        goalSelector.addGoal(Util.IMMOBILE + 3, new FleeBattleGoal(this, 1));
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.DIPLODOCUS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DIPLODOCUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DIPLODOCUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DIPLODOCUS_DEATH.get();
    }
}
