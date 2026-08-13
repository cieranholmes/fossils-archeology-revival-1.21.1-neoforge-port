package com.github.fossils.capabilities;

import com.github.fossils.entity.prehistoric.base.EntityInfo;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Simplified NeoForge-native replacement for the source mod's Architectury @ExpectPlatform capability system.
 * Mammal embryo/pregnancy tracking is not wired up yet (no current caller); hatched-dinosaur tracking uses the
 * player's persistent NBT data, which is all DinosaurEgg currently needs.
 */
public class ModCapabilities {
    private static final String HAS_HATCHED_DINOSAUR = "fossils_hatched_dinosaur";

    public static boolean hasEmbryo(Animal animal) {
        return false;
    }

    public static int getEmbryoProgress(Animal animal) {
        return 0;
    }

    @Nullable
    public static EntityInfo getEmbryo(Animal animal) {
        return null;
    }

    public static void setEmbryoProgress(Animal animal, int embryoProgress) {
    }

    public static void setEmbryo(Animal animal, @Nullable EntityInfo embryo) {
    }

    public static void syncMammalWithClient(Animal animal, int embryoProgress, EntityInfo embryo) {
    }

    public static void startPregnancy(Animal animal, EntityInfo embryo) {
        setEmbryo(animal, embryo);
        setEmbryoProgress(animal, 1);
        syncMammalWithClient(animal, 1, embryo);
    }

    public static void stopPregnancy(Animal animal) {
        setEmbryo(animal, null);
        setEmbryoProgress(animal, 0);
        syncMammalWithClient(animal, 0, null);
    }

    public static boolean hasHatchedDinosaur(Player player) {
        return player.getPersistentData().getBoolean(HAS_HATCHED_DINOSAUR);
    }

    public static void setHatchedDinosaur(Player player, boolean hatched) {
        player.getPersistentData().putBoolean(HAS_HATCHED_DINOSAUR, hatched);
    }
}
