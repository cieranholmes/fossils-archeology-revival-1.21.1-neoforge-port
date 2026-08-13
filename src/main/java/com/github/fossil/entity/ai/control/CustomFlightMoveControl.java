package com.github.fossil.entity.ai.control;

import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossil.entity.util.Util;
import com.github.fossil.network.debug.S2CMarkMessage;
import com.github.fossil.util.Version;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class CustomFlightMoveControl extends SmoothTurningMoveControl {
    private final PrehistoricFlying mob;
    private boolean shouldLandAtTarget;
    private Vec3 flyingWanted = Vec3.ZERO;
    private int tick;
    private int lastStuckCheck;
    private Vec3 lastStuckCheckPos = Vec3.ZERO;

    public CustomFlightMoveControl(PrehistoricFlying mob) {
        super(mob);
        this.mob = mob;
    }

    public void setFlyingTarget(double x, double y, double z, boolean shouldLandAtTarget) {
        flyingWanted = new Vec3(x, y, z);
        this.shouldLandAtTarget = shouldLandAtTarget;
        this.speedModifier = 1;
        if (Version.debugEnabled()) {
            int[] targets = new int[]{Mth.floor(x), Mth.floor(y), Mth.floor(z)};
            BlockState[] blocks = new BlockState[]{(shouldLandAtTarget ? Blocks.EMERALD_BLOCK : Blocks.GOLD_BLOCK).defaultBlockState()};
            PacketDistributor.sendToAllPlayers(new S2CMarkMessage(targets, blocks, false));
        }
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        if (mob.isUsingStuckNavigation()) {
            flyingWanted = new Vec3(x, y, z);
        }
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    //Operation is a protected nested type on MoveControl - these let callers outside the control hierarchy (e.g. PrehistoricFlying) set it without referencing the type directly
    public void setOperationMoveTo() {
        setOperation(Operation.MOVE_TO);
    }

    public void setOperationWait() {
        setOperation(Operation.WAIT);
    }

    @Override
    public void tick() {
        if (!mob.isFlying()) {
            mob.setNoGravity(false);
            super.tick();
        } else if (operation == Operation.MOVE_TO) {
            mob.setNoGravity(true);
            Vec3 offset = flyingWanted.subtract(mob.position());
            double dist = offset.length();
            if (dist > Math.min(1.5, mob.getBbWidth())) {
                float initialYRot = mob.getYRot();
                float initialXRot = mob.getXRot();
                float initialYaw = Util.yRotToYaw(initialYRot);

                float targetYaw = (float) Mth.atan2(offset.z, offset.x) * Mth.RAD_TO_DEG;//atan2 always returns between -180 and 180 so no wrapdegrees needed
                float targetPitch = (float) Mth.atan2(-offset.y, offset.horizontalDistance()) * Mth.RAD_TO_DEG;

                float newYaw = Mth.approachDegrees(initialYaw, targetYaw, 4);
                float newPitch = Mth.approachDegrees(initialXRot, targetPitch, 4);

                mob.setYRot(Util.yawToYRot(newYaw));
                mob.yBodyRot = mob.getYRot();
                mob.setXRot(newPitch);
                if (Mth.degreesDifferenceAbs(initialYRot, mob.getYRot()) < 3) {
                    //Slows down before reaching the target to prevent overshooting
                    float limit = 1.2f;
                    if (dist > 15) {
                        limit = 2.2f;
                    } else if (dist > 5) {
                        limit = 1.8f;
                    }
                    limit = shouldLandAtTarget ? limit * 0.8f : limit;
                    speedModifier = Mth.approach((float) speedModifier, limit, (float) (0.05 * (1.8 / speedModifier)));
                } else {
                    speedModifier = Mth.approach((float) speedModifier, 0.2f, 0.025f);
                }

                double targetXMove = speedModifier * Mth.cos(newYaw * Mth.DEG_TO_RAD) * Math.abs(offset.x / dist);
                double targetYMove = speedModifier * Mth.sin(-targetPitch * Mth.DEG_TO_RAD) * Math.abs(offset.y / dist);
                double targetZMove = speedModifier * Mth.sin(newYaw * Mth.DEG_TO_RAD) * Math.abs(offset.z / dist);
                Vec3 move = mob.getDeltaMovement();
                double newX = Mth.approach((float) move.x, (float) targetXMove, 0.01f);
                double newY = Mth.approach((float) move.y, (float) targetYMove, 0.01f);
                double newZ = Mth.approach((float) move.z, (float) targetZMove, 0.01f);
                mob.setDeltaMovement(newX, newY, newZ);
            } else {
                if (mob.isUsingStuckNavigation()) {
                    mob.switchNavigator(false);
                }
                if (shouldLandAtTarget) {
                    if (!mob.level().isEmptyBlock(mob.blockPosition().below())) {
                        mob.onReachAirTarget(BlockPos.containing(flyingWanted));//TODO: Maybe onReachGroundTarget?
                        mob.setFlying(false);
                        operation = Operation.WAIT;
                    }
                } else {
                    mob.onReachAirTarget(BlockPos.containing(flyingWanted));
                    operation = Operation.WAIT;
                }
            }
            if (mob.verticalCollisionBelow && offset.y < 0) {
                if (!mob.isUsingStuckNavigation()) {
                    if (offset.horizontalDistance() < 6 && offset.y > -3) {
                        //Just walk if close enough
                        mob.setFlying(false);
                        operation = Operation.WAIT;
                        mob.moveTo(flyingWanted, true, false);
                        //TODO: Maybe onReachGroundTarget?
                    } else {
                        mob.doStuckNavigation(flyingWanted);
                    }
                }
            } else if (mob.horizontalCollision || mob.verticalCollision) {
                doStuckDetection(mob.position());
            }
        } else {
            mob.setNoGravity(false);
        }
    }

    private void doStuckDetection(Vec3 pos) {
        tick++;
        if (tick - lastStuckCheck > 100) {
            if (pos.distanceToSqr(lastStuckCheckPos) < 2.25) {
                if (mob.isUsingStuckNavigation()) {
                    mob.getNavigation().recomputePath();
                } else {
                    mob.doStuckNavigation(flyingWanted);
                }
            }
            lastStuckCheck = tick;
            lastStuckCheckPos = pos;
        }
    }
}
