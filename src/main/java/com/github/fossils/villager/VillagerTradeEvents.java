package com.github.fossils.villager;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class VillagerTradeEvents {

    @SubscribeEvent
    static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == ModVillagers.ARCHEOLOGIST.get()) {
            var trades = event.getTrades();
            trades.get(1).addAll(ModTrades.getArcheoList(1));
            trades.get(2).addAll(ModTrades.getArcheoList(2));
            trades.get(3).addAll(ModTrades.getArcheoList(3));
            trades.get(4).addAll(ModTrades.getArcheoList(4));
            trades.get(5).addAll(ModTrades.getArcheoList(5));
        } else if (event.getType() == ModVillagers.PALEONTOLOGIST.get()) {
            var trades = event.getTrades();
            trades.get(1).addAll(ModTrades.getPaleoList(1));
            trades.get(2).addAll(ModTrades.getPaleoList(2));
            trades.get(3).addAll(ModTrades.getPaleoList(3));
            trades.get(4).addAll(ModTrades.getPaleoList(4));
            trades.get(5).addAll(ModTrades.getPaleoList(5));
        }
    }
}
