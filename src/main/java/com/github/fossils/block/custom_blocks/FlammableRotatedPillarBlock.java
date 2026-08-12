package com.github.fossils.block.custom_blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.github.fossils.block.ModBlocks.*;

public class FlammableRotatedPillarBlock extends RotatedPillarBlock {

    private static final Map<Block, Block> STRIPPABLES = new Object2ObjectOpenHashMap<>();

    private FlammableRotatedPillarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static RotatedPillarBlock get(BlockBehaviour.Properties properties) {
        return new FlammableRotatedPillarBlock(properties);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (ItemAbilities.AXE_STRIP == itemAbility) {
            Block block = STRIPPABLES.get(state.getBlock());
            return block != null ? block.defaultBlockState().setValue(AXIS, state.getValue(AXIS)) : null;
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    private static void registerStripped(Block base, Block stripped) {
        STRIPPABLES.put(base, stripped);
    }

    /**
     * Called from common setup, once all blocks have finished registering.
     */
    public static void registerAllStripped() {
        registerStripped(CALAMITES_LOG.get(), STRIPPED_CALAMITES_LOG.get());
        registerStripped(CORDAITES_LOG.get(), STRIPPED_CORDAITES_LOG.get());
        registerStripped(PALM_LOG.get(), STRIPPED_PALM_LOG.get());
        registerStripped(SIGILLARIA_LOG.get(), STRIPPED_SIGILLARIA_LOG.get());
        registerStripped(TEMPSKYA_LOG.get(), STRIPPED_TEMPSKYA_LOG.get());
        registerStripped(MUTANT_TREE_LOG.get(), STRIPPED_MUTANT_TREE_LOG.get());
        registerStripped(CALAMITES_WOOD.get(), STRIPPED_CALAMITES_WOOD.get());
        registerStripped(CORDAITES_WOOD.get(), STRIPPED_CORDAITES_WOOD.get());
        registerStripped(PALM_WOOD.get(), STRIPPED_PALM_WOOD.get());
        registerStripped(SIGILLARIA_WOOD.get(), STRIPPED_SIGILLARIA_WOOD.get());
        registerStripped(TEMPSKYA_WOOD.get(), STRIPPED_TEMPSKYA_WOOD.get());
        registerStripped(MUTANT_TREE_WOOD.get(), STRIPPED_MUTANT_TREE_WOOD.get());
    }
}
