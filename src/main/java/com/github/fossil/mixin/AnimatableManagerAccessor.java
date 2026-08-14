package com.github.fossil.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.animation.AnimatableManager;

@Mixin(AnimatableManager.class)
public interface AnimatableManagerAccessor {

    @Invoker("finishFirstTick")
    void fossil$finishFirstTick();
}
