package com.github.fossil.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BubbleBlowerBlockEntity extends BlockEntity {
    private final AABB area;

    public BubbleBlowerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.BUBBLE_BLOWER.get(), blockPos, blockState);
        Vec3 pos = Vec3.atCenterOf(blockPos);
        this.area = new AABB(pos, pos).inflate(1.5);
    }

    public AABB getArea() {
        return area;
    }
}
