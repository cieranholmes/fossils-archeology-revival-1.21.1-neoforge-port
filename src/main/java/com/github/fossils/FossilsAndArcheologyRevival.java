package com.github.fossils;

import com.github.fossils.advancements.ModTriggers;
import com.github.fossils.block.ModBlocks;
import com.github.fossils.block.custom_blocks.FlammableRotatedPillarBlock;
import com.github.fossils.block.entity.ModBlockEntities;
import com.github.fossils.capabilities.ModAttachments;
import com.github.fossils.client.particle.ModParticles;
import com.github.fossils.entity.ModEntities;
import com.github.fossils.food.FossilFoodMappings;
import com.github.fossils.inventory.ModMenus;
import com.github.fossils.item.ModItems;
import com.github.fossils.item.ModTabs;
import com.github.fossils.loot.ModLootItemFunctionTypes;
import com.github.fossils.material.ModArmorMaterials;
import com.github.fossils.material.ModFluidTypes;
import com.github.fossils.material.ModFluids;
import com.github.fossils.sounds.ModSounds;
import com.github.fossils.villager.ModVillagers;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(FossilsAndArcheologyRevival.MODID)
public class FossilsAndArcheologyRevival {
    public static final String MODID = "fossils";
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
        ModTriggers.register(modEventBus);
        ModVillagers.register(modEventBus);
        ModAttachments.register(modEventBus);

        modEventBus.addListener((FMLCommonSetupEvent event) -> FlammableRotatedPillarBlock.registerAllStripped());
        modEventBus.addListener((FMLCommonSetupEvent event) -> FossilFoodMappings.register());
    }
}
