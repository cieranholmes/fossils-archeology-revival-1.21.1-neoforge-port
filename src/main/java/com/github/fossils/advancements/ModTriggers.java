package com.github.fossils.advancements;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Placeholder advancement triggers. The 1.20.1 SimpleCriterionTrigger JSON criterion API these were built on
 * was reworked around Codecs in 1.21.1; these no-op until the advancements phase ports the real criteria.
 */
public class ModTriggers {
    public static final ScarabTameTrigger SCARAB_TAME_TRIGGER = new ScarabTameTrigger();
    public static final IncubateEggTrigger INCUBATE_EGG_TRIGGER = new IncubateEggTrigger();

    public static void register() {
    }

    public static class ScarabTameTrigger {
        public void trigger(ServerPlayer player) {
        }
    }

    public static class IncubateEggTrigger {
        public void trigger(ServerPlayer player, Entity entity) {
        }
    }
}
