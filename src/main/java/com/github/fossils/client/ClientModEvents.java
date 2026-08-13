package com.github.fossils.client;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.client.gui.AnalyzerScreen;
import com.github.fossils.client.gui.CultureVatScreen;
import com.github.fossils.client.gui.FeederScreen;
import com.github.fossils.client.gui.SifterScreen;
import com.github.fossils.client.gui.WorktableScreen;
import com.github.fossils.client.renderer.entity.AnuBossRenderer;
import com.github.fossils.client.renderer.entity.AnuDeadRenderer;
import com.github.fossils.client.renderer.entity.AnuTotemRenderer;
import com.github.fossils.client.renderer.entity.AnubiteRenderer;
import com.github.fossils.client.renderer.entity.DinosaurEggRenderer;
import com.github.fossils.client.renderer.entity.FailuresaurusRenderer;
import com.github.fossils.client.renderer.entity.FriendlyPiglinRenderer;
import com.github.fossils.client.renderer.entity.JavelinRenderer;
import com.github.fossils.client.renderer.entity.LaserPointRenderer;
import com.github.fossils.client.renderer.entity.MeganeuraRenderer;
import com.github.fossils.client.renderer.entity.PrehistoricFishGeoRenderer;
import com.github.fossils.client.renderer.entity.PrehistoricGeoRenderer;
import com.github.fossils.client.renderer.entity.QuaggaRenderer;
import com.github.fossils.client.renderer.entity.SentryPiglinRenderer;
import com.github.fossils.client.renderer.entity.SkeletonRenderer;
import com.github.fossils.client.renderer.entity.StoneTabletRenderer;
import com.github.fossils.client.renderer.entity.TarSlimeRenderer;
import com.github.fossils.client.renderer.entity.ToyBallRenderer;
import com.github.fossils.client.renderer.entity.ToyScratchingPostRenderer;
import com.github.fossils.client.renderer.entity.ToyTetheredLogRenderer;
import com.github.fossils.entity.ModEntities;
import com.github.fossils.entity.animation.ClientAnimationInfoLoader;
import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.entity.prehistoric.base.PrehistoricFish;
import com.github.fossils.inventory.ModMenus;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID, value = Dist.CLIENT)
public class ClientModEvents {

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
