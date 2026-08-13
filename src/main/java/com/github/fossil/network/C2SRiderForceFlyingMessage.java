package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SRiderForceFlyingMessage(int entityId, boolean flying) implements CustomPacketPayload {
    private static final Component FLYING_DISABLED = Component.translatable("entity.fossil.flying.disabled");

    public static final Type<C2SRiderForceFlyingMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("rider_force_flying"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRiderForceFlyingMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeBoolean(msg.flying());
            },
            buf -> new C2SRiderForceFlyingMessage(buf.readInt(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SRiderForceFlyingMessage payload, IPayloadContext context) {
        Player player = context.player();
        Entity entity = player.level().getEntity(payload.entityId());
        if (player.getServer() != null && !player.getServer().isFlightAllowed()) {
            player.displayClientMessage(FLYING_DISABLED, false);
        } else if (entity instanceof PrehistoricFlying mob) {
            Entity rider = entity.getControllingPassenger();
            if (rider != null && rider.getId() == player.getId()) {
                if (payload.flying()) {
                    if (mob.hasTakeOffAnimation()) {
                        mob.startTakeOff();
                    } else {
                        mob.setFlying(true);
                    }
                } else {
                    mob.setFlying(true);
                    mob.setFlying(false);
                }
            }
        }
    }
}
