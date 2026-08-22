package com.github.fossil.entity;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.Allosaurus;
import com.github.fossil.entity.prehistoric.Ankylosaurus;
import com.github.fossil.entity.prehistoric.Arthropleura;
import com.github.fossil.entity.prehistoric.Brachiosaurus;
import com.github.fossil.entity.prehistoric.Ceratosaurus;
import com.github.fossil.entity.prehistoric.Citipati;
import com.github.fossil.entity.prehistoric.Compsognathus;
import com.github.fossil.entity.prehistoric.Deinonychus;
import com.github.fossil.entity.prehistoric.Dilophosaurus;
import com.github.fossil.entity.prehistoric.Dimetrodon;
import com.github.fossil.entity.prehistoric.Diplodocus;
import com.github.fossil.entity.prehistoric.Dodo;
import com.github.fossil.entity.prehistoric.Dryosaurus;
import com.github.fossil.entity.prehistoric.Edaphosaurus;
import com.github.fossil.entity.prehistoric.Elasmotherium;
import com.github.fossil.entity.prehistoric.Gallimimus;
import com.github.fossil.entity.prehistoric.Gastornis;
import com.github.fossil.entity.prehistoric.Kelenken;
import com.github.fossil.entity.prehistoric.Mammoth;
import com.github.fossil.entity.prehistoric.Megalania;
import com.github.fossil.entity.prehistoric.Megaloceros;
import com.github.fossil.entity.prehistoric.Ornitholestes;
import com.github.fossil.entity.prehistoric.Pachycephalosaurus;
import com.github.fossil.entity.prehistoric.Pachyrhinosaurus;
import com.github.fossil.entity.prehistoric.Parasaurolophus;
import com.github.fossil.entity.prehistoric.Phorusrhacos;
import com.github.fossil.entity.prehistoric.Platybelodon;
import com.github.fossil.entity.prehistoric.Postosuchus;
import com.github.fossil.entity.prehistoric.Protoceratops;
import com.github.fossil.entity.prehistoric.Psittacosaurus;
import com.github.fossil.entity.prehistoric.Smilodon;
import com.github.fossil.entity.prehistoric.Stegosaurus;
import com.github.fossil.entity.prehistoric.Therizinosaurus;
import com.github.fossil.entity.prehistoric.Titanis;
import com.github.fossil.entity.prehistoric.Triceratops;
import com.github.fossil.entity.prehistoric.Tyrannosaurus;
import com.github.fossil.entity.prehistoric.Velociraptor;
import com.github.fossil.entity.prehistoric.base.DinosaurEgg;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricFish;
import com.github.fossil.entity.prehistoric.base.PrehistoricFlying;
import com.github.fossil.entity.prehistoric.fish.AlligatorGar;
import com.github.fossil.entity.prehistoric.fish.Coelacanth;
import com.github.fossil.entity.prehistoric.fish.Nautilus;
import com.github.fossil.entity.prehistoric.fish.Sturgeon;
import com.github.fossil.entity.prehistoric.flying.Confuciusornis;
import com.github.fossil.entity.prehistoric.flying.Dimorphodon;
import com.github.fossil.entity.prehistoric.flying.Pteranodon;
import com.github.fossil.entity.prehistoric.flying.Quetzalcoatlus;
import com.github.fossil.entity.prehistoric.swimming.Aquilolamna;
import com.github.fossil.entity.prehistoric.swimming.Crassigyrinus;
import com.github.fossil.entity.prehistoric.swimming.Dicranurus;
import com.github.fossil.entity.prehistoric.swimming.Diplocaulus;
import com.github.fossil.entity.prehistoric.swimming.Henodus;
import com.github.fossil.entity.prehistoric.swimming.Ichthyosaurus;
import com.github.fossil.entity.prehistoric.swimming.Liopleurodon;
import com.github.fossil.entity.prehistoric.swimming.Lonchodomas;
import com.github.fossil.entity.prehistoric.swimming.Megalodon;
import com.github.fossil.entity.prehistoric.swimming.Megalograptus;
import com.github.fossil.entity.prehistoric.swimming.Meganeura;
import com.github.fossil.entity.prehistoric.swimming.Mosasaurus;
import com.github.fossil.entity.prehistoric.swimming.Plesiosaurus;
import com.github.fossil.entity.prehistoric.swimming.Sarcosuchus;
import com.github.fossil.entity.prehistoric.swimming.Scotoharpes;
import com.github.fossil.entity.prehistoric.swimming.Spinosaurus;
import com.github.fossil.entity.prehistoric.swimming.Tiktaalik;
import com.github.fossil.entity.prehistoric.swimming.Walliserops;
import com.github.fossil.entity.monster.AnuBoss;
import com.github.fossil.entity.monster.Anubite;
import com.github.fossil.entity.monster.Failuresaurus;
import com.github.fossil.entity.monster.FriendlyPiglin;
import com.github.fossil.entity.monster.SentryPiglin;
import com.github.fossil.entity.monster.TarSlime;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

