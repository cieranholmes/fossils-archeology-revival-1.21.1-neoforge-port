package com.github.fossil.entity.monster;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.world.dimension.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class AnuLairEvents {

    @SubscribeEvent
    static void preventExplosionBeforeAnuKilled(ExplosionEvent.Start event) {
        if (event.getLevel().dimension() == ModDimensions.ANU_LAIR && event.getLevel() instanceof ServerLevel serverLevel) {
            AnuBoss.AnuLair anuLair = serverLevel.getDataStorage().get(AnuBoss.AnuLair.FACTORY, "anu_lair");
            if (anuLair == null || !anuLair.isAnuKilled()) {
                event.setCanceled(true);
            }
        }
    }
}
