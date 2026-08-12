package com.github.fossils.client;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.gui.AnalyzerScreen;
import com.github.fossils.client.gui.CultureVatScreen;
import com.github.fossils.client.gui.FeederScreen;
import com.github.fossils.client.gui.SifterScreen;
import com.github.fossils.client.gui.WorktableScreen;
import com.github.fossils.inventory.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANALYZER.get(), AnalyzerScreen::new);
        event.register(ModMenus.SIFTER.get(), SifterScreen::new);
        event.register(ModMenus.CULTURE_VAT.get(), CultureVatScreen::new);
        event.register(ModMenus.WORKTABLE.get(), WorktableScreen::new);
        event.register(ModMenus.FEEDER.get(), FeederScreen::new);
    }
}
