package com.github.fossils.entity.data;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.entity.animation.ServerAnimationInfoLoader;
import com.github.fossils.entity.variant.EntityVariantLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class EntityDataEvents {

    @SubscribeEvent
    static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(EntityDataLoader.INSTANCE);
        event.addListener(EntityVariantLoader.INSTANCE);
        event.addListener(ServerAnimationInfoLoader.INSTANCE);
    }
}
