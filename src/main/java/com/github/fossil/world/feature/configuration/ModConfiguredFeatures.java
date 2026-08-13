package com.github.fossil.world.feature.configuration;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> CALAMITES_TREE_KEY = createKey("calamites_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CORDAITES_TREE_KEY = createKey("cordaites_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUTANT_TREE_KEY = createKey("mutant_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_TREE_KEY = createKey("palm_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIGILLARIA_TREE_KEY = createKey("sigillaria_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TEMPSKYA_TREE_KEY = createKey("tempskya_tree");

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, FossilsAndArcheologyRevival.location(name));
    }
}
