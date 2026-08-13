package com.github.fossil.network;

import com.github.fossil.network.debug.C2SDisableAIMessage;
import com.github.fossil.network.debug.C2SDiscardMessage;
import com.github.fossil.network.debug.C2SForceAnimationMessage;
import com.github.fossil.network.debug.C2SRotationMessage;
import com.github.fossil.network.debug.C2SSlowMessage;
import com.github.fossil.network.debug.C2SStructureMessage;
import com.github.fossil.network.debug.C2STameMessage;
import com.github.fossil.network.debug.InstructionMessage;
import com.github.fossil.network.debug.S2CCancelAnimationMessage;
import com.github.fossil.network.debug.S2CMarkMessage;
import com.github.fossil.network.debug.SyncDebugInfoMessage;
import com.github.fossil.util.Version;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Source's debug-build-only DEBUG_CHANNEL packets.
 * <p>
 * All 11 of the original packets are now registered here. C2SStructureMessage and InstructionMessage were the last
 * two: both were withheld until source's {@code InstructionSystem} (a real {@code AISystem} subclass that
 * starts/steps/persists a scripted instruction sequence on a {@code Prehistoric}) existed as a server-side target -
 * it's now ported to {@code com.github.fossil.entity.prehistoric.system.InstructionSystem} (registered on every
 * {@code Prehistoric} the same way sleep/sit/leap/attach systems are). InstructionMessage's client branch still
 * writes into the {@code InstructionTab} static-field stub (see
 * {@link com.github.fossil.client.gui.debug.InstructionTab}) since the real GUI tab is a separate porting task.
 * <p>
 * SyncDebugInfoMessage and S2CMarkMessage were ported/wired earlier: both have real, non-GUI senders in source
 * (Prehistoric.aiStep()'s own per-tick broadcast; DinoPanicGoal/CustomFlightMoveControl's target-marking), so -
 * like the other debug packets - they're reachable now even though their full receive-side behavior
 * (DebugScreen-gated) waits on the still-unported DebugScreen.
 */
public class DebugPackets {
    static void register(PayloadRegistrar registrar) {
        if (!Version.debugEnabled()) {
            return;
        }
        registrar.playBidirectional(C2SDisableAIMessage.TYPE, C2SDisableAIMessage.STREAM_CODEC, C2SDisableAIMessage::handle);
        registrar.playToServer(C2SDiscardMessage.TYPE, C2SDiscardMessage.STREAM_CODEC, C2SDiscardMessage::handle);
        registrar.playToServer(C2SForceAnimationMessage.TYPE, C2SForceAnimationMessage.STREAM_CODEC, C2SForceAnimationMessage::handle);
        registrar.playToServer(C2SRotationMessage.TYPE, C2SRotationMessage.STREAM_CODEC, C2SRotationMessage::handle);
        registrar.playToServer(C2SSlowMessage.TYPE, C2SSlowMessage.STREAM_CODEC, C2SSlowMessage::handle);
        registrar.playToServer(C2SStructureMessage.TYPE, C2SStructureMessage.STREAM_CODEC, C2SStructureMessage::handle);
        registrar.playToServer(C2STameMessage.TYPE, C2STameMessage.STREAM_CODEC, C2STameMessage::handle);
        registrar.playBidirectional(InstructionMessage.TYPE, InstructionMessage.STREAM_CODEC, InstructionMessage::handle);
        registrar.playToClient(S2CCancelAnimationMessage.TYPE, S2CCancelAnimationMessage.STREAM_CODEC, S2CCancelAnimationMessage::handle);
        registrar.playToClient(S2CMarkMessage.TYPE, S2CMarkMessage.STREAM_CODEC, S2CMarkMessage::handle);
        registrar.playBidirectional(SyncDebugInfoMessage.TYPE, SyncDebugInfoMessage.STREAM_CODEC, SyncDebugInfoMessage::handle);
    }
}
