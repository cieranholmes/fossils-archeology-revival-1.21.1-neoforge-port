package com.github.fossil.client;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.client.gui.AnalyzerScreen;
import com.github.fossil.client.gui.CultureVatScreen;
import com.github.fossil.client.gui.FeederScreen;
import com.github.fossil.client.gui.SifterScreen;
import com.github.fossil.client.gui.WorktableScreen;
import com.github.fossil.client.gui.debug.DebugScreen;
import com.github.fossil.client.gui.debug.PathingScreen;
import com.github.fossil.client.gui.debug.navigation.DebugCenteredPathNavigation;
import com.github.fossil.client.gui.debug.navigation.PathingDebug;
import com.github.fossil.client.gui.debug.navigation.PathingRenderer;
import com.github.fossil.client.gui.debug.navigation.PlayerPathNavigation;
import com.github.fossil.client.gui.debug.navigation.SweepPathNavigation;
import com.github.fossil.client.gui.debug.navigation.WaterPathNavigation;
import com.github.fossil.client.model.block.PlantBlockModel;
import com.github.fossil.client.model.block.PlantModelLoader;
import com.github.fossil.client.particle.BubbleParticle;
import com.github.fossil.client.particle.LaserParticle;
import com.github.fossil.client.particle.ModParticles;
import com.github.fossil.client.particle.TarBubbleParticle;
import com.github.fossil.client.particle.VolcanoVentAshEmitterParticle;
import com.github.fossil.client.particle.VolcanoVentAshParticle;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.PrehistoricPlantInfo;
import com.github.fossil.block.entity.ModBlockEntities;
import com.github.fossil.material.ModFluidTypes;
import com.github.fossil.client.renderer.blockentity.AncientChestRenderer;
import com.github.fossil.client.renderer.blockentity.AnuBarrierRenderer;
import com.github.fossil.client.renderer.blockentity.AnuStatueRenderer;
import com.github.fossil.client.renderer.blockentity.AnubiteStatueRenderer;
import com.github.fossil.client.renderer.blockentity.CultureVatRenderer;
import com.github.fossil.client.renderer.blockentity.SarcophagusRenderer;
import com.github.fossil.client.renderer.entity.AnuBossRenderer;
import com.github.fossil.client.renderer.entity.AnuDeadRenderer;
import com.github.fossil.client.renderer.entity.AnuTotemRenderer;
import com.github.fossil.client.renderer.entity.AnubiteRenderer;
import com.github.fossil.client.renderer.entity.DinosaurEggRenderer;
import com.github.fossil.client.renderer.entity.FailuresaurusRenderer;
import com.github.fossil.client.renderer.entity.FriendlyPiglinRenderer;
import com.github.fossil.client.renderer.entity.JavelinRenderer;
import com.github.fossil.client.renderer.entity.LaserPointRenderer;
import com.github.fossil.client.renderer.entity.MeganeuraRenderer;
import com.github.fossil.client.renderer.entity.PrehistoricFishGeoRenderer;
import com.github.fossil.client.renderer.entity.PrehistoricGeoRenderer;
import com.github.fossil.client.renderer.entity.QuaggaRenderer;
import com.github.fossil.client.renderer.entity.SentryPiglinRenderer;
import com.github.fossil.client.renderer.entity.SkeletonRenderer;
import com.github.fossil.client.renderer.entity.StoneTabletRenderer;
import com.github.fossil.client.renderer.entity.TarSlimeRenderer;
import com.github.fossil.client.renderer.entity.ToyBallRenderer;
import com.github.fossil.client.renderer.entity.ToyScratchingPostRenderer;
import com.github.fossil.client.renderer.entity.ToyTetheredLogRenderer;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.entity.animation.ClientAnimationInfoLoader;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricFish;
import com.github.fossil.inventory.ModMenus;
import com.github.fossil.util.Version;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    public static KeyMapping debugScreenKey;
    public static KeyMapping pathingScreenKey;
    public static KeyMapping debugRepathKey;
    public static KeyMapping debugAdvanceKey;
    public static KeyMapping debugReverseKey;
    public static KeyMapping debugHelpKey;

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        if (!Version.debugEnabled()) {
            return;
        }
        debugScreenKey = new KeyMapping("key.fossil.debug_screen", InputConstants.Type.KEYSYM, InputConstants.KEY_Y, "category.fossil.debug");
        pathingScreenKey = new KeyMapping("key.fossil.pathing_screen", InputConstants.Type.KEYSYM, InputConstants.KEY_R, "category.fossil.debug");
        debugRepathKey = new KeyMapping("key.fossil.debug_repath", InputConstants.Type.KEYSYM, InputConstants.KEY_V, "category.fossil.debug");
        debugAdvanceKey = new KeyMapping("key.fossil.debug_advance", InputConstants.Type.KEYSYM, InputConstants.KEY_X, "category.fossil.debug");
        debugReverseKey = new KeyMapping("key.fossil.debug_reverse", InputConstants.Type.KEYSYM, InputConstants.KEY_C, "category.fossil.debug");
        debugHelpKey = new KeyMapping("key.fossil.debug_help", InputConstants.Type.KEYSYM, InputConstants.KEY_B, "category.fossil.debug");
        event.register(debugScreenKey);
        event.register(pathingScreenKey);
        event.register(debugRepathKey);
        event.register(debugAdvanceKey);
        event.register(debugReverseKey);
        event.register(debugHelpKey);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        if (!Version.debugEnabled() || Minecraft.getInstance().isPaused()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        while (debugHelpKey.consumeClick()) {
            PathingDebug.showHelpMenu = !PathingDebug.showHelpMenu;
        }
        while (pathingScreenKey.consumeClick()) {
            if (PathingDebug.showHelpMenu) {
                minecraft.setScreen(new PathingScreen());
            }
        }
        while (debugRepathKey.consumeClick()) {
            if (PathingDebug.showHelpMenu) {
                PathingDebug.rePath();
            }
        }
        while (debugAdvanceKey.consumeClick()) {
            if (PathingDebug.showHelpMenu) {
                PathingRenderer.advanceIndex();
            }
        }
        while (debugReverseKey.consumeClick()) {
            if (PathingDebug.showHelpMenu) {
                PathingRenderer.reverseIndex();
            }
        }
        PathingDebug.tick();
        while (debugScreenKey.consumeClick()) {
            Entity entity = DebugScreen.getHitResult(minecraft);
            minecraft.setScreen(new DebugScreen(entity == null ? DebugScreen.entity : entity));
        }
    }

    @SubscribeEvent
    static void onClientPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!Version.debugEnabled()) {
            return;
        }
        PathingDebug.pathNavigation1 = new PlayerPathNavigation(event.getPlayer(), Minecraft.getInstance().level, "Base");
        PathingDebug.pathNavigation3 = new DebugCenteredPathNavigation(event.getPlayer(), Minecraft.getInstance().level);
        PathingDebug.pathNavigation4 = new SweepPathNavigation(event.getPlayer(), Minecraft.getInstance().level);
        PathingDebug.pathNavigation5 = new WaterPathNavigation(event.getPlayer(), Minecraft.getInstance().level);
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANALYZER.get(), AnalyzerScreen::new);
        event.register(ModMenus.SIFTER.get(), SifterScreen::new);
        event.register(ModMenus.CULTURE_VAT.get(), CultureVatScreen::new);
        event.register(ModMenus.WORKTABLE.get(), WorktableScreen::new);
        event.register(ModMenus.FEEDER.get(), FeederScreen::new);
    }

    @SubscribeEvent
    static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientAnimationInfoLoader.INSTANCE);
        event.registerReloadListener(OptionalTextureLoader.INSTANCE);
        event.registerReloadListener(SkeletonGeoModelLoader.INSTANCE);
        event.registerReloadListener(DinopediaBioLoader.INSTANCE);
    }

    @SubscribeEvent
    static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(PlantBlockModel.LOADER, new PlantModelLoader());
    }

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientModEvents::registerBlockRenderTypes);
    }

    private static void registerBlockRenderTypes() {
        for (PrehistoricPlantInfo info : PrehistoricPlantInfo.values()) {
            ItemBlockRenderTypes.setRenderLayer(info.getPlantBlock(), RenderType.cutout());
        }
        for (var vase : ModBlocks.VASES) {
            ItemBlockRenderTypes.setRenderLayer(vase.get(), RenderType.cutout());
        }
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANU_FIGURINE_DESTROYED.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANU_FIGURINE_RESTORED.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANU_FIGURINE_PRISTINE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SKELETON_FIGURINE_RESTORED.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SKELETON_FIGURINE_PRISTINE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CALAMITES_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CALAMITES_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CALAMITES_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CALAMITES_TRAPDOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CORDAITES_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CORDAITES_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CORDAITES_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CORDAITES_TRAPDOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_TRAPDOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_TUMOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.MUTANT_TREE_VINE.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PALM_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PALM_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PALM_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PALM_TRAPDOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGILLARIA_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGILLARIA_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGILLARIA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGILLARIA_TRAPDOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPSKYA_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPSKYA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPSKYA_TOP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPSKYA_LEAF.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPSKYA_TRAPDOOR.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SLIME_TRAIL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_GLASS.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.REINFORCED_GLASS.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CULTURE_VAT.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANU_PORTAL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AMBER_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AMBER_CHUNK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AMBER_CHUNK_DOMINICAN.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.AMBER_CHUNK_MOSQUITO.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SHELL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COMFY_BED.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.LARGE_CHAIN_FENCE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SMALL_CHAIN_FENCE.get(), RenderType.cutout());
        // NOTE: source also registers FERNS (cutout), OBSIDIAN_SPIKES (cutout), and HOME_PORTAL (translucent) here,
        // but none of those three blocks are registered in this repo's ModBlocks yet - see the pre-existing
        // "Phase 1/Blocks catch-up" comments in ModItems/ModBlockStateProvider. Revisit once they're added.
    }

    @SubscribeEvent
    static void registerClientExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            @Override
            public net.minecraft.resources.ResourceLocation getStillTexture() {
                return FossilsAndArcheologyRevival.location("block/tar_still");
            }

            @Override
            public net.minecraft.resources.ResourceLocation getFlowingTexture() {
                return FossilsAndArcheologyRevival.location("block/tar_flowing");
            }

            @Override
            public net.minecraft.resources.ResourceLocation getOverlayTexture() {
                return FossilsAndArcheologyRevival.location("block/tar_still");
            }
        }, ModFluidTypes.TAR_TYPE.get());

        // These items' models are "builtin/entity" (no baked quads of their own - vanilla's chest/bed/
        // shulker-box pattern), so without a registered custom renderer they're not a missing-texture
        // placeholder, they're just invisible.
        event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return com.github.fossil.client.renderer.CustomItemRenderer.INSTANCE;
            }
        }, ModBlocks.ANU_STATUE.get().asItem(),
                ModBlocks.ANUBITE_STATUE.get().asItem(),
                ModBlocks.ANCIENT_CHEST.get().asItem(),
                ModBlocks.SARCOPHAGUS.get().asItem());
    }

    @SubscribeEvent
    static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.VOLCANO_VENT_ASH.get(), VolcanoVentAshParticle.Provider::new);
        event.registerSpecial(ModParticles.VOLCANO_VENT_ASH_EMITTER.get(), new VolcanoVentAshEmitterParticle.Provider());
        event.registerSpriteSet(ModParticles.BUBBLE.get(), BubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.TAR_BUBBLE.get(), TarBubbleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.LASER_PARTICLE.get(), LaserParticle.Provider::new);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        registerFish(event, ModEntities.ALLIGATOR_GAR, "alligator_gar");
        registerDino(event, ModEntities.ALLOSAURUS, "allosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.ANKYLOSAURUS, "ankylosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.AQUILOLAMNA, "aquilolamna", RenderType::entityCutout);
        registerDino(event, ModEntities.ARTHROPLEURA, "arthropleura");
        registerDino(event, ModEntities.BRACHIOSAURUS, "brachiosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.CERATOSAURUS, "ceratosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.CITIPATI, "citipati");
        registerFish(event, ModEntities.COELACANTH, "coelacanth");
        registerDino(event, ModEntities.COMPSOGNATHUS, "compsognathus");
        registerDino(event, ModEntities.CONFUCIUSORNIS, "confuciusornis");
        registerDino(event, ModEntities.CRASSIGYRINUS, "crassigyrinus");
        registerDino(event, ModEntities.DEINONYCHUS, "deinonychus", RenderType::entityCutout);
        registerDino(event, ModEntities.DILOPHOSAURUS, "dilophosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.DIMETRODON, "dimetrodon", RenderType::entityCutout);
        registerDino(event, ModEntities.DIMORPHODON, "dimorphodon");
        registerDino(event, ModEntities.DIPLOCAULUS, "diplocaulus");
        registerDino(event, ModEntities.DIPLODOCUS, "diplodocus", RenderType::entityCutout);
        registerDino(event, ModEntities.DODO, "dodo");
        registerDino(event, ModEntities.DRYOSAURUS, "dryosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.EDAPHOSAURUS, "edaphosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.ELASMOTHERIUM, "elasmotherium");
        registerDino(event, ModEntities.GALLIMIMUS, "gallimimus");
        registerDino(event, ModEntities.GASTORNIS, "gastornis");
        registerDino(event, ModEntities.HENODUS, "henodus");
        registerDino(event, ModEntities.ICHTHYOSAURUS, "ichthyosaurus");
        registerDino(event, ModEntities.KELENKEN, "kelenken");
        registerDino(event, ModEntities.LIOPLEURODON, "liopleurodon");
        registerDino(event, ModEntities.MAMMOTH, "mammoth");
        registerDino(event, ModEntities.MEGALANIA, "megalania");
        registerDino(event, ModEntities.MEGALOCEROS, "megaloceros");
        registerDino(event, ModEntities.MEGALODON, "megalodon");
        registerDino(event, ModEntities.MEGALOGRAPTUS, "megalograptus");
        event.registerEntityRenderer(ModEntities.MEGANEURA.get(), MeganeuraRenderer::new);
        registerDino(event, ModEntities.MOSASAURUS, "mosasaurus", RenderType::entityCutout);
        registerFish(event, ModEntities.NAUTILUS, "nautilus");
        registerDino(event, ModEntities.ORNITHOLESTES, "ornitholestes");
        registerDino(event, ModEntities.PACHYCEPHALOSAURUS, "pachycephalosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.PACHYRHINOSAURUS, "pachyrhinosaurus");
        registerDino(event, ModEntities.PARASAUROLOPHUS, "parasaurolophus", RenderType::entityCutout);
        registerDino(event, ModEntities.PHORUSRHACOS, "phorusrhacos");
        registerDino(event, ModEntities.PLATYBELODON, "platybelodon");
        registerDino(event, ModEntities.PLESIOSAURUS, "plesiosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.POSTOSUCHUS, "postosuchus");
        registerDino(event, ModEntities.PROTOCERATOPS, "protoceratops", RenderType::entityCutout);
        registerDino(event, ModEntities.PSITTACOSAURUS, "psittacosaurus");
        registerDino(event, ModEntities.PTERANODON, "pteranodon");
        event.registerEntityRenderer(ModEntities.QUAGGA.get(), QuaggaRenderer::new);
        registerDino(event, ModEntities.QUETZALCOATLUS, "quetzalcoatlus");
        registerDino(event, ModEntities.SARCOSUCHUS, "sarcosuchus");
        registerDino(event, ModEntities.SMILODON, "smilodon");
        registerDino(event, ModEntities.SPINOSAURUS, "spinosaurus");
        registerDino(event, ModEntities.STEGOSAURUS, "stegosaurus", RenderType::entityCutout);
        registerFish(event, ModEntities.STURGEON, "sturgeon");
        registerDino(event, ModEntities.THERIZINOSAURUS, "therizinosaurus");
        registerDino(event, ModEntities.TIKTAALIK, "tiktaalik");
        registerDino(event, ModEntities.TITANIS, "titanis");
        registerDino(event, ModEntities.TRICERATOPS, "triceratops", RenderType::entityCutout);
        registerDino(event, ModEntities.TYRANNOSAURUS, "tyrannosaurus", RenderType::entityCutout);
        registerDino(event, ModEntities.VELOCIRAPTOR, "velociraptor", RenderType::entityCutout);
        registerTrilobite(event, ModEntities.DICRANURUS);
        registerTrilobite(event, ModEntities.LONCHODOMAS);
        registerTrilobite(event, ModEntities.SCOTOHARPES);
        registerTrilobite(event, ModEntities.WALLISEROPS);
        event.registerEntityRenderer(ModEntities.DINOSAUR_EGG.get(), DinosaurEggRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_BIRD_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.LASER_POINT.get(), LaserPointRenderer::new);
        event.registerEntityRenderer(ModEntities.ANUBITE.get(), AnubiteRenderer::new);
        event.registerEntityRenderer(ModEntities.ANU_BOSS.get(), AnuBossRenderer::new);
        event.registerEntityRenderer(ModEntities.ANU_DEAD.get(), AnuDeadRenderer::new);
        event.registerEntityRenderer(ModEntities.ANU_TOTEM.get(), AnuTotemRenderer::new);
        event.registerEntityRenderer(ModEntities.FAILURESAURUS.get(), FailuresaurusRenderer::new);
        event.registerEntityRenderer(ModEntities.SENTRY_PIGLIN.get(), SentryPiglinRenderer::new);
        event.registerEntityRenderer(ModEntities.STONE_TABLET.get(), StoneTabletRenderer::new);
        event.registerEntityRenderer(ModEntities.TAR_SLIME.get(), TarSlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.TOY_BALL.get(), ToyBallRenderer::new);
        event.registerEntityRenderer(ModEntities.TOY_TETHERED_LOG.get(), ToyTetheredLogRenderer::new);
        event.registerEntityRenderer(ModEntities.TOY_SCRATCHING_POST.get(), ToyScratchingPostRenderer::new);
        event.registerEntityRenderer(ModEntities.JAVELIN.get(), JavelinRenderer::new);
        event.registerEntityRenderer(ModEntities.ANCIENT_LIGHTNING_BOLT.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(ModEntities.FRIENDLY_PIGLIN.get(), FriendlyPiglinRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETON.get(), SkeletonRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.ANU_STATUE.get(), AnuStatueRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANUBITE_STATUE.get(), AnubiteStatueRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SARCOPHAGUS.get(), SarcophagusRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CULTURE_VAT.get(), CultureVatRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANCIENT_CHEST.get(), AncientChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANU_BARRIER.get(), AnuBarrierRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.COMFY_BED.get(), net.minecraft.client.renderer.blockentity.BedRenderer::new);
    }

    private static <T extends Prehistoric> void registerTrilobite(EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> type) {
        event.registerEntityRenderer(type.get(), context -> new PrehistoricGeoRenderer<>(context, "trilobite", RenderType::entityCutoutNoCull));
    }

    private static <T extends Prehistoric> void registerDino(EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> type, String name, Function<ResourceLocation, RenderType> renderType) {
        event.registerEntityRenderer(type.get(), context -> new PrehistoricGeoRenderer<>(context, name, renderType));
    }

    private static <T extends Prehistoric> void registerDino(EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> type, String name) {
        registerDino(event, type, name, RenderType::entityCutoutNoCull);
    }

    private static <T extends PrehistoricFish> void registerFish(EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> type, String name) {
        event.registerEntityRenderer(type.get(), context -> new PrehistoricFishGeoRenderer<>(context, name));
    }
}
