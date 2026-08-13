package com.github.fossil.world.feature.placement;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<OrePlacementBiomeModifier>> ORE_PLACEMENT =
            BIOME_MODIFIER_SERIALIZERS.register("ore_placement", () -> OrePlacementBiomeModifier.CODEC);

    public static void register(IEventBus modEventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }
}
