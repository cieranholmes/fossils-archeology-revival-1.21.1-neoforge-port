package com.github.fossil.world.feature.placement;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.ModEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class ModBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<OrePlacementBiomeModifier>> ORE_PLACEMENT =
            BIOME_MODIFIER_SERIALIZERS.register("ore_placement", () -> OrePlacementBiomeModifier.CODEC);

    public static final ResourceKey<BiomeModifier> ADD_MOAI_STATUE_KEY = createKey("add_moai_statue");
    public static final ResourceKey<BiomeModifier> ADD_ALLIGATOR_GAR_KEY = createKey("add_alligator_gar");
    public static final ResourceKey<BiomeModifier> ADD_COELACANTH_KEY = createKey("add_coelacanth");
    public static final ResourceKey<BiomeModifier> ADD_NAUTILUS_KEY = createKey("add_nautilus");
    public static final ResourceKey<BiomeModifier> ADD_STURGEON_KEY = createKey("add_sturgon");

    public static void register(IEventBus modEventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FossilsAndArcheologyRevival.location(name));
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);

        Holder<Biome> beach = biomes.getOrThrow(Biomes.BEACH);
        Holder<PlacedFeature> moaiStatue = features.getOrThrow(ModPlacedFeatures.MOAI_STATUE_KEY);
        context.register(ADD_MOAI_STATUE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(beach), HolderSet.direct(moaiStatue), GenerationStep.Decoration.SURFACE_STRUCTURES));

        context.register(ADD_ALLIGATOR_GAR_KEY, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.ALLIGATOR_GAR.get(), 4, 1, 4))));

        context.register(ADD_COELACANTH_KEY, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.OCEAN)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.COELACANTH.get(), 3, 1, 4))));

        context.register(ADD_NAUTILUS_KEY, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.OCEAN)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.NAUTILUS.get(), 2, 1, 4))));

        context.register(ADD_STURGEON_KEY, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.RIVER)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.STURGEON.get(), 5, 1, 4))));
    }
}
