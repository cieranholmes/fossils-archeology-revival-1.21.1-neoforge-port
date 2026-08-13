package com.github.fossils.network.debug;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.gui.debug.DebugScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported from source's debug-only {@code S2CMarkMessage} - real (non-GUI) senders are {@code DinoPanicGoal} and
 * {@code CustomFlightMoveControl}, both restored to send this below. Receive side calls
 * {@code DebugScreen.showPath(...)}, which only does anything at all when {@code DebugScreen.showPaths} (a debug
 * HUD toggle, off by default) has been switched on.
 */
public record S2CMarkMessage(int[] targets, int[] blockStateIds, boolean below) implements CustomPacketPayload {
    public static final Type<S2CMarkMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_mark"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CMarkMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeVarIntArray(msg.targets());
                buf.writeVarIntArray(msg.blockStateIds());
                buf.writeBoolean(msg.below());
            },
            buf -> new S2CMarkMessage(buf.readVarIntArray(), buf.readVarIntArray(), buf.readBoolean())
    );

    /**
     * Matches source's {@code S2CMarkMessage(int[] targetArray, BlockState[] blocks, boolean below)} constructor -
     * {@code targetArray} holds 3 ints (x,y,z) per target.
     */
    public S2CMarkMessage(int[] targetArray, BlockState[] blocks, boolean below) {
        this(targetArray, idsOf(blocks), below);
    }

    private static int[] idsOf(BlockState[] blocks) {
        int[] ids = new int[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            ids[i] = Block.getId(blocks[i]);
        }
        return ids;
    }

    public List<BlockPos> targetPositions() {
        List<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < targets.length / 3; i++) {
            positions.add(new BlockPos(targets[3 * i], targets[3 * i + 1], targets[3 * i + 2]));
        }
        return positions;
    }

    public List<BlockState> blockStates() {
        List<BlockState> states = new ArrayList<>();
        for (int id : blockStateIds) {
            states.add(Block.stateById(id));
        }
        return states;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(S2CMarkMessage payload, IPayloadContext context) {
        if (context.player() != null) {
            DebugScreen.showPath(context.player(), payload.targetPositions(), payload.blockStates(), payload.below());
        }
    }
}
