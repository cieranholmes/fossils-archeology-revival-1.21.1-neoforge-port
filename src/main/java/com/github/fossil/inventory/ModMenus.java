package com.github.fossil.inventory;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FeederMenu>> FEEDER = register("feeder", FeederMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<SifterMenu>> SIFTER = register("sifter", SifterMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AnalyzerMenu>> ANALYZER = register("analyzer", AnalyzerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<WorktableMenu>> WORKTABLE = register("worktable", WorktableMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<CultureVatMenu>> CULTURE_VAT = register("culture_vat", CultureVatMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String key, MenuType.MenuSupplier<T> factory) {
        return MENUS.register(key, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
