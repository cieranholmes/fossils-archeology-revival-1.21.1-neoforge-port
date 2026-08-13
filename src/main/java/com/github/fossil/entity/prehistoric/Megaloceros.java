package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.ai.FleeBattleGoal;
import com.github.fossil.entity.prehistoric.base.OrderType;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfoAI;
import com.github.fossil.entity.util.Util;
import com.github.fossil.sounds.ModSounds;
import com.github.fossil.util.Gender;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Megaloceros extends Prehistoric {
    private Megaloceros lastMate;

    public Megaloceros(EntityType<Megaloceros> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(Util.IMMOBILE + 3, new FleeBattleGoal(this, 1));
        targetSelector.addGoal(4, new LastMateHurtByTargetGoal(this));
    }

    @Override
    public void procreate(Prehistoric other) {
        super.procreate(other);
        if (other instanceof Megaloceros megaloceros) {
            lastMate = megaloceros;
        }
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.MEGALOCEROS;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0, -0.35, 0);
    }

    @Override
    public float getGenderedScale() {
        return getGender() == Gender.MALE ? 1.2f : super.getGenderedScale();
    }

    @Override
    public PrehistoricEntityInfoAI.Response aiResponseType() {
        return getGender() == Gender.MALE && !isBaby() ? PrehistoricEntityInfoAI.Response.TERRITORIAL : PrehistoricEntityInfoAI.Response.SCARED;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.MEGALOCEROS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.MEGALOCEROS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.MEGALOCEROS_DEATH.get();
    }

    static class LastMateHurtByTargetGoal extends TargetGoal {
        private final Megaloceros megaloceros;
        private int timestamp;

        public LastMateHurtByTargetGoal(Megaloceros megaloceros) {
            super(megaloceros, false);
            this.megaloceros = megaloceros;
        }

        @Override
        public boolean canUse() {
            if (megaloceros.aiResponseType() == PrehistoricEntityInfoAI.Response.SCARED || megaloceros.getCurrentOrder() != OrderType.WANDER) {
                return false;
            }
            if (megaloceros.lastMate == null) {
                return false;
            }
            targetMob = megaloceros.lastMate.getLastHurtByMob();
            int i = megaloceros.lastMate.getLastHurtByMobTimestamp();
            return i != timestamp && canAttack(targetMob, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            super.start();
            megaloceros.setTarget(targetMob);
            timestamp = megaloceros.lastMate.getLastHurtByMobTimestamp();
        }
    }
}
