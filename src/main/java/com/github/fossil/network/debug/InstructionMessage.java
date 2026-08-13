package com.github.fossil.network.debug;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.client.gui.debug.InstructionTab;
import com.github.fossil.client.gui.debug.instruction.Instruction;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Ported from source's debug-only {@code InstructionMessage}. Bidirectional exactly like source's Architectury
 * registration (a single {@code NetworkChannel#register} with no direction restriction) - matches the
 * {@code C2SDisableAIMessage} idiom already established here of one bidirectional payload + one {@code handle()}
 * that branches on which side received it ({@code Level#isClientSide}), rather than two separate payload types.
 * <p>
 * Sent server-to-client by {@link com.github.fossil.entity.prehistoric.system.InstructionSystem#syncWithClients()}
 * to mirror the running instruction list into the debug HUD's {@link InstructionTab#INSTRUCTIONS} (read by the
 * still-unported {@code InstructionRenderer}/{@code InstructionsList}). Sent client-to-server by
 * {@code InstructionTab}'s Start button (once that real tab exists) to command the target entity's
 * {@code InstructionSystem} to start running a sequence built in the debug HUD.
 */
public record InstructionMessage(int entityId, boolean loop, List<Instruction> instructions) implements CustomPacketPayload {
    public static final Type<InstructionMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_instruction"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InstructionMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeInt(msg.entityId());
                buf.writeBoolean(msg.loop());
                Instruction.encodeBuffer(msg.instructions(), buf);
            },
            buf -> new InstructionMessage(buf.readInt(), buf.readBoolean(), Instruction.decodeBuffer(buf))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InstructionMessage payload, IPayloadContext context) {
        Level level = context.player().level();
        Entity entity = level.getEntity(payload.entityId());
        if (entity instanceof Prehistoric prehistoric) {
            if (level.isClientSide) {
                if (InstructionTab.activeEntity == null || InstructionTab.activeEntity.getId() != entity.getId()) {
                    InstructionTab.INSTRUCTIONS.put(entity.getUUID(), new InstructionTab.Pair(payload.entityId(), payload.instructions()));
                }
            } else {
                prehistoric.getInstructionSystem().start(payload.instructions(), payload.loop(), !payload.instructions().isEmpty());
            }
        }
    }
}
