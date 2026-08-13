package com.github.fossil.block.custom_blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FossilBlock extends Block {

    public FossilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull SoundType getSoundType(BlockState blockState) {
        return SoundType.STONE;
    }
}
