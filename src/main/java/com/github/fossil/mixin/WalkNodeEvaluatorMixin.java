package com.github.fossil.mixin;

import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin extends NodeEvaluator {

    @WrapOperation(method = "findAcceptedNode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/pathfinder/WalkNodeEvaluator;canReachWithoutCollision(Lnet/minecraft/world/level/pathfinder/Node;)Z"))
    public boolean preventMobFromGettingStuck(WalkNodeEvaluator instance, Node node, Operation<Boolean> original) {
        boolean collides = !original.call(instance, node);
        if (collides && mob instanceof Prehistoric && mob.blockPosition().equals(node.asBlockPos())) {
            return false;
        }
        return collides;
    }
}
