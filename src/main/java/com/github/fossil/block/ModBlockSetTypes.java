package com.github.fossil.block;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ModBlockSetTypes {
    public static final BlockSetType CALAMITES = register(FossilsAndArcheologyRevival.location("calamites"));
    public static final BlockSetType CORDAITES = register(FossilsAndArcheologyRevival.location("cordaites"));
    public static final BlockSetType MUTANT_TREE = register(FossilsAndArcheologyRevival.location("mutant_tree"));
    public static final BlockSetType PALM = register(FossilsAndArcheologyRevival.location("palm"));
    public static final BlockSetType SIGILLARIA = register(FossilsAndArcheologyRevival.location("sigillaria"));
    public static final BlockSetType TEMPSKYA = register(FossilsAndArcheologyRevival.location("tempskya"));

    public static void register() {
    }

    private static BlockSetType register(ResourceLocation id) {
        return BlockSetType.register(new BlockSetType(id.toString()));
    }
}
