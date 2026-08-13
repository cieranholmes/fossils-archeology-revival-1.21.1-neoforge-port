package com.github.fossil.world.feature.tree;

import com.github.fossil.world.feature.configuration.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class MutantTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("fossils_mutant_tree", Optional.empty(), Optional.of(ModConfiguredFeatures.MUTANT_TREE_KEY), Optional.empty());

    private MutantTreeGrower() {
    }
}
