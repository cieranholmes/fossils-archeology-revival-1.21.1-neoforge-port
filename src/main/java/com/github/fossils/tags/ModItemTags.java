package com.github.fossils.tags;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> FOSSIL_SAPLINGS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("fossil_saplings"));
    public static final TagKey<Item> DNA_INSECTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_insects"));
    public static final TagKey<Item> DNA_LIMBLESS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_limbless"));
    public static final TagKey<Item> DNA_PLANTS = TagKey.create(Registries.ITEM, FossilsAndArcheologyRevival.location("dna_plants"));
}
