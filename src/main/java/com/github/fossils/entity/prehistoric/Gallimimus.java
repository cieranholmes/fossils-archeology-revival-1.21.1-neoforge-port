package com.github.fossils.entity.prehistoric;

import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfoAI;
import com.github.fossils.entity.prehistoric.base.PrehistoricFlocking;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Gallimimus extends PrehistoricFlocking {

    public Gallimimus(EntityType<Gallimimus> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.GALLIMIMUS;
    }

    @Override
    protected int getMaxGroupSize() {
        return 10;
    }

    @Override
    public Item getOrderItem() {
        return Items.STICK;
    }

    @Override
    public PrehistoricEntityInfoAI.Response aiResponseType() {
        if (hasGroupLeader()) {
            return groupLeader.aiResponseType();
        }
        return groupSize >= 3 ? PrehistoricEntityInfoAI.Response.TERRITORIAL : PrehistoricEntityInfoAI.Response.SCARED;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GALLIMIMUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.GALLIMIMUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GALLIMIMUS_DEATH.get();
    }
}
