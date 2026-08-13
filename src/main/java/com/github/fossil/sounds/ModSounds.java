package com.github.fossil.sounds;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_BONES = SOUND_EVENTS.register("music_bones",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_bones")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_FIRST_DINOSAUR = SOUND_EVENTS.register("music_first_dinosaur",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_first_dinosaur")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_SCARAB = SOUND_EVENTS.register("music_scarab",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_scarab")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISCOVERY = SOUND_EVENTS.register("music_discovery",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_discovery")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_MATING = SOUND_EVENTS.register("music_mating",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_mating")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ANU_TOTEM = SOUND_EVENTS.register("anu_totem",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("anu_totem")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ANU_COUGH = SOUND_EVENTS.register("anu_cough",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("anu_cough")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ANU_DEATH = SOUND_EVENTS.register("anu_death",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("anu_death")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ANU_LAUGH = SOUND_EVENTS.register("anu_laugh",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("anu_laugh")));
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_ANU = SOUND_EVENTS.register("music_anu",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("music_anu")));
    public static final DeferredHolder<SoundEvent, SoundEvent> DRUM_SINGLE = SOUND_EVENTS.register("drum_single",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("drum_single")));
    public static final DeferredHolder<SoundEvent, SoundEvent> TAR = SOUND_EVENTS.register("tar",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("tar")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WHIP = SOUND_EVENTS.register("whip",
            () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location("whip")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ALLOSAURUS_AMBIENT = registerSound("allosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALLOSAURUS_HURT = registerSound("allosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALLOSAURUS_DEATH = registerSound("allosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANKYLOSAURUS_AMBIENT = registerSound("ankylosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANKYLOSAURUS_HURT = registerSound("ankylosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANKYLOSAURUS_DEATH = registerSound("ankylosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARTHROPLEURA_AMBIENT = registerSound("arthropleura_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARTHROPLEURA_HURT = registerSound("arthropleura_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARTHROPLEURA_DEATH = registerSound("arthropleura_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ARTHROPLEURA_WALK = registerSound("arthropleura_walk");
    public static final DeferredHolder<SoundEvent, SoundEvent> BRACHIOSAURUS_AMBIENT = registerSound("brachiosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> BRACHIOSAURUS_HURT = registerSound("brachiosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BRACHIOSAURUS_DEATH = registerSound("brachiosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CERATOSAURUS_AMBIENT = registerSound("ceratosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> CERATOSAURUS_HURT = registerSound("ceratosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CERATOSAURUS_DEATH = registerSound("ceratosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CITIPATI_AMBIENT = registerSound("citipati_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> CITIPATI_HURT = registerSound("citipati_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CITIPATI_DEATH = registerSound("citipati_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMPSOGNATHUS_AMBIENT = registerSound("compsognathus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMPSOGNATHUS_HURT = registerSound("compsognathus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> COMPSOGNATHUS_DEATH = registerSound("compsognathus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CONFUCIUSORNIS_AMBIENT = registerSound("confuciusornis_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> CONFUCIUSORNIS_HURT = registerSound("confuciusornis_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CONFUCIUSORNIS_DEATH = registerSound("confuciusornis_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEINONYCHUS_AMBIENT = registerSound("deinonychus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEINONYCHUS_HURT = registerSound("deinonychus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEINONYCHUS_DEATH = registerSound("deinonychus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DILOPHOSAURUS_AMBIENT = registerSound("dilophosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DILOPHOSAURUS_HURT = registerSound("dilophosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DILOPHOSAURUS_DEATH = registerSound("dilophosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIPLODOCUS_AMBIENT = registerSound("diplodocus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIPLODOCUS_HURT = registerSound("diplodocus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIPLODOCUS_DEATH = registerSound("diplodocus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DODO_AMBIENT = registerSound("dodo_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DODO_HURT = registerSound("dodo_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DODO_DEATH = registerSound("dodo_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRYOSAURUS_AMBIENT = registerSound("dryosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRYOSAURUS_HURT = registerSound("dryosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DRYOSAURUS_DEATH = registerSound("dryosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> EDAPHOSAURUS_AMBIENT = registerSound("edaphosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> EDAPHOSAURUS_HURT = registerSound("edaphosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> EDAPHOSAURUS_DEATH = registerSound("edaphosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELASMOTHERIUM_AMBIENT = registerSound("elasmotherium_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELASMOTHERIUM_HURT = registerSound("elasmotherium_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELASMOTHERIUM_DEATH = registerSound("elasmotherium_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> GALLIMIMUS_AMBIENT = registerSound("gallimimus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> GALLIMIMUS_HURT = registerSound("gallimimus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> GALLIMIMUS_DEATH = registerSound("gallimimus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> HENODUS_AMBIENT = registerSound("henodus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> HENODUS_HURT = registerSound("henodus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> HENODUS_DEATH = registerSound("henodus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICHTHYOSAURUS_AMBIENT = registerSound("ichthyosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICHTHYOSAURUS_AMBIENT_OUTSIDE = registerSound("ichthyosaurus_outside");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICHTHYOSAURUS_HURT = registerSound("ichthyosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICHTHYOSAURUS_DEATH = registerSound("ichthyosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIOPLEURODON_AMBIENT_OUTSIDE = registerSound("liopleurodon_ambient_outside");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIOPLEURODON_AMBIENT_INSIDE = registerSound("liopleurodon_ambient_inside");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIOPLEURODON_HURT = registerSound("liopleurodon_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIOPLEURODON_DEATH = registerSound("liopleurodon_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMOTH_AMBIENT = registerSound("mammoth_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMOTH_HURT = registerSound("mammoth_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMOTH_DEATH = registerSound("mammoth_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALANIA_AMBIENT = registerSound("megalania_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALANIA_HURT = registerSound("megalania_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALANIA_DEATH = registerSound("megalania_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOCEROS_AMBIENT = registerSound("megaloceros_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOCEROS_HURT = registerSound("megaloceros_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOCEROS_DEATH = registerSound("megaloceros_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALODON_AMBIENT = registerSound("megalodon_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALODON_HURT_OUTSIDE = registerSound("megalodon_hurt_outside");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALODON_HURT = registerSound("megalodon_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALODON_DEATH = registerSound("megalodon_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOGRAPTUS_AMBIENT = registerSound("megalograptus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOGRAPTUS_HURT = registerSound("megalograptus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGALOGRAPTUS_DEATH = registerSound("megalograptus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGANEURA_AMBIENT = registerSound("meganeura_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGANEURA_HURT = registerSound("meganeura_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MEGANEURA_DEATH = registerSound("meganeura_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOSASAURUS_AMBIENT_OUTSIDE = registerSound("mosasaurus_ambient_outside");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOSASAURUS_AMBIENT_INSIDE = registerSound("mosasaurus_ambient_inside");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOSASAURUS_HURT = registerSound("mosasaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOSASAURUS_DEATH = registerSound("mosasaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORNITHOLESTES_AMBIENT = registerSound("ornitholestes_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORNITHOLESTES_HURT = registerSound("ornitholestes_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ORNITHOLESTES_DEATH = registerSound("ornitholestes_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> PACHYCEPHALOSAURUS_AMBIENT = registerSound("pachycephalosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> PACHYCEPHALOSAURUS_HURT = registerSound("pachycephalosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> PACHYCEPHALOSAURUS_DEATH = registerSound("pachycephalosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> PARASAUROLOPHUS_AMBIENT = registerSound("parasaurolophus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> PARASAUROLOPHUS_HURT = registerSound("parasaurolophus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> PARASAUROLOPHUS_DEATH = registerSound("parasaurolophus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLATYBELODON_AMBIENT = registerSound("platybelodon_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLATYBELODON_HURT = registerSound("platybelodon_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLATYBELODON_DEATH = registerSound("platybelodon_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLESIOSAURUS_AMBIENT_OUTSIDE = registerSound("plesiosaurus_ambient_outside");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLESIOSAURUS_AMBIENT_INSIDE = registerSound("plesiosaurus_ambient_inside");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLESIOSAURUS_HURT = registerSound("plesiosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLESIOSAURUS_DEATH = registerSound("plesiosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> PTERANODON_AMBIENT = registerSound("pteranodon_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> PTERANODON_HURT = registerSound("pteranodon_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> PTERANODON_DEATH = registerSound("pteranodon_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAGGA_AMBIENT = registerSound("quagga_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAGGA_HURT = registerSound("quagga_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAGGA_DEATH = registerSound("quagga_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SARCOSUCHUS_BABY_AMBIENT = registerSound("sarcosuchus_baby_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SARCOSUCHUS_AMBIENT = registerSound("sarcosuchus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SARCOSUCHUS_HURT = registerSound("sarcosuchus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SARCOSUCHUS_DEATH = registerSound("sarcosuchus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMILODON_AMBIENT = registerSound("smilodon_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMILODON_HURT = registerSound("smilodon_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMILODON_DEATH = registerSound("smilodon_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPINOSAURUS_AMBIENT = registerSound("spinosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPINOSAURUS_HURT = registerSound("spinosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPINOSAURUS_DEATH = registerSound("spinosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEGOSAURUS_AMBIENT = registerSound("stegosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEGOSAURUS_HURT = registerSound("stegosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> STEGOSAURUS_DEATH = registerSound("stegosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TERROR_BIRD_AMBIENT = registerSound("terror_bird_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> TERROR_BIRD_HURT = registerSound("terror_bird_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TERROR_BIRD_DEATH = registerSound("terror_bird_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> THERIZINOSAURUS_AMBIENT = registerSound("therizinosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> THERIZINOSAURUS_HURT = registerSound("therizinosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> THERIZINOSAURUS_DEATH = registerSound("therizinosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TIKTAALIK_AMBIENT = registerSound("tiktaalik_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> TIKTAALIK_HURT = registerSound("tiktaalik_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TIKTAALIK_DEATH = registerSound("tiktaalik_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRICERATOPS_AMBIENT = registerSound("triceratops_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRICERATOPS_HURT = registerSound("triceratops_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRICERATOPS_DEATH = registerSound("triceratops_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TYRANNOSAURUS_AMBIENT = registerSound("tyrannosaurus_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> TYRANNOSAURUS_HURT = registerSound("tyrannosaurus_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TYRANNOSAURUS_DEATH = registerSound("tyrannosaurus_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TYRANNOSAURUS_WEAK = registerSound("tyrannosaurus_weak");
    public static final DeferredHolder<SoundEvent, SoundEvent> VELOCIRAPTOR_AMBIENT = registerSound("velociraptor_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> VELOCIRAPTOR_HURT = registerSound("velociraptor_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VELOCIRAPTOR_DEATH = registerSound("velociraptor_death");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(FossilsAndArcheologyRevival.location(id)));
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
