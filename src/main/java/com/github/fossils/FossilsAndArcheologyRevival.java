package com.github.fossils;

import com.github.fossils.block.ModBlocks;
import com.github.fossils.block.custom_blocks.FlammableRotatedPillarBlock;
import com.github.fossils.block.entity.ModBlockEntities;
import com.github.fossils.inventory.ModMenus;
import com.github.fossils.item.ModItems;
import com.github.fossils.item.ModTabs;
import com.github.fossils.material.ModFluidTypes;
import com.github.fossils.material.ModFluids;
import com.github.fossils.sounds.ModSounds;
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
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModTabs.register(modEventBus);
        ModSounds.register(modEventBus);
        ModMenus.register(modEventBus);

        modEventBus.addListener((FMLCommonSetupEvent event) -> FlammableRotatedPillarBlock.registerAllStripped());
    }
}
