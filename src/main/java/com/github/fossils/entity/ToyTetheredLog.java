package com.github.fossils.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

/**
 * Ported without {@code ModItems.TOY_TETHERED_LOGS} (a per-wood-type item map that belongs to the Items phase);
 * {@link #getPickResult()} falls back to a plain oak log until that phase adds the real toy log items.
 * <p>
 * NOTE: cross-client sync of the hit-animation (the network broadcast the source mod did in {@link #hurt}) is
 * not yet wired up - networking hasn't been ported (Phase 3). The local animation-state bookkeeping in
 * {@link #startAnimation} and {@link #tick} is kept as-is.
 */
public class ToyTetheredLog extends ToyBase {
    private static final EntityDataAccessor<String> WOOD_TYPE = SynchedEntityData.defineId(ToyTetheredLog.class, EntityDataSerializers.STRING);
    public int animationTick;
    public boolean animationPlaying;
    public float animationX;
    public float animationZ;

    public ToyTetheredLog(EntityType<ToyTetheredLog> type, Level level) {
        super(type, level, 30, SoundEvents.WOOD_HIT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(WOOD_TYPE, WoodType.OAK.name());
    }

    public void startAnimation(float animationX, float animationZ) {
        animationTick = 0;
        animationPlaying = true;
        this.animationX = animationX;
        this.animationZ = animationZ;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(0, 0, 0);
        if (!isAttachedToBlock()) {
            if (!level().isClientSide) {
                Block.popResource(level(), blockPosition(), getPickResult());
            }
            discard();
            playSound(attackNoise, 1, getVoicePitch());
        }
        if (level().isClientSide) {
            if (animationPlaying) {
                animationTick++;
            }
            if (animationTick >= 40) {
                animationPlaying = false;
                animationTick = 0;
            }
        }
    }

    private boolean isAttachedToBlock() {
        return !level().isEmptyBlock(blockPosition().above(2));
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.OAK_LOG);
    }

    public void setWoodType(String woodType) {
        entityData.set(WOOD_TYPE, woodType);
    }

    public String getWoodTypeName() {
        return entityData.get(WOOD_TYPE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("woodType", getWoodTypeName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        String woodType = compound.getString("woodType");
        if (woodType.isBlank()) {
            woodType = WoodType.OAK.name();
        }
        setWoodType(woodType);
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.2f;
    }
}
