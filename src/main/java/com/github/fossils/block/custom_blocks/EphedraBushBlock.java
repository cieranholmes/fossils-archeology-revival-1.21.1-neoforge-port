package com.github.fossils.block.custom_blocks;

import com.github.fossils.block.PrehistoricPlantInfo;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

public class EphedraBushBlock extends ShortBerryBushBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;

    public EphedraBushBlock(VoxelShape shape, PrehistoricPlantInfo info) {
        super(shape, info);
    }

    @Override
    protected MapCodec<EphedraBushBlock> codec() {
        return simpleCodec(properties -> new EphedraBushBlock(shape, info));
    }

    @Override
    public IntegerProperty ageProperty() {
        return AGE;
    }
}
