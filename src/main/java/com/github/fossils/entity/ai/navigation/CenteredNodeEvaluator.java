package com.github.fossils.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Set;

/**
 * Overwrites the vanilla {@link WalkNodeEvaluator} to make entities move in the middle of the path instead of the edges.
 *
 * @see CenteredPath
 */
public class CenteredNodeEvaluator extends WalkNodeEvaluator {

    /**
     * @implNote This implementation tries the block at the center of the mob as well as the ones checked by {@link WalkNodeEvaluator}
     */
    @Override
    public Node getStart() {
        //Unchanged original code
        BlockPos blockPos;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int i = this.mob.getBlockY();
        BlockState blockState = this.currentContext.getBlockState(mutableBlockPos.set(this.mob.getX(), i, this.mob.getZ()));
        if (this.mob.canStandOnFluid(blockState.getFluidState())) {
            while (this.mob.canStandOnFluid(blockState.getFluidState())) {
                blockState = this.currentContext.getBlockState(mutableBlockPos.set(this.mob.getX(), ++i, this.mob.getZ()));
            }
            --i;
        } else if (this.canFloat() && this.mob.isInWater()) {
            while (blockState.is(Blocks.WATER) || blockState.getFluidState() == Fluids.WATER.getSource(false)) {
                blockState = this.currentContext.getBlockState(mutableBlockPos.set(this.mob.getX(), ++i, this.mob.getZ()));
            }
            --i;
        } else if (this.mob.onGround()) {
            i = Mth.floor(this.mob.getY() + 0.5);
        } else {
            blockPos = this.mob.blockPosition();
            while ((this.currentContext.getBlockState(blockPos).isAir() || this.currentContext.getBlockState(blockPos).isPathfindable(PathComputationType.LAND))
                    && blockPos.getY() > this.mob.level().getMinBuildHeight()) {
                blockPos = blockPos.below();
            }
            i = blockPos.above().getY();
        }
        blockPos = this.mob.blockPosition();
        PathType pathType = getCachedPathType(blockPos.getX(), i, blockPos.getZ());
        //Changed code
        if (this.mob.getPathfindingMalus(pathType) < 0.0f) {
            AABB aABB = this.mob.getBoundingBox();
            Vec3 center = aABB.setMinY(i).setMaxY(i).getCenter();
            if (canStartAt(mutableBlockPos.set(center.x, center.y, center.z)) || canStartAt(
                    mutableBlockPos.set(aABB.minX, i, aABB.maxZ)) || canStartAt(
                    mutableBlockPos.set(aABB.maxX, i, aABB.minZ)) || canStartAt(
                    mutableBlockPos.set(aABB.maxX, i, aABB.maxZ))) {
                return getStartNode(mutableBlockPos);
            }
        }
        return getStartNode(new BlockPos(blockPos.getX(), i, blockPos.getZ()));
    }

    /**
     * @implNote This implementation checks neighbours around the target instead of shifted to a positive x and z
     */
    @Override
    public Set<PathType> getPathTypeWithinMobBB(PathfindingContext context, int x, int y, int z) {
        EnumSet<PathType> pathTypes = EnumSet.noneOf(PathType.class);
        float width = Math.max(0, entityWidth - 2);
        int widthEachSide = Mth.ceil(width / 2.0f) + 1;
        for (int i = 0; i < widthEachSide; ++i) {
            for (int j = 0; j < entityHeight; ++j) {
                for (int k = 0; k < widthEachSide; ++k) {
                    pathTypes.add(getPathType(context, x + i, y + j, z + k));
                    if (i != 0 || k != 0) {
                        pathTypes.add(getPathType(context, x - i, y + j, z - k));
                    }
                }
            }
        }
        return pathTypes;
    }
}
