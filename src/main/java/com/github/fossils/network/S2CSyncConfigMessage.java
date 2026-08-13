package com.github.fossils.network;

import com.github.fossils.Config;
import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sync a fixed set of config values on server join to client. Source's FossilConfig.overrideEntries(Map, Map) was a
 * generic key->value override system; target's Config exposes these as plain mutable public static fields, so the
 * client-side handler just assigns them directly rather than porting a generic override map.
 */
public record S2CSyncConfigMessage(int pregnancyDuration, int anubiteCooldown, int machineMaxEnergy,
                                    boolean healingDinos, boolean whipToTameDino, boolean machinesRequireEnergy) implements CustomPacketPayload {
    public static final Type<S2CSyncConfigMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("sync_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncConfigMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.pregnancyDuration());
                buf.writeInt(msg.anubiteCooldown());
                buf.writeInt(msg.machineMaxEnergy());
                buf.writeBoolean(msg.healingDinos());
                buf.writeBoolean(msg.whipToTameDino());
                buf.writeBoolean(msg.machinesRequireEnergy());
            },
            buf -> new S2CSyncConfigMessage(buf.readInt(), buf.readInt(), buf.readInt(),
                    buf.readBoolean(), buf.readBoolean(), buf.readBoolean())
    );

    public S2CSyncConfigMessage() {
        this(Config.pregnancyDuration, Config.anubiteCooldown, Config.machineMaxEnergy,
                Config.healingDinos, Config.whipToTameDino, Config.machinesRequireEnergy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CSyncConfigMessage payload, IPayloadContext context) {
        Config.pregnancyDuration = payload.pregnancyDuration();
        Config.anubiteCooldown = payload.anubiteCooldown();
        Config.machineMaxEnergy = payload.machineMaxEnergy();
        Config.healingDinos = payload.healingDinos();
        Config.whipToTameDino = payload.whipToTameDino();
        Config.machinesRequireEnergy = payload.machinesRequireEnergy();
    }
}
