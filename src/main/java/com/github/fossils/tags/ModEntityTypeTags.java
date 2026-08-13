package com.github.fossils.tags;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTags {
    public static final TagKey<EntityType<?>> LIVESTOCK = TagKey.create(Registries.ENTITY_TYPE, FossilsAndArcheologyRevival.location("livestock"));
    public static final TagKey<EntityType<?>> MAMMAL = TagKey.create(Registries.ENTITY_TYPE, FossilsAndArcheologyRevival.location("mammal"));
}
