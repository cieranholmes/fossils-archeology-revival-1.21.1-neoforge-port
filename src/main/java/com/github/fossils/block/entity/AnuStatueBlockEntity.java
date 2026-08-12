package com.github.fossils.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AnuStatueBlockEntity extends BlockEntity {

    public AnuStatueBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ANU_STATUE.get(), blockPos, blockState);
    }
}
