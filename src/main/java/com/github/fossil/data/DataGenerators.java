package com.github.fossil.data;

import com.github.fossil.data.providers.FossilAdvancements;
import com.github.fossil.data.providers.ModBlockStateProvider;
import com.github.fossil.data.providers.ModBlockTagsProvider;
import com.github.fossil.data.providers.ModEntityTypeTagsProvider;
import com.github.fossil.data.providers.ModItemProvider;
import com.github.fossil.data.providers.ModFoodValueProvider;
import com.github.fossil.data.providers.ModItemTagsProvider;
import com.github.fossil.data.providers.ModLootProvider;
import com.github.fossil.data.providers.ModRecipeProvider;
import com.github.fossil.enchantment.ModEnchantmentProvider;
import com.github.fossil.entity.damagesource.ModDamageTypes;
import com.github.fossil.world.dimension.ModDimensions;
import com.github.fossil.world.feature.configuration.ModConfiguredFeatures;
import com.github.fossil.world.biome.ModBiomes;
import com.github.fossil.world.feature.placement.ModBiomeModifiers;
import com.github.fossil.world.feature.placement.ModPlacedFeatures;
import com.github.fossil.world.feature.structures.ModStructureSets;
import com.github.fossil.world.feature.structures.ModStructures;
import com.github.fossil.world.feature.structures.ModTemplatePools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::gatherData);
    }

    private static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(Registries.ENCHANTMENT, ModEnchantmentProvider::bootstrap)
                .add(Registries.DAMAGE_TYPE, ModDamageTypes::bootstrap)
                .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
                .add(Registries.BIOME, ModBiomes::bootstrap)
                .add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap)
                .add(Registries.STRUCTURE, ModStructures::bootstrap)
                .add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap));
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        ExistingFileHelper efh = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), (DataProvider.Factory<DataProvider>) output -> new ModBlockStateProvider(output, efh));
        generator.addProvider(event.includeClient(), (DataProvider.Factory<DataProvider>) output -> new ModItemProvider(output, efh));
        var blockTags = new ModBlockTagsProvider(generator.getPackOutput(), lookup, efh);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DataProvider>) output -> new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), efh));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DataProvider>) output -> new ModEntityTypeTagsProvider(output, lookup, efh));
        generator.addProvider(event.includeServer(), new ModLootProvider(generator.getPackOutput(), lookup));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DataProvider>) output -> new ModRecipeProvider(output, lookup));
        generator.addProvider(event.includeServer(), new AdvancementProvider(generator.getPackOutput(), lookup, efh, List.of(new FossilAdvancements())));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DataProvider>) ModFoodValueProvider::new);
    }
}
