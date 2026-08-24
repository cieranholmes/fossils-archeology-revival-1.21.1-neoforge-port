package com.github.fossil.entity.ai;

import com.github.fossil.entity.animation.ServerAnimationInfo;
import com.github.fossil.entity.prehistoric.base.Charging;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.world.entity.LivingEntity;

/**
 * A {@link DelayedAttackGoal} variant that flags {@link Charging#setCharging} while closing an extended distance on
 * the target - the entity's own {@code AnimationLogic} plays a windup+loop animation for as long as the flag is set
 * - then lands one normal attack with bonus knockback once in melee range, representing a headbutt/ram charge.
 */
public class ChargeAttackGoal<T extends Prehistoric & Charging> extends DelayedAttackGoal<T> {
    private final double chargeRangeSqr;
    private final float chargeKnockback;
    private long chargeHitTick = -1;

    public ChargeAttackGoal(T mob, double speedModifier, double chargeRange, float chargeKnockback) {
        super(mob, speedModifier, false);
        this.chargeRangeSqr = chargeRange * chargeRange;
        this.chargeKnockback = chargeKnockback;
    }

    @Override
    public void stop() {
        super.stop();
        mob.setCharging(false);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, boolean inRange) {
        long currentTime = mob.level().getGameTime();
        if (inRange) {
            boolean wasCharging = mob.isCharging();
            mob.setCharging(false);
            if (currentTime > attackEndTick + 20) {
                ServerAnimationInfo animationInfo = mob.startAttack();
                attackDamageTick = (long) (currentTime + animationInfo.actionDelay + 5);
                attackEndTick = (long) (currentTime + animationInfo.animation.length());
                if (attackDamageTick > attackEndTick) {
                    attackDamageTick = attackEndTick;
                }
                chargeHitTick = wasCharging ? attackDamageTick : -1;
            }
            if (attackDamageTick > 0 && currentTime == attackDamageTick) {
                mob.attackTarget(enemy);
                if (currentTime == chargeHitTick) {
                    double dx = mob.getX() - enemy.getX();
                    double dz = mob.getZ() - enemy.getZ();
                    enemy.knockback(chargeKnockback, dx, dz);
                    chargeHitTick = -1;
                }
                attackDamageTick = -1;
            }
        } else if (mob.distanceToSqr(enemy) <= chargeRangeSqr && mob.getSensing().hasLineOfSight(enemy)) {
            mob.setCharging(true);
        } else {
            mob.setCharging(false);
        }
    }
}
