package com.github.fossil.world.biome;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Resource keys for the custom biomes. The actual biome content (generation settings, mob spawns,
 * special effects) is authored as datapack JSON under {@code data/fossil/worldgen/biome/} rather than
 * a {@code BootstapContext} bootstrap method, since this project's datagen pipeline isn't stood up yet
 * (Phase 7 scope) — these keys are only what runtime code (chunk generators, the TerraBlender region,
 * dimensions) needs to look biomes up by.
 */
public class ModBiomes {
    public static final ResourceKey<Biome> ANU_LAIR_KEY = resource("anu_lair");
    public static final ResourceKey<Biome> TREASURE_ROOM_KEY = resource("treasure_room");
    public static final ResourceKey<Biome> VOLCANO_KEY = resource("volcano");

    private static ResourceKey<Biome> resource(String name) {
        return ResourceKey.create(Registries.BIOME, FossilsAndArcheologyRevival.location(name));
    }
}
