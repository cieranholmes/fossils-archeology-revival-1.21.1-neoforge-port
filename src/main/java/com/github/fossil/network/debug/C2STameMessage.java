package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2STameMessage(int id) implements CustomPacketPayload {
    public static final Type<C2STameMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_tame"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2STameMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeInt(msg.id()),
            buf -> new C2STameMessage(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2STameMessage payload, IPayloadContext context) {
        Entity entity = context.player().level().getEntity(payload.id());
        if (entity instanceof TamableAnimal animal) {
            animal.tame(context.player());
        } else if (entity instanceof AbstractHorse horse) {
            horse.tameWithName(context.player());
        }
    }
}
