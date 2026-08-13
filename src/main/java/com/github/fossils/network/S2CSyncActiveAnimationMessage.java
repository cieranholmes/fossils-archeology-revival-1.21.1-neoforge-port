package com.github.fossils.network;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.entity.animation.AnimationCategory;
import com.github.fossils.entity.animation.AnimationLogic;
import com.github.fossils.entity.prehistoric.base.PrehistoricAnimatable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sync currently active animation from server to clients
 */
public record S2CSyncActiveAnimationMessage(int entityId, String controller, String animationName,
                                             AnimationCategory category, int ticks, double speed, boolean loop) implements CustomPacketPayload {
    public static final Type<S2CSyncActiveAnimationMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("sync_active_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncActiveAnimationMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeUtf(msg.controller());
                buf.writeUtf(msg.animationName());
                buf.writeUtf(msg.category().name());
                buf.writeInt(msg.ticks());
                buf.writeDouble(msg.speed());
                buf.writeBoolean(msg.loop());
            },
            buf -> {
                int entityId = buf.readInt();
                String controller = buf.readUtf();
                String animationName = buf.readUtf();
                String categoryName = buf.readUtf();
                AnimationCategory category = AnimationCategory.CATEGORIES.stream()
                        .filter(c -> c.name().equals(categoryName)).findFirst().orElse(AnimationCategory.NONE);
                int ticks = buf.readInt();
                double speed = buf.readDouble();
                boolean loop = buf.readBoolean();
                return new S2CSyncActiveAnimationMessage(entityId, controller, animationName, category, ticks, speed, loop);
            }
    );

    public S2CSyncActiveAnimationMessage(Entity entity, String controller, AnimationLogic.ActiveAnimationInfo activeAnimationInfo) {
        this(entity.getId(), controller, activeAnimationInfo.animationName(), activeAnimationInfo.category(),
                activeAnimationInfo.transitionLength(), activeAnimationInfo.speed(), activeAnimationInfo.loop());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncActiveAnimationMessage payload, IPayloadContext context) {
        if (context.player() == null) {
            return;
        }
        Entity entity = context.player().level().getEntity(payload.entityId());
        if (entity instanceof PrehistoricAnimatable<?> prehistoric && prehistoric.getAllAnimations().containsKey(payload.animationName())) {
            double endTick = entity.level().getGameTime() + prehistoric.getAnimation(payload.animationName()).animation.length();
            AnimationLogic.ActiveAnimationInfo activeAnimationInfo = new AnimationLogic.Builder(payload.animationName(), endTick, payload.category())
                    .forced().transitionLength(payload.ticks()).speed(payload.speed()).loop(payload.loop()).build();
            prehistoric.getAnimationLogic().addNextAnimation(payload.controller(), activeAnimationInfo);
        }
    }
}
