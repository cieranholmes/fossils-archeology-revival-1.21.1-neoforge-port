package com.github.fossil.entity.prehistoric.base;

/**
 * Implemented by species that play a windup+loop "charge" animation while closing distance on a target before a
 * heavier attack (see {@link com.github.fossil.entity.ai.ChargeAttackGoal}).
 */
public interface Charging {
    boolean isCharging();

    void setCharging(boolean charging);

    String getChargeWindupAnimation();

    String getChargeLoopAnimation();
}
