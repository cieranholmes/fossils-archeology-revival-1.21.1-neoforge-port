package com.github.fossil.tags;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> ANCIENT_WOOD_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("ancient_wood_logs"));
    public static final TagKey<Item> CALAMITES_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("calamites_logs"));
    public static final TagKey<Item> CORDAITES_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("cordaites_logs"));
    public static final TagKey<Item> MUTANT_TREE_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("mutant_tree_logs"));
    public static final TagKey<Item> PALM_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("palm_logs"));
    public static final TagKey<Item> SIGILLARIA_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("sigillaria_logs"));
    public static final TagKey<Item> TEMPSKYA_LOGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("tempskya_logs"));
    public static final TagKey<Item> FOSSIL_SEEDS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fossil_seeds"));
    public static final TagKey<Item> RESTORED_SEEDS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("restored_seeds"));
    public static final TagKey<Item> FOSSIL_SAPLINGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fossil_saplings"));
    public static final TagKey<Item> FOSSILS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fossil"));
    public static final TagKey<Item> FIGURINES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("figurines"));
    public static final TagKey<Item> BIRD_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bird_eggs"));
    public static final TagKey<Item> DINO_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dino_eggs"));
    public static final TagKey<Item> ALL_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("all_eggs"));
    public static final TagKey<Item> COOKABLE_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("cookable_eggs"));
    public static final TagKey<Item> SIFTER_INPUTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("sifter_inputs"));
    public static final TagKey<Item> DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna"));
    public static final TagKey<Item> EMBRYOS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("embryos"));
    public static final TagKey<Item> DNA_INSECTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_insects"));
    public static final TagKey<Item> DNA_LIMBLESS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_limbless"));
    public static final TagKey<Item> DNA_PLANTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_plants"));
    public static final TagKey<Item> ARM_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_arm"));
    public static final TagKey<Item> FOOT_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_foot"));
    public static final TagKey<Item> LEG_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_leg"));
    public static final TagKey<Item> RIBCAGE_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_ribcage"));
    public static final TagKey<Item> SKULL_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_skull"));
    public static final TagKey<Item> TAIL_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_tail"));
    public static final TagKey<Item> UNIQUE_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_unique"));
    public static final TagKey<Item> VERTEBRAE_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_vertebrae"));
    public static final TagKey<Item> ALL_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_all"));
    //Source has COOKED_MEAT pointing at the same location as UNCOOKED_MEAT ("meat_uncooked") - a copy-paste bug,
    //faithfully replicated per project policy (see .claude/memory/fossils_known_upstream_bugs.md) rather than
    //silently fixed mid-port.
    public static final TagKey<Item> UNCOOKED_MEAT = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("meat_uncooked"));
    public static final TagKey<Item> COOKED_MEAT = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("meat_uncooked"));

    public static final TagKey<Item> FILTER_BONES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_bones"));
    public static final TagKey<Item> FILTER_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_dna"));
    public static final TagKey<Item> FILTER_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_eggs"));
    public static final TagKey<Item> FILTER_MEAT = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_meat"));
    public static final TagKey<Item> FILTER_BUCKETS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_buckets"));

    public static final TagKey<Item> FILTER_PLANTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_plants"));
    public static final TagKey<Item> FILTER_OTHER = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_other"));
    public static final TagKey<Item> FILTER_TREES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_trees"));
    public static final TagKey<Item> FILTER_VASES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_vases"));
    public static final TagKey<Item> FILTER_UNBREAKABLE = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_unbreakable"));
    public static final TagKey<Item> FILTER_MACHINES = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_machines"));
    public static final TagKey<Item> FILTER_BUILDING_BLOCKS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_building_blocks"));

    public static final TagKey<Item> FILTER_PARK = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_park"));
    public static final TagKey<Item> FILTER_TOOLS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("filter_tools"));

    //REI/JEI support
    public static final TagKey<Item> FISH_EGGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fish_eggs"));
    public static final TagKey<Item> BONES_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("bones_dna"));
    public static final TagKey<Item> MEAT_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("meat_dna"));
    public static final TagKey<Item> DINO_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dino_dna"));
    public static final TagKey<Item> FISH_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fish_dna"));
    public static final TagKey<Item> EMBRYO_DNA = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("embryo_dna"));

    //Common conventions tags (source's Fabric-only "c:" tags - NeoForge unified onto the same "c" namespace)
    public static final TagKey<Item> C_FOODS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods"));
    public static final TagKey<Item> C_GLASS_BLOCKS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks"));
    public static final TagKey<Item> C_SWORDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "swords"));
    public static final TagKey<Item> C_PICKAXES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "pickaxes"));
    public static final TagKey<Item> C_AXES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "axes"));
    public static final TagKey<Item> C_SHOVELS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "shovels"));
    public static final TagKey<Item> C_HOES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "hoes"));
    public static final TagKey<Item> C_WATER_BUCKETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "water_buckets"));
}
