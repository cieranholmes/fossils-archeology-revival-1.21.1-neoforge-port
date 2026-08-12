package com.github.fossils.world.feature.tree;

import com.github.fossils.world.feature.configuration.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class SigillariaTreeGrower {
    public static final TreeGrower GROWER = new TreeGrower("fossils_sigillaria", Optional.empty(), Optional.of(ModConfiguredFeatures.SIGILLARIA_TREE_KEY), Optional.empty());

    private SigillariaTreeGrower() {
    }
}
