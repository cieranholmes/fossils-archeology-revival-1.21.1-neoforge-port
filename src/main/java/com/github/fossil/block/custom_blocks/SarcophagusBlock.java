package com.github.fossil.block.custom_blocks;

import com.github.fossil.block.entity.ModBlockEntities;
import com.github.fossil.block.entity.SarcophagusBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SarcophagusBlock extends BaseEntityBlock {
    public static final MapCodec<SarcophagusBlock> CODEC = simpleCodec(SarcophagusBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 2);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = Block.box(1, 0, -1, 16, 49, 17);

    public SarcophagusBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LAYER, 0).setValue(LIT, false));
    }

    @Override
    protected MapCodec<SarcophagusBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (blockState.getValue(LAYER) != 0) {
            BlockPos below = pos.below(blockState.getValue(LAYER));
            return level.getBlockState(below).useWithoutItem(level, player, hit.withPosition(below));
        }
        if (level.getBlockEntity(pos) instanceof SarcophagusBlockEntity blockEntity) {
            if (blockEntity.getState() == SarcophagusBlockEntity.STATE_UNLOCKED) {
                blockEntity.setState(SarcophagusBlockEntity.STATE_OPENING);
                blockEntity.setDoorTimer(1);
                level.blockEvent(pos, blockState.getBlock(), SarcophagusBlockEntity.STATE_EVENT, blockEntity.getState());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(blockState, level, pos, player, hit);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        int layer = state.getValue(LAYER);
        if (direction.getAxis() == Direction.Axis.Y && !neighborState.is(this)) {
            if (layer == 1 || layer == 0 && direction == Direction.UP || layer == 2 && direction == Direction.DOWN) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.SARCOPHAGUS.get(),
                level.isClientSide ? SarcophagusBlockEntity::clientTick : SarcophagusBlockEntity::serverTick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SarcophagusBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), defaultBlockState().setValue(LAYER, 1).setValue(FACING, state.getValue(FACING)), 3);
        level.setBlock(pos.above(2), defaultBlockState().setValue(LAYER, 2).setValue(FACING, state.getValue(FACING)), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        switch (state.getValue(LAYER)) {
            case 1 -> {
                BlockState blockState = level.getBlockState(pos.below());
                return blockState.is(this) && blockState.getValue(LAYER) == 0;
            }
            case 2 -> {
                BlockState blockState = level.getBlockState(pos.below());
                return blockState.is(this) && blockState.getValue(LAYER) == 1;
            }
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LAYER, LIT);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.move(0, -state.getValue(LAYER), 0);
    }
}
