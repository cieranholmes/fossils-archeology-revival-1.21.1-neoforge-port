package com.github.fossils.block.custom_blocks;

import com.github.fossils.block.entity.CultureVatBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class CultureVatBlock extends CustomEntityBlock {
    public static final MapCodec<CultureVatBlock> CODEC = simpleCodec(CultureVatBlock::new);
    public static final EnumProperty<EmbryoType> EMBRYO = EnumProperty.create("embryo", EmbryoType.class);

    public CultureVatBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(EMBRYO, EmbryoType.GENERIC));
    }

    @Override
    protected MapCodec<CultureVatBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EMBRYO);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CultureVatBlockEntity(pos, state);
    }

    public enum EmbryoType implements StringRepresentable {
        GENERIC("generic"),
        PLANT("plant"),
        TREE("tree"),
        LIMBLESS("limbless"),
        INSECT("insect"),
        NONE("none");

        private final String name;

        EmbryoType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
