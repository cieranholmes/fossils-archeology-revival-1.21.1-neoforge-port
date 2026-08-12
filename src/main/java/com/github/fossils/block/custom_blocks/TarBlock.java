package com.github.fossils.block.custom_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class TarBlock extends LiquidBlock {
    public TarBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    public static TarBlock get(FlowingFluid fluid, Properties properties) {
        return new TarBlock(fluid, properties) {
            @Override
            public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                return true;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                return 30;
            }

            @Override
            public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
                return 30;
            }
        };
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.makeStuckInBlock(state, new Vec3(0.1, 1, 0.1));
        } else if (entity instanceof ItemEntity) {
            entity.makeStuckInBlock(state, new Vec3(0.1, 0.1, 0.1));
            if (Mth.floor(entity.position().y + 0.875) == pos.getY()) {
                entity.playSound(SoundEvents.GENERIC_SWIM, 0.4f, 0.6f + level.random.nextFloat() * 0.4f);
                entity.discard();
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
