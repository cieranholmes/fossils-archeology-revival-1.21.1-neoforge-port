package com.github.fossils.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Ported without the real {@code ModItems} javelin items (ANCIENT_JAVELIN/WOODEN_JAVELIN/etc. belong to the
 * Items phase, unstarted); {@link #getPickupItem()} and {@link #getDefaultPickupItem()} fall back to a plain
 * stick until that phase adds the real javelin items.
 * <p>
 * {@code AbstractArrow.setPierceLevel} is private in this Minecraft version (was protected in 1.20.1), so
 * pierce level is hardcoded via a {@link #getPierceLevel()} override instead of being written to synced data.
 */
public class Javelin extends AbstractArrow {
    private static final EntityDataAccessor<Integer> TIER_ID = SynchedEntityData.defineId(Javelin.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANCIENT = SynchedEntityData.defineId(Javelin.class, EntityDataSerializers.BOOLEAN);
    private int itemDamage;

    public Javelin(EntityType<Javelin> type, Level level) {
        super(type, level);
    }

    public Javelin(Level level, LivingEntity shooter, Tier tier, boolean ancient, int itemDamage) {
        super(ModEntities.JAVELIN.get(), shooter, level, ItemStack.EMPTY, null);
        this.itemDamage = itemDamage;
        if (tier instanceof Tiers tiers) {
            setTier(tiers);
        }
        entityData.set(ANCIENT, ancient);
        setBaseDamage(getDamage(tier, ancient));
    }

    private static double getDamage(Tier tier, boolean ancient) {
        if (ancient) {
            return 5;
        } else if (tier instanceof Tiers tiers) {
            switch (tiers) {
                case WOOD -> {
                    return 2;
                }
                case STONE -> {
                    return 2.5;
                }
                case GOLD -> {
                    return 3.5;
                }
                case IRON -> {
                    return 3;
                }
                case DIAMOND -> {
                    return 4;
                }
            }
        }
        return 2;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANCIENT, false);
        builder.define(TIER_ID, 0);
    }

    @Override
    public byte getPierceLevel() {
        return (byte) 16;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level() instanceof ServerLevel && isAncient() && random.nextInt(100) < 30) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level());
            lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPosition()));
            lightningBolt.setCause(getOwner() instanceof ServerPlayer ? (ServerPlayer) getOwner() : null);
            level().addFreshEntity(lightningBolt);
        }
    }

    @Override
    protected void tickDespawn() {

    }

    public Tier getTier() {
        if (!isAncient()) {
            return Tiers.values()[entityData.get(TIER_ID)];
        }
        return Tiers.WOOD;
    }

    public void setTier(Tiers tier) {
        entityData.set(TIER_ID, tier.ordinal());
    }

    public boolean isAncient() {
        return entityData.get(ANCIENT);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ancient", isAncient());
        if (getTier() instanceof Tiers tiers) {
            compound.putInt("Tier", tiers.ordinal());
        }
        compound.putInt("Damage", itemDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(ANCIENT, compound.getBoolean("ancient"));
        if (!isAncient()) {
            setTier(Tiers.values()[compound.getInt("Tier")]);
        }
        itemDamage = compound.getInt("Damage");
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.STICK);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(Items.STICK);
    }
}
