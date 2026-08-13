package com.github.fossil.world.feature.structures;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Resource keys for the mod's custom-Java structures. The vanilla-shaped jigsaw structures (aztec temple,
 * egyptian academy, fossil/tar sites) are pure datapack JSON under {@code data/fossil/worldgen/structure/}
 * and don't need a Java key here — only the 3 structures with real Java generation logic do, since the
 * two custom chunk generators need to look them up by key at runtime.
 */
public class ModStructures {
    public static final ResourceKey<Structure> ANU_CASTLE_KEY = createKey("anu_castle");
    public static final ResourceKey<Structure> HELL_BOAT_KEY = createKey("hell_boat");
    public static final ResourceKey<Structure> TREASURE_ROOM_KEY = createKey("treasure_room");

    private static ResourceKey<Structure> createKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, FossilsAndArcheologyRevival.location(name));
    }
}
