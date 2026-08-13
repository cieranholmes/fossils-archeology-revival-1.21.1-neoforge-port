package com.github.fossil.block.custom_blocks;

import com.github.fossil.block.entity.FakeObsidianBlockEntity;
import com.github.fossil.block.entity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FakeObsidian extends BaseEntityBlock {
    public static final MapCodec<FakeObsidian> CODEC = simpleCodec(FakeObsidian::new);

    public FakeObsidian(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<FakeObsidian> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.FAKE_OBSIDIAN.get(), FakeObsidianBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FakeObsidianBlockEntity(pos, state);
    }

    @Override
    public Item asItem() {
        Item item = super.asItem();
        if (item instanceof AirItem) {
            return Blocks.OBSIDIAN.asItem();
        }
        return item;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return Blocks.OBSIDIAN.getCloneItemStack(level, pos, state);
    }
}
