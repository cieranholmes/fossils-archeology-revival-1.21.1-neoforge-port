package com.github.fossil.material;

import com.github.fossil.Config;
import com.github.fossil.client.particle.ModParticles;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.entity.monster.TarSlime;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class TarFluid {

    private static void animateParticle(Level level, BlockPos pos, FluidState state, RandomSource random) {
        if (random.nextInt(5) == 0 && level.isEmptyBlock(pos.above())) {
            double posX = pos.getX() + random.nextDouble();
            double posY = pos.getY() + 1.0;
            double posZ = pos.getZ() + random.nextDouble();
            double speedX = (random.nextDouble() - 0.5D) * 0.3D;
            double speedY = 0.3D * random.nextDouble() + 0.2D;
            double speedZ = (random.nextDouble() - 0.5D) * 0.3D;
            level.addParticle(ModParticles.TAR_BUBBLE.get(), posX, posY, posZ, speedX, speedY, speedZ);
        }
    }

    private static void trySpawnTarSlime(Level level, BlockPos pos, boolean isSource) {
        int tarSlimeSpawnRate = Config.tarSlimesSpawnRate;
        if (level.getDifficulty() != Difficulty.PEACEFUL && Config.spawnTarSlimes &&
                level.random.nextInt(isSource ? tarSlimeSpawnRate : tarSlimeSpawnRate * 15) == 0) {
            double spawnRange = 16;
            int k = level.getEntities(ModEntities.TAR_SLIME.get(), new AABB(pos).inflate(spawnRange), Entity::isAlive).size();
            if (k < 6) {
                TarSlime slime = ModEntities.TAR_SLIME.get().create(level);
                slime.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
                slime.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(slime);
            }
        }
    }

    public static class Source extends BaseFlowingFluid.Source {

        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public void tick(Level level, BlockPos pos, FluidState state) {
            super.tick(level, pos, state);
            trySpawnTarSlime(level, pos, isSource(state));
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            animateParticle(level, pos, state, random);
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

        @Override
        public void tick(Level level, BlockPos pos, FluidState state) {
            super.tick(level, pos, state);
            trySpawnTarSlime(level, pos, isSource(state));
        }

        @Override
        protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
            if (Boolean.FALSE.equals(state.getValue(FALLING)) && random.nextInt(64) == 0) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        ModSounds.TAR.get(), SoundSource.BLOCKS, 0.3f, random.nextFloat() * 0.4f + 0.8f, false);
            }

            animateParticle(level, pos, state, random);
        }
    }
}
