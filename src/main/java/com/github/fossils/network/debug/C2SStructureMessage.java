package com.github.fossils.network.debug;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.gui.debug.instruction.Instruction;
import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossils.entity.prehistoric.base.PrehistoricSwimming;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Ported from source's debug-only {@code C2SStructureMessage} - a debug-HUD button handler that lines up one of
 * every {@link Prehistoric} species on a scratch platform and drives each one along a short {@code MoveTo} path via
 * {@link com.github.fossils.entity.prehistoric.system.InstructionSystem}, for eyeballing every species' animations
 * at once. Client-to-server only, matching source's {@code playToServer}-equivalent registration.
 */
public record C2SStructureMessage(boolean onlyStructure) implements CustomPacketPayload {
    public static final Type<C2SStructureMessage> TYPE = new Type<>(FossilsAndArcheologyRevival.location("debug_structure"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SStructureMessage> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeBoolean(msg.onlyStructure()),
            buf -> new C2SStructureMessage(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(C2SStructureMessage payload, IPayloadContext context) {
        if (context.player().level() instanceof ServerLevel serverLevel) {
            spawnTestStructure(serverLevel, payload.onlyStructure());
        }
    }

    private static void spawnTestStructure(ServerLevel level, boolean onlyStructure) {
        int length = 0;
        List<Prehistoric> toAdd = new ArrayList<>();
        for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
            Entity entity = info.entityType().create(level);
            if (entity instanceof Prehistoric prehistoric) {
                toAdd.add(prehistoric);
                length++;
            }
        }
        BlockState ironBlock = Blocks.JUNGLE_PLANKS.defaultBlockState();
        for (BlockPos blockPos : BlockPos.betweenClosed(0, 119, 0, length * 5 + 20, 119, 50)) {
            level.setBlock(blockPos, ironBlock, 2);
        }
        for (int i = 0; i < toAdd.size(); i++) {
            Prehistoric prehistoric = toAdd.get(i);
            double xOffset = i * 5d + 3;
            List<Instruction> instructions = List.of(
                    new Instruction.TeleportTo(BlockPos.containing(xOffset, 120, 0), 0),
                    new Instruction.MoveTo(BlockPos.containing(xOffset, 120, 45)));
            prehistoric.finalizeSpawn(level, level.getCurrentDifficultyAt(prehistoric.blockPosition()), MobSpawnType.SPAWN_EGG, null);
            prehistoric.moveTo(xOffset, 120, 0, 0, 0);
            if (prehistoric instanceof PrehistoricSwimming swimming && !swimming.isAmphibious()) {
                swimming.setNoAi(true);
            } else {
                prehistoric.getInstructionSystem().start(instructions, true, true);
            }
            if (prehistoric instanceof PrehistoricFlying flying) {
                flying.setNoAi(true);
            }
            prehistoric.getInstructionSystem().syncWithClients();
            level.addFreshEntity(prehistoric);
        }
    }
}
