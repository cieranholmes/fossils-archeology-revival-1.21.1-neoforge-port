package com.github.fossil.entity.ai;

import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.animation.ServerAnimationInfo;
import com.github.fossil.entity.prehistoric.base.OrderType;
import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.github.fossil.entity.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * {@link GrabMeleeAttackGoal}'s ATTACK/GRAB branches plus an occasional breach-and-slam LEAP attack, used by
 * Mosasaurus for its previously-unused "leap" animation. A full copy of GrabMeleeAttackGoal's logic (its fields are
 * private, so it can't be subclassed directly) with a third branch added.
 * <p>
 * Unlike ATTACK/GRAB, LEAP starts from outside melee range: the mosasaurus physically homes in on the target every
 * tick at a burst of speed while the leap animation plays, landing the hit on contact (or once the animation runs
 * out) - it's a genuine lunge, not just an alternate animation played in place.
 */
public class MosasaurusLeapAttackGoal extends DelayedAttackGoal<PrehistoricSwimming> {
    private static final int ATTACK = 0;
    private static final int GRAB = 1;
    private static final int LEAP = 2;
    private static final int GRAB_DURATION = 55;
    private static final double LEAP_RANGE = 10;
    private static final double LEAP_SPEED = 0.55;
    private final String leapAnimationName;
    private LivingEntity attackTarget;
    private int attackType = -1;
    private long grabStartTick = -1;
    private long lastLeapEndTick = -1;
    private double breachY;
    private boolean diving;

    public MosasaurusLeapAttackGoal(PrehistoricSwimming entity, double speed, String leapAnimationName) {
        super(entity, speed, false);
        this.leapAnimationName = leapAnimationName;
    }

    @Override
    public boolean canContinueToUse() {
        if (attackType == ATTACK || attackType == LEAP) {
            return super.canContinueToUse();
        }
        if (attackTarget == null || attackTarget.isRemoved() || mob.getCurrentOrder() == OrderType.STAY) {
            return false;
        }
        if (attackType == GRAB && (!mob.isDoingGrabAttack() || !mob.hasPassenger(attackTarget))) {
            attackEndTick = mob.level().getGameTime() + 20;
            return false;
        }
        return CAN_ATTACK_TARGET.test(attackTarget);
    }

    @Override
    public void start() {
        super.start();
        attackTarget = mob.getTarget();
        attackType = -1;
        grabStartTick = -1;
    }

    @Override
    public void stop() {
        super.stop();
        mob.setDoingGrabAttack(false);
    }

