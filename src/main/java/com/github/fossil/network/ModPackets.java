package com.github.fossil.network;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Replaces source's Architectury NetworkChannel/NetworkManager (three channels: CAP_CHANNEL, SYNC_CHANNEL, and a
 * debug-build-only DEBUG_CHANNEL) with NeoForge's RegisterPayloadHandlersEvent/PayloadRegistrar. NeoForge payloads
 * don't need separate named channels the way Architectury's did - one registrar handles every payload type, each
 * identified by its own Type<?> id. CAP_CHANNEL's only packet (S2CMammalCapMessage) has no equivalent here at all -
 * see fossil-phase3-capabilities-networking-progress memory - so this registrar only ever needs the production
 * and debug packet sets, not a third grouping.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class ModPackets {
    private static final String VERSION = "1";

    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        ProductionPackets.register(registrar);
        DebugPackets.register(registrar);
    }
}
