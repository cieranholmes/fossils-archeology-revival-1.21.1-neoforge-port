package com.github.fossils.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class TarFluid {

    public static class Source extends BaseFlowingFluid.Source {

        public Source(Properties properties) {
            super(properties);
        }
    }

    public static class Flowing extends BaseFlowingFluid.Flowing {

        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
            return direction == Direction.DOWN && this.isSame(fluid);
        }
    }
}
