package com.github.fossil.entity.data;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.animation.ServerAnimationInfoLoader;
import com.github.fossil.entity.variant.EntityVariantLoader;
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
