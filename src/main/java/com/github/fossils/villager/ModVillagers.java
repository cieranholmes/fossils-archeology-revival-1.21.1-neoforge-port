package com.github.fossils.villager;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.block.ModBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * NeoForge auto-registers each PoiType's matching block states (NeoForgeRegistryCallbacks' PoiTypeCallbacks),
 * so unlike source's Architectury version, no LifecycleEvent.SETUP/registerBlockStates step is needed here.
 */
public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, FossilsAndArcheologyRevival.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<PoiType, PoiType> ARCHEOLOGIST_POI = register("archeologist_poi", ModBlocks.WORKTABLE);
    public static final DeferredHolder<VillagerProfession, VillagerProfession> ARCHEOLOGIST = register("archeologist", ARCHEOLOGIST_POI, SoundEvents.VILLAGER_WORK_FISHERMAN);
    public static final DeferredHolder<PoiType, PoiType> PALEONTOLOGIST_POI = register("paleontologist_poi", ModBlocks.ANALYZER);
    public static final DeferredHolder<VillagerProfession, VillagerProfession> PALEONTOLOGIST = register("paleontologist", PALEONTOLOGIST_POI, SoundEvents.VILLAGER_WORK_TOOLSMITH);

    public static void register(IEventBus modEventBus) {
        POI_TYPES.register(modEventBus);
        PROFESSIONS.register(modEventBus);
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, DeferredBlock<Block> block) {
        return register(name, block, 1, 1);
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, DeferredBlock<Block> block, int maxTickets, int validRange) {
        return POI_TYPES.register(name, () -> new PoiType(getBlockStates(block), maxTickets, validRange));
    }

    private static DeferredHolder<VillagerProfession, VillagerProfession> register(String name, DeferredHolder<PoiType, PoiType> jobSite, @Nullable SoundEvent workSound) {
        return PROFESSIONS.register(name, () -> new VillagerProfession(name, holder -> holder.is(jobSite.getKey()), holder -> holder.is(jobSite.getKey()),
                ImmutableSet.of(), ImmutableSet.of(), workSound));
    }

    private static Set<BlockState> getBlockStates(DeferredBlock<Block> block) {
        return ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates());
    }
}
