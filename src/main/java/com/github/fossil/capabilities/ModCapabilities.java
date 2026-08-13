package com.github.fossil.capabilities;

import com.github.fossil.entity.prehistoric.base.EntityInfo;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge Data Attachment-backed replacement for the source mod's Architectury @ExpectPlatform capability system.
 * Client sync for mammal pregnancy is handled automatically by ModAttachments.MAMMAL_DATA's sync handler - no
 * manual sync packet is needed (source's S2CMammalCapMessage/onPlayerStartTracking resync have no equivalent here).
 */
public class ModCapabilities {

    public static boolean hasEmbryo(Animal animal) {
        return getEmbryo(animal) != null;
    }

    public static int getEmbryoProgress(Animal animal) {
        return animal.getData(ModAttachments.MAMMAL_DATA).getEmbryoProgress();
    }

    @Nullable
    public static EntityInfo getEmbryo(Animal animal) {
        return animal.getData(ModAttachments.MAMMAL_DATA).getEmbryo();
    }

    /**
     * Deliberately does not sync - matches source, where the per-tick progress increment runs independently (and
     * silently) on both logical sides in lockstep, rather than the server pushing a packet every single tick.
     */
    public static void setEmbryoProgress(Animal animal, int embryoProgress) {
        animal.getData(ModAttachments.MAMMAL_DATA).setEmbryoProgress(embryoProgress);
    }

    public static void setEmbryo(Animal animal, @Nullable EntityInfo embryo) {
        animal.getData(ModAttachments.MAMMAL_DATA).setEmbryo(embryo);
    }

    public static void startPregnancy(Animal animal, EntityInfo embryo) {
        MammalData data = animal.getData(ModAttachments.MAMMAL_DATA);
        data.setEmbryo(embryo);
        data.setEmbryoProgress(1);
        animal.syncData(ModAttachments.MAMMAL_DATA);
    }

    public static void stopPregnancy(Animal animal) {
        MammalData data = animal.getData(ModAttachments.MAMMAL_DATA);
        data.setEmbryo(null);
        data.setEmbryoProgress(0);
        animal.syncData(ModAttachments.MAMMAL_DATA);
    }

    public static boolean hasHatchedDinosaur(Player player) {
        return player.getData(ModAttachments.HAS_HATCHED_DINOSAUR);
    }

    public static void setHatchedDinosaur(Player player, boolean hatched) {
        player.setData(ModAttachments.HAS_HATCHED_DINOSAUR, hatched);
    }
}
