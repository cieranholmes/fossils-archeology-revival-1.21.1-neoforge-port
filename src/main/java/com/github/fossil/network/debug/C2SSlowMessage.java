package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SSlowMessage(double modifier) implements CustomPacketPayload {
    private static final ResourceLocation SLOW_DOWN_ID = FossilsAndArcheologyRevival.location("debug_slow_down");

    public static final Type<C2SSlowMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_slow"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSlowMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeDouble(msg.modifier()),
            buf -> new C2SSlowMessage(buf.readDouble())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SSlowMessage payload, IPayloadContext context) {
        Player player = context.player();
        AttributeInstance instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        instance.removeModifiers();
        instance.addPermanentModifier(new AttributeModifier(SLOW_DOWN_ID, -(1 - payload.modifier()), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}
