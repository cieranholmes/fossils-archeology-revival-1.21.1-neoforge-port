package com.github.fossil.tags;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> ANCIENT_WOOD_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("ancient_wood_logs"));
    public static final TagKey<Block> CALAMITES_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("calamites_logs"));
    public static final TagKey<Block> CORDAITES_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("cordaites_logs"));
    public static final TagKey<Block> MUTANT_TREE_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("mutant_tree_logs"));
    public static final TagKey<Block> PALM_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("palm_logs"));
    public static final TagKey<Block> SIGILLARIA_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("sigillaria_logs"));
    public static final TagKey<Block> TEMPSKYA_LOGS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("tempskya_logs"));
    public static final TagKey<Block> FIGURINES = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("figurines"));
    public static final TagKey<Block> UNBREAKABLE = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("unbreakable"));
    public static final TagKey<Block> MOOD_BONUS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("mood_bonus"));
    public static final TagKey<Block> PLANTS = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("plants"));
    public static final TagKey<Block> PLANTABLE_ON_SAND = TagKey.create(Registries.BLOCK, FossilsAndArcheologyRevival.location("plantable_on_sand"));

    //Common conventions tag (source's Fabric-only "c:" tag - NeoForge unified onto the same "c" namespace)
    public static final TagKey<Block> C_GLASS_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));
}
