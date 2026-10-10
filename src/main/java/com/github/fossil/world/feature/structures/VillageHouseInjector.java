package com.github.fossil.world.feature.structures;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.mixin.StructureTemplatePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds the archeologist's and paleontologist's houses to vanilla's five village house pools.
 * <p>
 * This used to be done by shipping {@code data/minecraft/worldgen/template_pool/village/*}{@code /houses.json}
 * - a verbatim copy of each vanilla pool with our two entries appended. Datapack files replace rather than
 * merge, so those copies silently deleted the houses of whichever other mod lost the load-order coin toss (and
 * ours in the opposite case), and they would have quietly reverted vanilla's own pool to its 1.21.1 contents on
 * any future Minecraft update. Appending at runtime is additive, so every mod's houses survive.
 * <p>
 * NeoForge has no datapack-level equivalent for this: {@code BiomeModifier} and {@code StructureModifier} only
 * reach a structure's {@code StructureSettings} (biomes and spawn overrides), never its template pools. Runtime
 * append via {@link StructureTemplatePoolAccessor} is the standard route, and
 * {@link ServerAboutToStartEvent} is the right moment for it - the datapack registries are fully loaded and
 * frozen by then, and nothing has generated a chunk yet. Template pools are startup-only registries
 * ({@code ReloadableServerRegistries} covers loot tables, predicates and item modifiers, not worldgen), so
 * {@code /reload} cannot drop what we add here.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class VillageHouseInjector {

    /**
     * Same weight the shipped pool copies used, against 30-39 vanilla elements per pool - these are meant to be
     * a rare find, not a fixture of every village.
     */
    private static final int WEIGHT = 1;

    private record VillageHouse(ResourceKey<StructureTemplatePool> pool, String piece) {}

    private static final List<VillageHouse> HOUSES = List.of(
            house("plains", "houses/archeo_house_plains_top"),
            house("plains", "houses/paleo_house_plains_top"),
            house("desert", "houses/archeo_house_desert"),
            house("desert", "houses/paleo_house_desert"),
            house("savanna", "houses/archeo_house_savanna"),
            house("savanna", "houses/paleo_house_savanna"),
            house("snowy", "houses/archeo_house_snowy"),
            house("snowy", "houses/paleo_house_snowy"),
            house("taiga", "houses/archeo_house_taiga_top"),
            house("taiga", "houses/paleo_house_taiga"));

    @SubscribeEvent
    static void addVillageHouses(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> pools = event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        int added = 0;
        for (VillageHouse house : HOUSES) {
            StructureTemplatePool pool = pools.get(house.pool());
            if (pool == null) {
                // A datapack or another mod can legitimately remove a vanilla pool; that costs us one house
                // variant, which is no reason to take the server down with it.
                FossilsAndArcheologyRevival.LOGGER.warn("Village pool {} is missing, so {} will not generate.",
                        house.pool().location(), house.piece());
                continue;
            }
            if (append(pool, house.piece())) {
                added++;
            }
        }
        FossilsAndArcheologyRevival.LOGGER.debug("Added {} of {} village houses to their village pools.", added, HOUSES.size());
    }

    private static boolean append(StructureTemplatePool pool, String piece) {
        StructureTemplatePoolAccessor accessor = (StructureTemplatePoolAccessor) pool;
        // A pool element's toString() embeds its template location - the same identity check
        // JigsawPlacementMixin makes. Guards against adding a second copy if we ever run twice, and against
        // doubling up on a pack that already lists the house in its own datapack.
        if (accessor.getTemplates().stream().anyMatch(element -> element.toString().contains(piece))) {
            return false;
        }

        // Legacy (rather than plain single) to match every other village house: it swaps in
        // BlockIgnoreProcessor.STRUCTURE_AND_AIR, so the air blocks baked into these templates carve out the
        // house interior instead of being skipped. Rigid keeps the piece at the height its jigsaw dictates.
        StructurePoolElement element = StructurePoolElement.legacy(piece).apply(StructureTemplatePool.Projection.RIGID);

        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(accessor.getRawTemplates());
        rawTemplates.add(Pair.of(element, WEIGHT));
        accessor.setRawTemplates(rawTemplates);
        for (int i = 0; i < WEIGHT; i++) {
            accessor.getTemplates().add(element);
        }
        // templates just grew, so the cached max piece height has to be recomputed on next use.
        accessor.setMaxSize(Integer.MIN_VALUE);
        return true;
    }

    private static VillageHouse house(String villageType, String piece) {
        return new VillageHouse(
                ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/" + villageType + "/houses")),
                FossilsAndArcheologyRevival.location(piece).toString());
    }
}
