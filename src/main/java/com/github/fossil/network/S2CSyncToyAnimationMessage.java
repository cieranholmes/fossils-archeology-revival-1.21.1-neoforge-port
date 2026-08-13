package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.ToyTetheredLog;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Signals the start of a toy animation
 */
public record S2CSyncToyAnimationMessage(int entityId, float animationX, float animationZ) implements CustomPacketPayload {
    public static final Type<S2CSyncToyAnimationMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("sync_toy_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncToyAnimationMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeFloat(msg.animationX());
                buf.writeFloat(msg.animationZ());
            },
            buf -> new S2CSyncToyAnimationMessage(buf.readInt(), buf.readFloat(), buf.readFloat())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncToyAnimationMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity instanceof ToyTetheredLog toy) {
            toy.startAnimation(payload.animationX(), payload.animationZ());
        }
    }
}
