package com.github.fossil.mixin;

import com.github.fossil.entity.prehistoric.base.PrehistoricAnimatable;
import com.github.fossil.entity.prehistoric.base.PrehistoricLeaping;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public class GeoEntityRendererMixin {

    @Shadow
    protected Entity animatable;

    @ModifyExpressionValue(method = "actuallyRender", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean disableSitting(boolean original) {
        if (animatable instanceof PrehistoricLeaping leaping && leaping.getLeapSystem().isAttackRiding()) {
            return false;
        }
        return original;
    }

    @WrapOperation(method = "actuallyRender", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 useUninterpolatedVelocityWhileRidden(Entity entity, Operation<Vec3> original) {
        if (entity instanceof PrehistoricAnimatable<?> && entity.isVehicle() && !entity.isControlledByLocalInstance()) {
            return entity.position().subtract(new Vec3(entity.xOld, entity.yOld, entity.zOld));
        }
        return original.call(entity);
    }
}
