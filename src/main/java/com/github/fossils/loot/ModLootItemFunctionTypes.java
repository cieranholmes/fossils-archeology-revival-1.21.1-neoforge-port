package com.github.fossils.loot;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctionTypes {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CustomizeToDinoFunction>> CUSTOMIZE_TO_DINOSAUR =
            LOOT_FUNCTION_TYPES.register("customize_to_dinosaur", () -> new LootItemFunctionType<>(CustomizeToDinoFunction.CODEC));

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTION_TYPES.register(modEventBus);
    }
}
