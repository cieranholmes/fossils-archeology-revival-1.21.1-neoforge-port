package com.github.fossil.entity.ai;

import com.github.fossil.entity.prehistoric.base.PrehistoricSwimming;

public class AmphibiousWanderGoal extends DinoWanderGoal {
    private final PrehistoricSwimming swimming;

    public AmphibiousWanderGoal(PrehistoricSwimming swimming, double speed) {
        super(swimming, speed);
        this.swimming = swimming;
    }

    @Override
    public boolean canUse() {
        if (mob.isInWater() || !swimming.isAmphibious()) {
            return false;
        }
        return super.canUse();
    }
}
