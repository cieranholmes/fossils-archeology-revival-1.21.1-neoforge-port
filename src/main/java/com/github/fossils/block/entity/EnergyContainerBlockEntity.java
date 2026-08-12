package com.github.fossils.block.entity;

import com.github.fossils.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class EnergyContainerBlockEntity extends MachineContainerBlockEntity {
    protected final CommonEnergyStorage energyStorage = new FAEnergyStorage(Config.machineMaxEnergy, Config.machineTransferRate, Config.machineEnergyUsage, 0) {
        @Override
        protected void onChange() {
            setChanged();
        }
    };

    protected EnergyContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Energy")) {
            energyStorage.load(registries, tag.get("Energy"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Energy", energyStorage.save(registries));
    }

    public CommonEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
