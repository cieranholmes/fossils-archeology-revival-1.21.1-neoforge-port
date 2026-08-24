package com.github.fossil.entity.ai;

import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;

/**
 * A Goal that will move the entity to the nearest open water source and play a drinking animation once it arrives.
 * Purely cosmetic - unlike {@link EatBlockGoal} this does not affect hunger, since dinos have no thirst stat.
 */
public class DrinkGoal extends CacheMoveToBlockGoal {
    private final String drinkAnimation;
    private long animEndTick;
    private boolean drank;

    public DrinkGoal(Prehistoric entity, String drinkAnimation) {
        super(entity, 1, 32);
        this.drinkAnimation = drinkAnimation;
    }

    @Override
    public void start() {
        super.start();
        drank = false;
        animEndTick = 0;
    }

    @Override
    public boolean canUse() {
        if (entity.isInWater() || entity.getLastHurtByMob() != null) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (animEndTick != 0) {
            return entity.level().getGameTime() < animEndTick;
        }
        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (isReachedTarget() && !drank) {
            AnimationInfo animationInfo = entity.getAllAnimations().get(drinkAnimation);
            if (animationInfo != null) {
                entity.getAnimationLogic().triggerAnimation(AnimationLogic.IDLE_CTRL, animationInfo, AnimationCategory.EAT);
                animEndTick = (long) (entity.level().getGameTime() + animationInfo.animation.length());
            }
            drank = true;
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        if (!super.isValidTarget(level, pos)) {
            return false;
        }
        return level.getFluidState(pos).is(FluidTags.WATER) && level.getBlockState(pos.above()).isAir();
    }
}
