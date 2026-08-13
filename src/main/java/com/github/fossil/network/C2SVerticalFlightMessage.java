package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SVerticalFlightMessage(int entityId, int code) implements CustomPacketPayload {
    private static final int STOP = 0;
    private static final int FLY_UP = 1;
    private static final int FLY_DOWN = 2;

    public static final Type<C2SVerticalFlightMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("vertical_flight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SVerticalFlightMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeInt(msg.code());
            },
            buf -> new C2SVerticalFlightMessage(buf.readInt(), buf.readInt())
    );

    public static C2SVerticalFlightMessage flyUp(int entityId) {
        return new C2SVerticalFlightMessage(entityId, FLY_UP);
    }

    public static C2SVerticalFlightMessage flyDown(int entityId) {
        return new C2SVerticalFlightMessage(entityId, FLY_DOWN);
    }

    public static C2SVerticalFlightMessage stop(int entityId) {
        return new C2SVerticalFlightMessage(entityId, STOP);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SVerticalFlightMessage payload, IPayloadContext context) {
        Player player = context.player();
        Entity entity = player.level().getEntity(payload.entityId());
        if (entity instanceof PrehistoricFlying mob) {
            Entity rider = entity.getControllingPassenger();
            if (rider != null && rider.getId() == player.getId()) {
                if (payload.code() == FLY_UP) {
                    mob.setFlyingUp(true);
                    mob.setFlyingDown(false);
                } else if (payload.code() == FLY_DOWN) {
                    mob.setFlyingUp(false);
                    mob.setFlyingDown(true);
                } else if (payload.code() == STOP) {
                    mob.setFlyingUp(false);
                    mob.setFlyingDown(false);
                }
            }
        }
    }
}
