package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.capabilities.ModAttachments;
import com.github.fossil.capabilities.ModCapabilities;
import com.github.fossil.client.gui.debug.DebugScreen;
import com.github.fossil.entity.PrehistoricSkeleton;
import com.github.fossil.entity.prehistoric.base.DinosaurEgg;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.util.Gender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Ported from source's debug-only {@code SyncDebugInfoMessage}. Real (non-GUI) sender:
 * {@code Prehistoric.aiStep()} broadcasts this every server tick (gated on {@code Version.debugEnabled()}) to
 * players within 16 blocks - that call site is restored below. Source's other senders
 * ({@code InfoTab}/{@code EggTab}/{@code EmbryoTab}/{@code SkeletonEditTab}, all C2S edits from the debug HUD's
 * info-editing tabs) are not wired since those tabs don't exist in target yet.
 * <p>
 * One shared code path like source: on the client, returns immediately unless the current screen is a
 * {@code DebugScreen} showing this exact entity; age/hunger/mood are only applied server-side, everything else
 * (gender/cooldowns/refresh) runs on both sides.
 */
public record SyncDebugInfoMessage(int entityId, String enumString, int age, int matingCooldown, int playingCooldown, int climbingCooldown, int hunger,
                                    int mood) implements CustomPacketPayload {
    public static final Type<SyncDebugInfoMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_sync_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDebugInfoMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeUtf(msg.enumString());
                buf.writeInt(msg.age());
                buf.writeInt(msg.matingCooldown());
                buf.writeInt(msg.playingCooldown());
                buf.writeInt(msg.climbingCooldown());
                buf.writeInt(msg.hunger());
                buf.writeInt(msg.mood());
            },
            buf -> new SyncDebugInfoMessage(buf.readInt(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncDebugInfoMessage payload, IPayloadContext context) {
        Player player = context.player();
        if (player == null) {
            return;
        }
        boolean clientSide = player.level().isClientSide;
        if (clientSide) {
            if (Minecraft.getInstance().screen instanceof DebugScreen) {
                if (DebugScreen.entity == null || DebugScreen.entity.getId() != payload.entityId()) {
                    return;
                }
            } else {
                return;
            }
        }
        Entity entity = player.level().getEntity(payload.entityId());
        if (entity instanceof Prehistoric prehistoric) {
            if (!clientSide) {
                prehistoric.setAgeInTicks(payload.age());
                prehistoric.setHunger(payload.hunger());
                prehistoric.moodSystem.setMood(payload.mood());
            }
            prehistoric.setGender(Gender.valueOf(payload.enumString()));
            prehistoric.setMatingCooldown(payload.matingCooldown());
            prehistoric.moodSystem.setPlayingCooldown(payload.playingCooldown());
            prehistoric.setClimbingCooldown(payload.climbingCooldown());
            prehistoric.refreshTexturePath();
            prehistoric.refreshDimensions();
            prehistoric.updateAbilities();
        } else if (entity instanceof PrehistoricSkeleton fossil) {
            fossil.setInfoType(payload.enumString());
            fossil.setAge(payload.age());
        } else if (entity instanceof DinosaurEgg egg) {
            egg.setHatchingTime(payload.age());
        } else if (entity instanceof Animal animal && ModCapabilities.hasEmbryo(animal)) {
            ModCapabilities.setEmbryoProgress(animal, payload.age());
            //Source resynced the mammal capability to the client here (S2CMammalCapMessage). Target's
            //ModAttachments.MAMMAL_DATA syncs automatically via AttachmentType.sync(...) - this triggers that.
            animal.syncData(ModAttachments.MAMMAL_DATA);
        }
    }
}
