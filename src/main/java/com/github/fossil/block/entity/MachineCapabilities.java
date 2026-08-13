package com.github.fossil.block.entity;

import com.github.fossil.Config;
import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

/**
 * Replaces source's Forge-mixin-based capability exposure (EnergyContainerBlockEntityMixin/
 * MachineContainerBlockEntityMixin, which inject getCapability directly into the common base classes) with real
 * NeoForge RegisterCapabilitiesEvent registrations - the mixin trick has no equivalent here since this is a
 * NeoForge-only project with no Mixin framework in play at all. Without this, hoppers/other mods can't interact
 * with any machine, which was the actual state of target before this task.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class MachineCapabilities {

    @SubscribeEvent
    static void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerEnergy(event, ModBlockEntities.ANALYZER.get());
        registerEnergy(event, ModBlockEntities.CULTURE_VAT.get());

        registerItemHandler(event, ModBlockEntities.ANALYZER.get());
        registerItemHandler(event, ModBlockEntities.CULTURE_VAT.get());
        registerItemHandler(event, ModBlockEntities.FEEDER.get());
        registerItemHandler(event, ModBlockEntities.SIFTER.get());
        registerItemHandler(event, ModBlockEntities.WORKTABLE.get());
    }

    /**
     * Faithfully replicates source's side != null gate (an unsided/null-direction capability query returns null,
     * matching EnergyContainerBlockEntityMixin's own check) even though it looks like it may have been an
     * unintentional mixin quirk rather than deliberate design - see fossil-known-upstream-bugs policy.
     */
    private static <T extends EnergyContainerBlockEntity> void registerEnergy(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (be, side) ->
                Config.machinesRequireEnergy && side != null ? (IEnergyStorage) be.getEnergyStorage() : null);
    }

    private static <T extends BlockEntity & WorldlyContainer> void registerItemHandler(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, SidedInvWrapper::new);
    }
}
