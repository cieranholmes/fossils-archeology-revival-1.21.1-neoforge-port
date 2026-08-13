package com.github.fossils.network;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.entity.variant.EntityVariantLoader;
import com.github.fossils.entity.variant.Variant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Sync {@link Variant entity variants} on server join to client
 */
public record S2CSyncEntityVariantsMessage(Map<String, Map<String, Variant>> data) implements CustomPacketPayload {
    public static final Type<S2CSyncEntityVariantsMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("sync_entity_variants"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncEntityVariantsMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeMap(msg.data(), FriendlyByteBuf::writeUtf,
                    (innerBuf, map) -> innerBuf.writeMap(map, FriendlyByteBuf::writeUtf, Variant::writeBuf)),
            buf -> new S2CSyncEntityVariantsMessage(buf.readMap(HashMap::new, FriendlyByteBuf::readUtf,
                    innerBuf -> innerBuf.readMap(HashMap::new, FriendlyByteBuf::readUtf, Variant::readBuf)))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncEntityVariantsMessage payload, IPayloadContext context) {
        EntityVariantLoader.INSTANCE.replaceVariants(payload.data());
    }
}
