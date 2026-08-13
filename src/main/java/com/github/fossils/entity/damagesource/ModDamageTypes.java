package com.github.fossils.entity.damagesource;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> SUFFOCATE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, FossilsAndArcheologyRevival.location("aquatic_suffocate"));
}
