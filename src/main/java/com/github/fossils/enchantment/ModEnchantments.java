package com.github.fossils.enchantment;

import com.github.fossils.FossilsAndArcheologyRevival;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Enchantment became a datapack-JSON-driven dynamic registry in 1.21.1 (like JukeboxSong) - there is no
 * DeferredRegister<Enchantment> to populate here. The actual definitions live at
 * data/fossils/enchantment/{archeology,paleontology}.json; these are just the ResourceKey constants other code
 * (villager trades, loot tables) references them by, mirroring vanilla's own Enchantments.java.
 */
public interface ModEnchantments {
    ResourceKey<Enchantment> ARCHEOLOGY = key("archeology");
    ResourceKey<Enchantment> PALEONTOLOGY = key("paleontology");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, FossilsAndArcheologyRevival.location(name));
    }
}
