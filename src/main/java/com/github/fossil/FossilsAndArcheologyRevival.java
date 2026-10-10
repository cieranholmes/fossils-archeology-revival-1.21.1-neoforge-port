package com.github.fossil;

import com.github.fossil.advancements.ModTriggers;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.custom_blocks.FlammableRotatedPillarBlock;
import com.github.fossil.block.entity.ModBlockEntities;
import com.github.fossil.capabilities.ModAttachments;
import com.github.fossil.client.particle.ModParticles;
import com.github.fossil.compat.alexsmobs.AlexsMobsCompat;
import com.github.fossil.compat.farmers.FarmersDelightAddonCompat;
import com.github.fossil.compat.farmers.FarmersDelightCompat;
import com.github.fossil.compat.fintastic.FintasticCompat;
import com.github.fossil.compat.rainbowreef.RainbowReefCompat;
import com.github.fossil.data.DataGenerators;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.food.FossilFoodMappings;
import com.github.fossil.inventory.ModMenus;
import com.github.fossil.item.ModItems;
import com.github.fossil.item.ModTabs;
import com.github.fossil.loot.ModLootItemFunctionTypes;
import com.github.fossil.recipe.ModRecipes;
import com.github.fossil.material.ModArmorMaterials;
import com.github.fossil.material.ModFluidTypes;
import com.github.fossil.material.ModFluids;
import com.github.fossil.sounds.ModSounds;
import com.github.fossil.villager.ModVillagers;
import com.github.fossil.world.biome.FossilRegion;
import com.github.fossil.world.chunk.AnuLairChunkGenerator;
import com.github.fossil.world.chunk.TreasureChunkGenerator;
import com.github.fossil.world.feature.ModFeatures;
import com.github.fossil.world.feature.placement.ModBiomeModifiers;
import com.github.fossil.world.feature.structures.ModStructureType;
import com.github.fossil.world.surfacerules.ModSurfaceRules;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(FossilsAndArcheologyRevival.MODID)
public class FossilsAndArcheologyRevival {
    public static final String MODID = "fossil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public FossilsAndArcheologyRevival(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModTabs.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);
        ModMenus.register(modEventBus);
        ModLootItemFunctionTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModTriggers.register(modEventBus);
        ModVillagers.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModBiomeModifiers.register(modEventBus);
        DataGenerators.register(modEventBus);

        // StructureType/ChunkGenerator are "simple" code registries (net.minecraft.core.registries.BuiltInRegistries),
        // not data-driven ones - NeoForge only unfreezes them for the duration of RegisterEvent's dispatch for
        // that specific registry key (frozen everywhere else, including at mod-construction time and inside
        // FMLCommonSetupEvent#enqueueWork like the TerraBlender/surface-rule calls below - both throw
        // "Registry is already frozen" if tried there).
        modEventBus.addListener((RegisterEvent event) -> {
            event.register(Registries.STRUCTURE_TYPE, helper -> {
                helper.register(location("anu_castle"), ModStructureType.ANU_CASTLE);
                helper.register(location("hell_boat"), ModStructureType.HELL_BOAT);
                helper.register(location("treasure_room"), ModStructureType.TREASURE_ROOM);
            });
            event.register(Registries.CHUNK_GENERATOR, helper -> {
                helper.register(location("treasure_room"), TreasureChunkGenerator.CODEC);
                helper.register(location("anu_lair"), AnuLairChunkGenerator.CODEC);
            });
        });

        modEventBus.addListener((FMLCommonSetupEvent event) -> FlammableRotatedPillarBlock.registerAllStripped());
        modEventBus.addListener((FMLCommonSetupEvent event) -> FossilFoodMappings.register());
        modEventBus.addListener((FMLCommonSetupEvent event) -> ModRecipes.initRecipes());
        modEventBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            Regions.register(new FossilRegion("overworld", RegionType.OVERWORLD, 4));
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MODID, ModSurfaceRules.VOLCANIC_SURFACE_RULE);
        }));

        // Phase 9 compat-tier integrations: all optional, gated on the other mod actually being present.
        modEventBus.addListener((FMLCommonSetupEvent event) -> {
            if (ModList.get().isLoaded("alexsmobs")) {
                AlexsMobsCompat.register();
            }
            if (ModList.get().isLoaded("farmersdelight")) {
                FarmersDelightCompat.registerFoodMappings();
            }
            FarmersDelightAddonCompat.register();
            if (ModList.get().isLoaded("fintastic")) {
                FintasticCompat.register();
            }
            if (ModList.get().isLoaded("rainbow_reef")) {
                RainbowReefCompat.register();
            }
        });
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> FarmersDelightCompat.removeConflictingRecipes(event.getServer()));
    }
}