/**
 * All 61 species (Phase 4 task #7) now have real entity classes; every species is registered directly with its
 * real class.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AlligatorGar>> ALLIGATOR_GAR = registerFish("alligator_gar", AlligatorGar::new, 2, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Allosaurus>> ALLOSAURUS = registerDino("allosaurus", Allosaurus::new, 1.1f, 1.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Ankylosaurus>> ANKYLOSAURUS = registerDino("ankylosaurus", Ankylosaurus::new, 1.6f, 1.4f);
    public static final DeferredHolder<EntityType<?>, EntityType<Aquilolamna>> AQUILOLAMNA = registerAquatic("aquilolamna", Aquilolamna::new, 1.3f, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Arthropleura>> ARTHROPLEURA = registerDino("arthropleura", Arthropleura::new, 1, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Brachiosaurus>> BRACHIOSAURUS = registerDino("brachiosaurus", Brachiosaurus::new, 0.95f, 1.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Ceratosaurus>> CERATOSAURUS = registerDino("ceratosaurus", Ceratosaurus::new, 0.65f, 1.2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Citipati>> CITIPATI = registerDino("citipati", Citipati::new, 1.15f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Coelacanth>> COELACANTH = registerFish("coelacanth", Coelacanth::new, 2, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Compsognathus>> COMPSOGNATHUS = registerDino("compsognathus", Compsognathus::new, 1.1f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<Confuciusornis>> CONFUCIUSORNIS = registerDino("confuciusornis", Confuciusornis::new, 2, 1f);
    public static final DeferredHolder<EntityType<?>, EntityType<Crassigyrinus>> CRASSIGYRINUS = registerAquatic("crassigyrinus", Crassigyrinus::new, 1.5f, 0.9f);
    public static final DeferredHolder<EntityType<?>, EntityType<Deinonychus>> DEINONYCHUS = registerDino("deinonychus", Deinonychus::new, 1.2f, 1.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Dilophosaurus>> DILOPHOSAURUS = registerDino("dilophosaurus", Dilophosaurus::new, 1.25f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Dimetrodon>> DIMETRODON = registerDino("dimetrodon", Dimetrodon::new, 1.3f, 1.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Dimorphodon>> DIMORPHODON = registerDino("dimorphodon", Dimorphodon::new, 1.2f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<Diplocaulus>> DIPLOCAULUS = registerAquatic("diplocaulus", Diplocaulus::new, 1.5f, 0.9f);
    public static final DeferredHolder<EntityType<?>, EntityType<Diplodocus>> DIPLODOCUS = registerDino("diplodocus", Diplodocus::new, 1.85f, 1.85f);
    public static final DeferredHolder<EntityType<?>, EntityType<Dodo>> DODO = registerDino("dodo", Dodo::new, 1, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Dryosaurus>> DRYOSAURUS = registerDino("dryosaurus", Dryosaurus::new, 1.3f, 1.2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Edaphosaurus>> EDAPHOSAURUS = registerDino("edaphosaurus", Edaphosaurus::new, 1.4f, 1.4f);
    public static final DeferredHolder<EntityType<?>, EntityType<Elasmotherium>> ELASMOTHERIUM = registerDino("elasmotherium", Elasmotherium::new, 1.5f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<Gallimimus>> GALLIMIMUS = registerDino("gallimimus", Gallimimus::new, 1.2f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Gastornis>> GASTORNIS = registerDino("gastornis", Gastornis::new, 1.4f, 2);
    public static final DeferredHolder<EntityType<?>, EntityType<Henodus>> HENODUS = registerAquatic("henodus", Henodus::new, 1.1f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<Ichthyosaurus>> ICHTHYOSAURUS = registerAquatic("ichthyosaurus", Ichthyosaurus::new, 1.2f, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Kelenken>> KELENKEN = registerDino("kelenken", Kelenken::new, 1.4f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Liopleurodon>> LIOPLEURODON = registerAquatic("liopleurodon", Liopleurodon::new, 2.25f, 0.7f);
    public static final DeferredHolder<EntityType<?>, EntityType<Mammoth>> MAMMOTH = registerDino("mammoth", Mammoth::new, 1.2f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Megalania>> MEGALANIA = registerDino("megalania", Megalania::new, 1.6f, 0.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<Megaloceros>> MEGALOCEROS = registerDino("megaloceros", Megaloceros::new, 1f, 1.7f);
    public static final DeferredHolder<EntityType<?>, EntityType<Megalodon>> MEGALODON = registerAquatic("megalodon", Megalodon::new, 2f, 1.1f);
    public static final DeferredHolder<EntityType<?>, EntityType<Megalograptus>> MEGALOGRAPTUS = registerDino("megalograptus", Megalograptus::new, 1.8f, 0.75f);
    public static final DeferredHolder<EntityType<?>, EntityType<Meganeura>> MEGANEURA = registerDino("meganeura", Meganeura::new, 2.5f, 1.25f);
    public static final DeferredHolder<EntityType<?>, EntityType<Mosasaurus>> MOSASAURUS = registerAquatic("mosasaurus", Mosasaurus::new, 1.3f, 0.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<Nautilus>> NAUTILUS = registerFish("nautilus", Nautilus::new, 0.8f, 0.795f);
    public static final DeferredHolder<EntityType<?>, EntityType<Ornitholestes>> ORNITHOLESTES = registerDino("ornitholestes", Ornitholestes::new, 2.25f, 1.9f);
    public static final DeferredHolder<EntityType<?>, EntityType<Pachycephalosaurus>> PACHYCEPHALOSAURUS = registerDino("pachycephalosaurus", Pachycephalosaurus::new, 1, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Pachyrhinosaurus>> PACHYRHINOSAURUS = registerDino("pachyrhinosaurus", Pachyrhinosaurus::new, 1.4f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Parasaurolophus>> PARASAUROLOPHUS = registerDino("parasaurolophus", Parasaurolophus::new, 1.3f, 1.7f);
    public static final DeferredHolder<EntityType<?>, EntityType<Phorusrhacos>> PHORUSRHACOS = registerDino("phorusrhacos", Phorusrhacos::new, 1.2f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Platybelodon>> PLATYBELODON = registerDino("platybelodon", Platybelodon::new, 1.5f, 1.9f);
    public static final DeferredHolder<EntityType<?>, EntityType<Plesiosaurus>> PLESIOSAURUS = registerAquatic("plesiosaurus", Plesiosaurus::new, 1, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Postosuchus>> POSTOSUCHUS = registerDino("postosuchus", Postosuchus::new, 0.7f, 1f);
    public static final DeferredHolder<EntityType<?>, EntityType<Protoceratops>> PROTOCERATOPS = registerDino("protoceratops", Protoceratops::new, 1.5f, 1.25f);
    public static final DeferredHolder<EntityType<?>, EntityType<Psittacosaurus>> PSITTACOSAURUS = registerDino("psittacosaurus", Psittacosaurus::new, 1.5f, 1.25f);
    public static final DeferredHolder<EntityType<?>, EntityType<Pteranodon>> PTERANODON = registerDino("pteranodon", Pteranodon::new, 0.7f, 1f);
    public static final DeferredHolder<EntityType<?>, EntityType<Quagga>> QUAGGA = registerDino("quagga", Quagga::new, 1.3964844f, 1.6f);
    public static final DeferredHolder<EntityType<?>, EntityType<Quetzalcoatlus>> QUETZALCOATLUS = registerDino("quetzalcoatlus", Quetzalcoatlus::new, 1f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Sarcosuchus>> SARCOSUCHUS = registerDino("sarcosuchus", Sarcosuchus::new, 2, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Smilodon>> SMILODON = registerDino("smilodon", Smilodon::new, 1.2f, 1.4f);
    public static final DeferredHolder<EntityType<?>, EntityType<Spinosaurus>> SPINOSAURUS = registerDino("spinosaurus", Spinosaurus::new, 1.1f, 1.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Stegosaurus>> STEGOSAURUS = registerDino("stegosaurus", Stegosaurus::new, 1.4f, 1.8f);
    public static final DeferredHolder<EntityType<?>, EntityType<Sturgeon>> STURGEON = registerFish("sturgeon", Sturgeon::new, 1.9f, 0.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Therizinosaurus>> THERIZINOSAURUS = registerDino("therizinosaurus", Therizinosaurus::new, 1.1f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Tiktaalik>> TIKTAALIK = registerAquatic("tiktaalik", Tiktaalik::new, 1.7f, 0.4f);
    public static final DeferredHolder<EntityType<?>, EntityType<Titanis>> TITANIS = registerDino("titanis", Titanis::new, 1.3f, 2f);
    public static final DeferredHolder<EntityType<?>, EntityType<Triceratops>> TRICERATOPS = registerDino("triceratops", Triceratops::new, 0.8f, 1);
    public static final DeferredHolder<EntityType<?>, EntityType<Tyrannosaurus>> TYRANNOSAURUS = registerDino("tyrannosaurus", Tyrannosaurus::new, 1.1f, 1.25f);
    public static final DeferredHolder<EntityType<?>, EntityType<Velociraptor>> VELOCIRAPTOR = registerDino("velociraptor", Velociraptor::new, 1.6f, 1.5f);
    public static final DeferredHolder<EntityType<?>, EntityType<Dicranurus>> DICRANURUS = registerDino("dicranurus", Dicranurus::new, 0.8f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Lonchodomas>> LONCHODOMAS = registerDino("lonchodomas", Lonchodomas::new, 0.8f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Scotoharpes>> SCOTOHARPES = registerDino("scotoharpes", Scotoharpes::new, 0.8f, 0.3f);
    public static final DeferredHolder<EntityType<?>, EntityType<Walliserops>> WALLISEROPS = registerDino("walliserops", Walliserops::new, 0.8f, 0.3f);

    public static final DeferredHolder<EntityType<?>, EntityType<DinosaurEgg>> DINOSAUR_EGG = ENTITIES.register(
            "dinosaur_egg", () -> EntityType.Builder.of(DinosaurEgg::new, MobCategory.CREATURE).sized(0.5F, 0.6F).build("dinosaur_egg"));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownBirdEgg>> THROWN_BIRD_EGG = registerMisc("thrown_bird_egg", ThrownBirdEgg::new, 0.25f, 0.25f, 4, 10);

    public static final DeferredHolder<EntityType<?>, EntityType<ToyBall>> TOY_BALL = registerMisc("toy_ball", ToyBall::new, 0.5f, 0.5f, 5, 3);
    public static final DeferredHolder<EntityType<?>, EntityType<ToyTetheredLog>> TOY_TETHERED_LOG = registerMisc("toy_tethered_log", ToyTetheredLog::new, 0.6f, 1.9375f, 5, 20);
    public static final DeferredHolder<EntityType<?>, EntityType<ToyScratchingPost>> TOY_SCRATCHING_POST = registerMisc("toy_scratching_post", ToyScratchingPost::new, 0.6f, 2, 5, 20);

    public static final DeferredHolder<EntityType<?>, EntityType<Failuresaurus>> FAILURESAURUS = ENTITIES.register("failuresaurus",
            () -> EntityType.Builder.of(Failuresaurus::new, MobCategory.MONSTER).sized(0.9f, 1).build("failuresaurus"));
    public static final DeferredHolder<EntityType<?>, EntityType<TarSlime>> TAR_SLIME = ENTITIES.register("tar_slime",
            () -> EntityType.Builder.of(TarSlime::new, MobCategory.MONSTER).sized(2.04f, 2.04f).build("tar_slime"));
    public static final DeferredHolder<EntityType<?>, EntityType<Anubite>> ANUBITE = ENTITIES.register("anubite",
            () -> EntityType.Builder.of(Anubite::new, MobCategory.MONSTER).sized(1, 2.3f).fireImmune().build("anubite"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnuBoss>> ANU_BOSS = ENTITIES.register("anu_boss",
            () -> EntityType.Builder.of(AnuBoss::new, MobCategory.MONSTER).sized(1, 1.8f).fireImmune()
                    .clientTrackingRange(64).build("anu_boss"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnuDead>> ANU_DEAD = ENTITIES.register("anu_dead",
            () -> EntityType.Builder.of(AnuDead::new, MobCategory.MONSTER).sized(1.8f, 0.8f).fireImmune().build("anu_dead"));
    public static final DeferredHolder<EntityType<?>, EntityType<AnuTotem>> ANU_TOTEM = registerMisc("anu_totem", AnuTotem::new, 0.9f, 1.8f, 5, 20);
    public static final DeferredHolder<EntityType<?>, EntityType<SentryPiglin>> SENTRY_PIGLIN = ENTITIES.register("sentry_piglin",
            () -> EntityType.Builder.of(SentryPiglin::new, MobCategory.MONSTER).sized(0.8f, 2).fireImmune().build("sentry_piglin"));

    public static final DeferredHolder<EntityType<?>, EntityType<StoneTablet>> STONE_TABLET = registerMisc("stone_tablet", StoneTablet::new, 0.5f, 0.5f, 10, Integer.MAX_VALUE);

    public static final DeferredHolder<EntityType<?>, EntityType<LaserPointEntity>> LASER_POINT = registerMisc("laser_point", LaserPointEntity::new, 0.2f, 0.2f, 10, 3);

    public static final DeferredHolder<EntityType<?>, EntityType<Javelin>> JAVELIN = registerMisc("javelin", Javelin::new, 0.5f, 0.5f, 4, 20);
    public static final DeferredHolder<EntityType<?>, EntityType<AncientLightningBolt>> ANCIENT_LIGHTNING_BOLT = ENTITIES.register("ancient_lightning_bolt",
            () -> EntityType.Builder.of(AncientLightningBolt::new, MobCategory.MISC).sized(0, 0)
                    .clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build("ancient_lightning_bolt"));
    public static final DeferredHolder<EntityType<?>, EntityType<FriendlyPiglin>> FRIENDLY_PIGLIN = ENTITIES.register("friendly_piglin",
            () -> EntityType.Builder.of(FriendlyPiglin::new, MobCategory.MONSTER).sized(0.6f, 1.95f).fireImmune().clientTrackingRange(8)
                    .build("friendly_piglin"));
    public static final DeferredHolder<EntityType<?>, EntityType<PrehistoricSkeleton>> SKELETON = registerMisc("skeleton", PrehistoricSkeleton::new, 1, 1, 10, 3);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerMisc(String name, EntityType.EntityFactory<T> factory, float width, float height, int trackRange, int updateInterval) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(width, height).clientTrackingRange(trackRange).updateInterval(updateInterval).build(name));
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerDino(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, MobCategory.CREATURE).sized(width, height).clientTrackingRange(8).build(name));
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerAquatic(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, MobCategory.WATER_CREATURE).sized(width, height).clientTrackingRange(8).build(name));
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerFish(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(factory, MobCategory.WATER_CREATURE).sized(width, height).clientTrackingRange(8).build(name));
    }

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }

    @SubscribeEvent
    static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(DINOSAUR_EGG.get(), DinosaurEgg.createAttributes().build());
        event.put(ALLIGATOR_GAR.get(), PrehistoricFish.createAttributes().build());
        event.put(COELACANTH.get(), PrehistoricFish.createAttributes().build());
        event.put(NAUTILUS.get(), PrehistoricFish.createAttributes().build());
        event.put(STURGEON.get(), PrehistoricFish.createAttributes().build());
        event.put(CONFUCIUSORNIS.get(), PrehistoricFlying.createAttributes().build());
        event.put(DIMORPHODON.get(), PrehistoricFlying.createAttributes().build());
        event.put(PTERANODON.get(), PrehistoricFlying.createAttributes().build());
        event.put(QUETZALCOATLUS.get(), PrehistoricFlying.createAttributes().build());
        event.put(MOSASAURUS.get(), Mosasaurus.createAttributes().build());
        event.put(QUAGGA.get(), AbstractChestedHorse.createBaseChestedHorseAttributes().build());
        event.put(LASER_POINT.get(), LaserPointEntity.createAttributes().build());
        event.put(ANUBITE.get(), Anubite.createAttributes().build());
        event.put(ANU_BOSS.get(), AnuBoss.createAttributes().build());
        event.put(ANU_DEAD.get(), AnuDead.createAttributes().build());
        event.put(ANU_TOTEM.get(), AnuTotem.createAttributes().build());
        event.put(SENTRY_PIGLIN.get(), SentryPiglin.createAttributes().build());
        event.put(TAR_SLIME.get(), TarSlime.createAttributes().build());
        event.put(FRIENDLY_PIGLIN.get(), FriendlyPiglin.createAttributes().build());
        event.put(FAILURESAURUS.get(), Failuresaurus.createAttributes().build());
        for (var holder : List.of(
                ALLOSAURUS, ANKYLOSAURUS, AQUILOLAMNA, ARTHROPLEURA, BRACHIOSAURUS, CERATOSAURUS, CITIPATI,
                COMPSOGNATHUS, CRASSIGYRINUS, DEINONYCHUS, DILOPHOSAURUS, DIMETRODON,
                DIPLOCAULUS, DIPLODOCUS, DODO, DRYOSAURUS, EDAPHOSAURUS, ELASMOTHERIUM, GALLIMIMUS, GASTORNIS, HENODUS,
                ICHTHYOSAURUS, KELENKEN, LIOPLEURODON, MAMMOTH, MEGALANIA, MEGALOCEROS, MEGALODON, MEGALOGRAPTUS, MEGANEURA,
                ORNITHOLESTES, PACHYCEPHALOSAURUS, PACHYRHINOSAURUS, PARASAUROLOPHUS, PHORUSRHACOS, PLATYBELODON,
                PLESIOSAURUS, POSTOSUCHUS, PROTOCERATOPS, PSITTACOSAURUS, SARCOSUCHUS,
                SMILODON, SPINOSAURUS, STEGOSAURUS, THERIZINOSAURUS, TIKTAALIK, TITANIS, TRICERATOPS, TYRANNOSAURUS, VELOCIRAPTOR,
                DICRANURUS, LONCHODOMAS, SCOTOHARPES, WALLISEROPS
        )) {
            event.put(holder.get(), Prehistoric.createAttributes().build());
        }
    }

    @SubscribeEvent
    static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ALLIGATOR_GAR.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PrehistoricFish::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(NAUTILUS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PrehistoricFish::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(STURGEON.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                PrehistoricFish::canSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(COELACANTH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Coelacanth::canCoelacanthSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}
