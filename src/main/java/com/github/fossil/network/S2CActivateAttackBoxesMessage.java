package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Activate a mob's attack boxes on the client. Ideally only sent to the player being attacked.
 */
public record S2CActivateAttackBoxesMessage(int entityId, double attackDuration) implements CustomPacketPayload {
    public static final Type<S2CActivateAttackBoxesMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("activate_attack_boxes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CActivateAttackBoxesMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeDouble(msg.attackDuration());
            },
            buf -> new S2CActivateAttackBoxesMessage(buf.readInt(), buf.readDouble())
    );

    public S2CActivateAttackBoxesMessage(Entity entity, double attackDuration) {
        this(entity.getId(), attackDuration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CActivateAttackBoxesMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity instanceof Prehistoric prehistoric) {
            prehistoric.getEntityHitboxData().getAttackBoxData().activateAttackBoxes(context.player().level(), payload.attackDuration());
        }
    }
}
