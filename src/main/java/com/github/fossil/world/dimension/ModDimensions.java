package com.github.fossil.world.dimension;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class ModDimensions {
    public static final ResourceKey<Level> TREASURE_ROOM = ResourceKey.create(Registries.DIMENSION, FossilsAndArcheologyRevival.location("treasure_room"));
    public static final ResourceKey<Level> ANU_LAIR = ResourceKey.create(Registries.DIMENSION, FossilsAndArcheologyRevival.location("anu_lair"));
    public static final ResourceKey<DimensionType> TREASURE_ROOM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, TREASURE_ROOM.location());
    public static final ResourceKey<DimensionType> ANU_LAIR_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ANU_LAIR.location());

    /**
     * Teleports the entity to the anu dimension at a fixed point and places an obsidian platform below the entity.
     * Ported from Forge's {@code ITeleporter}/{@code PortalInfo} mechanism, which no longer exists at all in
     * NeoForge 1.21.1 - vanilla itself reworked dimension travel around {@link DimensionTransition} in this version,
     * so this uses that directly instead of the old platform-abstraction {@code @ExpectPlatform changeDimension}.
     */
    public static void teleportToAnuLair(MinecraftServer server, Entity entity) {
        ServerLevel anuLair = server.getLevel(ModDimensions.ANU_LAIR);
        Vec3 spawnPoint = new Vec3(70.5, 63, -17.5);
        int x = (int) spawnPoint.x;
        int y = (int) (spawnPoint.y - 2);
        int z = (int) spawnPoint.z;
        BlockPos.betweenClosed(x - 2, y + 1, z - 2, x + 2, y + 3, z + 2).forEach(blockPos -> anuLair.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed(x - 2, y, z - 2, x + 2, y, z + 2).forEach(blockPos -> anuLair.setBlockAndUpdate(blockPos, Blocks.OBSIDIAN.defaultBlockState()));
        entity.changeDimension(new DimensionTransition(anuLair, spawnPoint, entity.getDeltaMovement(), 0, 90, DimensionTransition.DO_NOTHING));
    }
}