    @Override
    protected boolean canUpdateMovement() {
        return attackType != GRAB && attackType != LEAP;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, boolean inRange) {
        long currentTime = mob.level().getGameTime();
        if (attackType == GRAB) {
            for (Entity passenger : mob.getPassengers()) {
                if (passenger instanceof LivingEntity && passenger != mob.getRidingPlayer()) {
                    if (mob.tickCount % 20 == 0) {
                        boolean hurt = passenger.hurt(mob.damageSources().mobAttack(mob), (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        if (!hurt || (currentTime >= grabStartTick + GRAB_DURATION && mob.getRandom().nextInt(5) == 0)) {
                            attackEndTick = currentTime + 20;
                            mob.stopGrabAttack(passenger);
                        }
                    }
                }
            }
        } else if (attackType == ATTACK) {
            if (inRange && attackDamageTick > 0 && currentTime >= attackDamageTick) {
                mob.attackTarget(enemy);
                mob.destroyBoat(enemy);
                attackDamageTick = -1;
                attackType = -1;
            }
        } else if (attackType == LEAP) {
            performLeap(enemy, currentTime);
        } else if (inRange) {
            if (currentTime > attackEndTick + 20) {
                boolean tooBig = Util.isEntityLargerThan(enemy, mob.grabTargetSize());
                if (tooBig || mob.getRandom().nextInt(5) > 0 || mob.getVehicle() == enemy) {
                    attackType = ATTACK;
                    ServerAnimationInfo animationInfo = mob.startAttack();
                    attackEndTick = (long) (currentTime + animationInfo.animation.length());
                    attackDamageTick = Math.min((long) (currentTime + animationInfo.actionDelay), attackEndTick);
                } else {
                    attackType = GRAB;
                    mob.destroyBoat(enemy);
                    mob.startGrabAttack(enemy);
                    attackEndTick = 1;
                    grabStartTick = currentTime;
                }
            }
        } else if (canStartLeap(enemy, currentTime)) {
            attackType = LEAP;
            lastLeapEndTick = currentTime;
            AnimationInfo animationInfo = mob.getAllAnimations().get(leapAnimationName);
            if (animationInfo != null) {
                mob.getAnimationLogic().triggerAnimation(AnimationLogic.ATTACK_CTRL, animationInfo, AnimationCategory.ATTACK);
                attackEndTick = (long) (currentTime + animationInfo.animation.length());
                double surfaceY = findSurfaceY();
                breachY = (Double.isNaN(surfaceY) ? mob.getY() : surfaceY) + mob.getBbHeight() + 2;
                diving = false;
            } else {
                attackType = -1;
                attackEndTick = currentTime;
            }
        }
    }

    /**
     * The Y of the first open-air block reached by going straight up from the mosasaurus's current position without
     * passing through anything but water first - i.e. there's clear water above to breach through, not a cave
     * ceiling. {@link Double#NaN} if no such block is found within reach.
     */
    private double findSurfaceY() {
        BlockPos.MutableBlockPos pos = mob.blockPosition().mutable();
        int range = Mth.ceil(mob.getBbHeight()) + 6;
        for (int i = 0; i <= range; i++) {
            if (mob.level().getBlockState(pos).isAir()) {
                return pos.getY();
            }
            if (!mob.level().getFluidState(pos).is(FluidTags.WATER)) {
                return Double.NaN;
            }
            pos.move(Direction.UP);
        }
        return Double.NaN;
    }

    private boolean canStartLeap(LivingEntity enemy, long currentTime) {
        if (currentTime <= lastLeapEndTick + 60) {
            return false;
        }
        if (!mob.isInWater() || enemy.isUnderWater() || enemy.isVehicle()) {
            return false;
        }
        if (mob.distanceToSqr(enemy) > LEAP_RANGE * LEAP_RANGE) {
            return false;
        }
        if (!mob.getSensing().hasLineOfSight(enemy)) {
            return false;
        }
        return mob.getRandom().nextInt(8) == 0;
    }

    /**
     * Two-phase lunge: first homes toward a point above the water surface near the target - guaranteeing an actual
     * breach clear of the water regardless of how deep the target's height would otherwise take it - then, once
     * near that peak, dives straight down onto the target. Hits on contact (or the attempt simply times out once
     * the animation runs out, so a dodge doesn't leave it lunging forever).
     */
    private void performLeap(LivingEntity enemy, long currentTime) {
        lastLeapEndTick = currentTime;
        double reach = mob.getBbWidth() / 2 + enemy.getBbWidth() / 2 + 1;
        if (mob.distanceToSqr(enemy) <= reach * reach) {
            mob.attackTarget(enemy);
            double dx = mob.getX() - enemy.getX();
            double dz = mob.getZ() - enemy.getZ();
            enemy.knockback(1.2f, dx, dz);
            attackType = -1;
            attackEndTick = currentTime;
            return;
        }
        if (currentTime >= attackEndTick) {
            attackType = -1;
            return;
        }
        Vec3 aimPoint;
        if (!diving) {
            aimPoint = new Vec3(enemy.getX(), breachY, enemy.getZ());
            if (mob.getY() >= breachY - 0.5) {
                diving = true;
            }
        } else {
            aimPoint = enemy.position().add(0, enemy.getBbHeight() / 2, 0);
        }
        Vec3 direction = aimPoint.subtract(mob.position());
        if (direction.lengthSqr() > 1.0E-4) {
            mob.setDeltaMovement(direction.normalize().scale(LEAP_SPEED));
            mob.getLookControl().setLookAt(enemy, 90, 90);
        }
    }
}
