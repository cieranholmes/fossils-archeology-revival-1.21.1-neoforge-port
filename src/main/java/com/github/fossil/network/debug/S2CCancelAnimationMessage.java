package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricAnimatable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CCancelAnimationMessage(int entityId, String controller) implements CustomPacketPayload {
    public static final Type<S2CCancelAnimationMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_cancel_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCancelAnimationMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeUtf(msg.controller());
            },
            buf -> new S2CCancelAnimationMessage(buf.readInt(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CCancelAnimationMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity instanceof PrehistoricAnimatable<?> prehistoric) {
            prehistoric.getAnimationLogic().cancelAnimation(payload.controller());
        }
    }
}
