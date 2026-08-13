package com.github.fossil.world.feature;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.world.feature.configuration.AshDiskConfiguration;
import com.github.fossil.world.feature.tree.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<Feature<?>, AshDiskFeature> ASH_DISK = FEATURES.register("ash_disk", AshDiskFeature::new);
    public static final DeferredHolder<Feature<?>, CalamitesTreeFeature> CALAMITES_TREE = FEATURES.register("calamites_tree", CalamitesTreeFeature::new);
    public static final DeferredHolder<Feature<?>, CordaitesTreeFeature> CORDAITES_TREE = FEATURES.register("cordaites_tree", CordaitesTreeFeature::new);
    public static final DeferredHolder<Feature<?>, MutantTreeFeature> MUTANT_TREE = FEATURES.register("mutant_tree", MutantTreeFeature::new);
    public static final DeferredHolder<Feature<?>, PalmTreeFeature> PALM_TREE = FEATURES.register("palm_tree", PalmTreeFeature::new);
    public static final DeferredHolder<Feature<?>, SigillariaTreeFeature> SIGILLARIA_TREE = FEATURES.register("sigillaria_tree", SigillariaTreeFeature::new);
    public static final DeferredHolder<Feature<?>, TempskyaTreeFeature> TEMPSKYA_TREE = FEATURES.register("tempskya_tree", TempskyaTreeFeature::new);
    public static final DeferredHolder<Feature<?>, MoaiStatueFeature> MOAI_STATUE = FEATURES.register("moai_statue", MoaiStatueFeature::new);
    public static final DeferredHolder<Feature<?>, VolcanoConeFeature> VOLCANO_CONE = FEATURES.register("volcano_cone", VolcanoConeFeature::new);
    public static final DeferredHolder<Feature<?>, VolcanoVentFeature> VOLCANO_VENT = FEATURES.register("volcano_vent", VolcanoVentFeature::new);

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}
