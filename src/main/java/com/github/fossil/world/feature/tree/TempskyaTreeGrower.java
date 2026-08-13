package com.github.fossil.world.feature.tree;

import com.github.fossil.world.feature.configuration.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class TempskyaTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("fossils_tempskya", Optional.empty(), Optional.of(ModConfiguredFeatures.TEMPSKYA_TREE_KEY), Optional.empty());

    private TempskyaTreeGrower() {
    }
}
