package com.github.fossils.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

public interface CommonEnergyStorage {

    int getEnergy();

    void extractEnergy(int maxExtract);

    void load(HolderLookup.Provider registries, Tag tag);

    Tag save(HolderLookup.Provider registries);
}
