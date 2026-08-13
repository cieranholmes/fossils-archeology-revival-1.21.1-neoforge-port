package com.github.fossils.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported without {@code ModItems.STONE_TABLET} (belongs to the Items phase, unstarted); {@link #dropItem}
 * falls back to a plain stone item until that phase adds the real stone tablet item.
 * <p>
 * {@code HangingEntity} no longer has the {@code getWidth()}/{@code getHeight()} sizing hooks used in 1.20.1;
 * it now requires {@link #calculateBoundingBox(BlockPos, Direction)} directly (mirrors vanilla
 * {@code Painting}'s implementation, adapted for our pixel-based {@link Variant} sizes).
 * <p>
 * Architectury's {@code EntitySpawnExtension} + {@code getAddEntityPacket()} is replaced with NeoForge's
 * {@link IEntityWithComplexSpawn} (see {@code DinosaurEgg} for the established pattern in this port).
 */
public class StoneTablet extends HangingEntity implements IEntityWithComplexSpawn {
    public Variant variant;

    public StoneTablet(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        //No synced data; variant/position/direction are sent via the complex spawn packet (see writeSpawnData).
    }

    public StoneTablet(Level level, BlockPos blockPos, Direction direction) {
        super(ModEntities.STONE_TABLET.get(), level, blockPos);
        List<Variant> validVariants = new ArrayList<>();
        this.variant = Variant.SOCIAL;
        for (Variant variant : Variant.values()) {
            this.variant = variant;
            setDirection(direction);
            if (!this.survives()) continue;
            validVariants.add(variant);
        }
        if (!validVariants.isEmpty()) {
            variant = validVariants.get(level.random.nextInt(validVariants.size()));
        }
        setDirection(direction);
    }

    public StoneTablet(Level level, BlockPos blockPos, Direction direction, Variant variant) {
        this(level, blockPos, direction);
        this.variant = variant;
        this.setDirection(direction);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", variant.ordinal());
        compound.putByte("Facing", (byte) direction.get2DDataValue());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        variant = Variant.values()[compound.getInt("Variant")];
        direction = Direction.from2DDataValue(compound.getByte("Facing"));
        setDirection(direction);
    }

    private int getWidth() {
        if (variant == null) {
            return 0;
        }
        return variant.sizeX;
    }

    private int getHeight() {
        if (variant == null) {
            return 0;
        }
        return variant.sizeY;
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        if (variant == null) {
            return AABB.ofSize(Vec3.atCenterOf(pos), 0, 0, 0);
        }
        float inset = 0.46875F;
        Vec3 center = Vec3.atCenterOf(pos).relative(direction, -inset);
        int widthBlocks = getWidth() / 16;
        int heightBlocks = getHeight() / 16;
        double widthOffset = offsetForSize(widthBlocks);
        double heightOffset = offsetForSize(heightBlocks);
        Direction counterClockwise = direction.getCounterClockWise();
        Vec3 offsetCenter = center.relative(counterClockwise, widthOffset).relative(Direction.UP, heightOffset);
        Direction.Axis axis = direction.getAxis();
        double sizeX = axis == Direction.Axis.X ? 0.0625 : widthBlocks;
        double sizeY = heightBlocks;
        double sizeZ = axis == Direction.Axis.Z ? 0.0625 : widthBlocks;
        return AABB.ofSize(offsetCenter, sizeX, sizeY, sizeZ);
    }

    private static double offsetForSize(int size) {
        return size % 2 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void dropItem(@Nullable Entity brokenEntity) {
        if (!level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        playSound(SoundEvents.STONE_BREAK, 1.0f, 1.0f);
        if (brokenEntity instanceof Player player && player.getAbilities().instabuild) {
            return;
        }

        spawnAtLocation(Items.STONE);
    }

    @Override
    public void playPlacementSound() {
        playSound(SoundEvents.STONE_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.variant.ordinal());
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.direction.get2DDataValue());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {
        this.variant = Variant.values()[buf.readVarInt()];
        this.pos = buf.readBlockPos();
        setDirection(Direction.from2DDataValue(buf.readUnsignedByte()));
    }

    public enum Variant {
        LIGHTNING("Lightning", 32, 16, 0, 0),
        SOCIAL("Social", 16, 16, 32, 0),
        GREAT_WAR("Greatwar", 32, 32, 0, 16),
        CLOCK("clock", 32, 16, 0, 48),
        PORTAL("Portal", 32, 32, 0, 64),
        HEROBRINE("Herobrine", 32, 32, 32, 32),
        FLAT_CREEP("FlatCreep", 16, 16, 48, 0),
        ANGRY("annoyangry", 16, 16, 48, 16),
        REX_1("Rex1", 32, 32, 64, 0),
        REX_2("Rex2", 32, 16, 64, 32),
        REX_3("Rex3", 32, 16, 64, 48),
        REX_4("Rex4", 32, 32, 64, 64),
        PUZZLE("Puzzle", 32, 32, 32, 64),
        GUN_FIGHT("GunFight", 64, 32, 32, 96),
        PRINCESS("Princess", 32, 32, 0, 96),
        MOSAURUS("Mosa", 32, 16, 224, 48),
        HOLY_MOSAURUS("HolyMosasaurus", 64, 32, 160, 48),
        ANCI_TM("AnciTM", 32, 32, 96, 0),
        MOD_TM("ModTM", 16, 32, 128, 0),
        VIG_TM("VigTM", 32, 32, 144, 0),
        SABER_HUNT("SaberHunt", 32, 16, 96, 32),
        ANU_PORTAL("AnuPortal", 32, 32, 96, 48),
        ANUBITE_1("Anubite1", 16, 16, 128, 32),
        ANUBITE_2("Anubite2", 16, 16, 144, 32),
        ANUBITE_3("Anubite3", 16, 16, 160, 32),
        ANUBITE_4("Anubite4", 16, 16, 176, 32),
        SARCOPHAGUS_OPEN("sarcophagus_open", 32, 32, 128, 48),
        SARCOPHAGUS_KILL("sarcophagus_kill", 32, 32, 96, 80),
        DEAD_ANU("deadAnu", 32, 32, 128, 80);

        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;

        Variant(String title, int xSize, int ySize, int textureX, int textureY) {
            this.title = title;
            this.sizeX = xSize;
            this.sizeY = ySize;
            this.offsetX = textureX;
            this.offsetY = textureY;
        }
    }
}
