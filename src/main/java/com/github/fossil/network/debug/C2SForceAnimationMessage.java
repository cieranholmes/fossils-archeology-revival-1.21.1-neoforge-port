package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.animation.AnimationCategory;
import com.github.fossil.entity.prehistoric.base.PrehistoricAnimatable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SForceAnimationMessage(String controller, int entityId, String animation, double speed,
                                        int transitionLength, boolean loop) implements CustomPacketPayload {
    public static final Type<C2SForceAnimationMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_force_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SForceAnimationMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeUtf(msg.controller());
                buf.writeInt(msg.entityId());
                buf.writeUtf(msg.animation());
                buf.writeDouble(msg.speed());
                buf.writeInt(msg.transitionLength());
                buf.writeBoolean(msg.loop());
            },
            buf -> new C2SForceAnimationMessage(buf.readUtf(), buf.readInt(), buf.readUtf(), buf.readDouble(), buf.readInt(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SForceAnimationMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity instanceof PrehistoricAnimatable<?> animatable) {
            animatable.getAnimationLogic().forceAnimation(payload.controller(), animatable.getAllAnimations().get(payload.animation()),
                    AnimationCategory.IDLE, payload.speed(), payload.transitionLength(), payload.loop());
        }
    }
}
