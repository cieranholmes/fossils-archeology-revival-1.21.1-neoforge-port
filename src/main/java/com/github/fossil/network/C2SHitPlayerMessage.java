package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Cause a player hit on the server side. Will only trigger for the player that sent the message.
 */
public record C2SHitPlayerMessage(int entityId, int targetId) implements CustomPacketPayload {
    public static final Type<C2SHitPlayerMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("hit_player"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SHitPlayerMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeInt(msg.targetId());
            },
            buf -> new C2SHitPlayerMessage(buf.readInt(), buf.readInt())
    );

    public C2SHitPlayerMessage(Entity entity, Entity target) {
        this(entity.getId(), target.getId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SHitPlayerMessage payload, IPayloadContext context) {
        Player player = context.player();
        Entity entity = player.level().getEntity(payload.entityId());
        if (entity instanceof Prehistoric prehistoric && player.getId() == payload.targetId()) {
            prehistoric.attackTarget(player);
        }
    }
}
