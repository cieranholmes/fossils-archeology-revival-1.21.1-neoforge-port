package com.github.fossils.block;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

public class ModWoodTypes {
    public static final WoodInfo CALAMITES = register(FossilsAndArcheologyRevival.location("calamites"), ModBlockSetTypes.CALAMITES, MapColor.COLOR_GREEN);
    public static final WoodInfo CORDAITES = register(FossilsAndArcheologyRevival.location("cordaites"), ModBlockSetTypes.CORDAITES, MapColor.PODZOL);
    public static final WoodInfo MUTANT_TREE = register(FossilsAndArcheologyRevival.location("mutant_tree"), ModBlockSetTypes.MUTANT_TREE, MapColor.CRIMSON_NYLIUM);
    public static final WoodInfo PALM = register(FossilsAndArcheologyRevival.location("palm"), ModBlockSetTypes.PALM, MapColor.SAND);
    public static final WoodInfo SIGILLARIA = register(FossilsAndArcheologyRevival.location("sigillaria"), ModBlockSetTypes.SIGILLARIA, MapColor.GRASS);
    public static final WoodInfo TEMPSKYA = register(FossilsAndArcheologyRevival.location("tempskya"), ModBlockSetTypes.TEMPSKYA, MapColor.TERRACOTTA_BROWN);

    public static void register() {
    }

    private static WoodInfo register(ResourceLocation id, BlockSetType setType, MapColor mapColor) {
        return new WoodInfo(id.getPath(), WoodType.register(new WoodType(id.toString(), setType)), setType, mapColor);
    }

    public record WoodInfo(String name, WoodType woodType, BlockSetType setType, MapColor mapColor) {
    }
}
