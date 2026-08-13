package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.data.EntityDataLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Sync {@link EntityDataLoader.Data entity data} on server join to client
 */
public record S2CSyncEntityInfoMessage(Map<String, EntityDataLoader.Data> data) implements CustomPacketPayload {
    public static final Type<S2CSyncEntityInfoMessage> TYPE = new Type<>(id());
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncEntityInfoMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeMap(msg.data(), FriendlyByteBuf::writeUtf, EntityDataLoader.Data::writeBuf),
            buf -> new S2CSyncEntityInfoMessage(buf.readMap(HashMap::new, FriendlyByteBuf::readUtf, EntityDataLoader.Data::readBuf))
    );

    private static ResourceLocation id() {
        return FossilsAndArcheologyRevival.location("sync_entity_info");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncEntityInfoMessage payload, IPayloadContext context) {
        EntityDataLoader.INSTANCE.replaceData(payload.data());
    }
}
