package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SRotationMessage(int entityId, double rotation, byte flag) implements CustomPacketPayload {
    public static final byte Y_ROT = 0;
    public static final byte X_ROT = 1;

    public static final Type<C2SRotationMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_rotation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRotationMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeDouble(msg.rotation());
                buf.writeByte(msg.flag());
            },
            buf -> new C2SRotationMessage(buf.readInt(), buf.readDouble(), buf.readByte())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SRotationMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity == null) {
            return;
        }
        switch (payload.flag()) {
            case Y_ROT -> {
                if (entity instanceof LivingEntity) {
                    entity.setYBodyRot((float) payload.rotation());
                    entity.setYHeadRot((float) payload.rotation());
                } else {
                    entity.setYRot((float) payload.rotation());
                }
            }
            case X_ROT -> entity.setXRot((float) payload.rotation());
        }
    }
}
