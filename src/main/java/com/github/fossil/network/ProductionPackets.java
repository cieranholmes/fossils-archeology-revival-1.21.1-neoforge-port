package com.github.fossil.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Source's CAP_CHANNEL/SYNC_CHANNEL packets, minus S2CMammalCapMessage (replaced entirely by
 * ModAttachments.MAMMAL_DATA's built-in sync handler - see fossil-phase3-capabilities-networking-progress memory)
 * and S2CSyncFoodMappingsMessage (source's food-value system was deliberately not ported in Phase 2 - target's
 * FoodMappings is hardcoded Java, identical on every client, so there's nothing to sync).
 */
public class ProductionPackets {
    static void register(PayloadRegistrar registrar) {
        registrar.playToClient(S2CSyncEntityInfoMessage.TYPE, S2CSyncEntityInfoMessage.STREAM_CODEC, S2CSyncEntityInfoMessage::handle);
        registrar.playToClient(S2CSyncEntityVariantsMessage.TYPE, S2CSyncEntityVariantsMessage.STREAM_CODEC, S2CSyncEntityVariantsMessage::handle);
        registrar.playToClient(S2CSyncConfigMessage.TYPE, S2CSyncConfigMessage.STREAM_CODEC, S2CSyncConfigMessage::handle);
        registrar.playToClient(S2CSyncActiveAnimationMessage.TYPE, S2CSyncActiveAnimationMessage.STREAM_CODEC, S2CSyncActiveAnimationMessage::handle);
        registrar.playToClient(S2CSyncToyAnimationMessage.TYPE, S2CSyncToyAnimationMessage.STREAM_CODEC, S2CSyncToyAnimationMessage::handle);
        registrar.playToClient(S2CMusicMessage.TYPE, S2CMusicMessage.STREAM_CODEC, S2CMusicMessage::handle);
        registrar.playToClient(S2CActivateAttackBoxesMessage.TYPE, S2CActivateAttackBoxesMessage.STREAM_CODEC, S2CActivateAttackBoxesMessage::handle);
        registrar.playToServer(C2SHitPlayerMessage.TYPE, C2SHitPlayerMessage.STREAM_CODEC, C2SHitPlayerMessage::handle);
        registrar.playToServer(C2SRiderForceFlyingMessage.TYPE, C2SRiderForceFlyingMessage.STREAM_CODEC, C2SRiderForceFlyingMessage::handle);
        registrar.playToServer(C2SVerticalFlightMessage.TYPE, C2SVerticalFlightMessage.STREAM_CODEC, C2SVerticalFlightMessage::handle);
    }
}
