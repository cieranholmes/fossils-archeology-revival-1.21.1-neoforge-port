package com.github.fossils.network;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.sounds.ModMusic;
import com.github.fossils.client.sounds.MusicHandler;
import com.github.fossils.sounds.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sends a starting {@link SoundEvent} from server to client
 */
public record S2CMusicMessage(SoundEvent soundEvent) implements CustomPacketPayload {
    public static final Type<S2CMusicMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("music"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMusicMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeVarInt(BuiltInRegistries.SOUND_EVENT.getId(msg.soundEvent())),
            buf -> new S2CMusicMessage(BuiltInRegistries.SOUND_EVENT.byId(buf.readVarInt()))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CMusicMessage payload, IPayloadContext context) {
        MusicHandler.startMusic(getMusic(payload.soundEvent()));
    }

    private static Music getMusic(SoundEvent soundEvent) {
        if (soundEvent == ModSounds.MUSIC_ANU.get()) {
            return ModMusic.ANU;
        } else if (soundEvent == ModSounds.MUSIC_FIRST_DINOSAUR.get()) {
            return ModMusic.FIRST_DINOSAUR;
        }
        return Musics.GAME;
    }
}
