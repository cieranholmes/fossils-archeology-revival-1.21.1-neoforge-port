package com.github.fossil.entity.ai;

import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.animation.AnimationInfo;
import com.github.fossil.entity.animation.AnimationLogic;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Occasionally plays a one-shot flavor animation (e.g. a threat display) while the entity is otherwise idle.
 * Purely cosmetic - fires once via {@link AnimationLogic#triggerAnimation} and ends immediately, exactly like the
 * eat/drink one-shot trigger.
 */
public class DisplayGoal extends Goal {
    private final Prehistoric entity;
    @Nullable
    private final SoundEvent sound;
    private final String[] animationNames;
    private int cooldown;

    /**
     * @param animationNames one or more candidate animations - when more than one is given, a random one is picked
     *                        each time the goal fires (e.g. alternating between a species' speak/speak_2 clips)
     */
    public DisplayGoal(Prehistoric entity, String... animationNames) {
        this(entity, null, animationNames);
    }

    /**
     * @param sound          played alongside the animation, if given - most species have no dedicated vocal sound
     *                       asset for these flavor clips, so this is null in the common case
     * @param animationNames one or more candidate animations - when more than one is given, a random one is picked
     *                        each time the goal fires (e.g. alternating between a species' speak/speak_2 clips)
     */
    public DisplayGoal(Prehistoric entity, @Nullable SoundEvent sound, String... animationNames) {
        this.entity = entity;
        this.sound = sound;
        this.animationNames = animationNames;
        setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        if (cooldown > entity.tickCount || entity.isInWater() || entity.isImmobile() || entity.isVehicle()) {
            return false;
        }
        return entity.getRandom().nextInt(200) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        cooldown = entity.tickCount + (entity.getRandom().nextInt(200) + 20) * 20;
        String animationName = animationNames[entity.getRandom().nextInt(animationNames.length)];
        AnimationInfo animationInfo = entity.getAllAnimations().get(animationName);
        if (animationInfo != null) {
            entity.getAnimationLogic().triggerAnimation(AnimationLogic.IDLE_CTRL, animationInfo, AnimationCategory.IDLE);
        }
        if (sound != null) {
            entity.playSound(sound, 1.0F, 1.0F);
        }
    }
}
