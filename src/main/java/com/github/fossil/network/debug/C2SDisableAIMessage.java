package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricDebug;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SDisableAIMessage(int id, boolean disableAI, byte aiType) implements CustomPacketPayload {
    public static final Type<C2SDisableAIMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_disable_ai"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SDisableAIMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.id());
                buf.writeBoolean(msg.disableAI());
                buf.writeByte(msg.aiType());
            },
            buf -> new C2SDisableAIMessage(buf.readInt(), buf.readBoolean(), buf.readByte())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SDisableAIMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.id());
        if (entity instanceof PrehistoricDebug prehistoric) {
            prehistoric.disableCustomAI(payload.aiType(), payload.disableAI());
        }
    }
}
