package com.github.fossils.network.debug;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.stream.StreamSupport;

public record C2SDiscardMessage(int entityId) implements CustomPacketPayload {
    public static final Type<C2SDiscardMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_discard"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SDiscardMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeInt(msg.entityId()),
            buf -> new C2SDiscardMessage(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SDiscardMessage payload, IPayloadContext context) {
        Player player = context.player();
        if (player.level() instanceof ServerLevel serverLevel) {
            if (payload.entityId() == -1) {
                StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false)
                        .filter(entity -> !(entity instanceof Player)).forEach(Entity::discard);
            } else {
                StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false)
                        .filter(entity -> entity.getId() == payload.entityId()).forEach(Entity::discard);
            }
        }
    }
}
