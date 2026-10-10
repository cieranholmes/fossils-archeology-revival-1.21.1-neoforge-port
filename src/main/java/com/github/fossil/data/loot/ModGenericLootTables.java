package com.github.fossil.data.loot;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.tags.ModItemTags;
import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.github.fossil.block.ModBlocks.SKULL_BLOCK;
import static com.github.fossil.item.ModItems.*;

public class ModGenericLootTables implements LootTableSubProvider {
    private final Map<ResourceKey<LootTable>, LootTable.Builder> map = Maps.newHashMap();
    private int scarab;
    private int broken;
    private int skullBlock;
    private int mobFossil;
    private Item mobFossilItem;
    private int plantFossil;
    private int relic;
    private int bone;

    protected void addTables() {
        //lol this is so bad but I like it
        //All adds up to ~1095
        var defaultList = scarab(1).broken(5).skullBlock(34).mobFossil(BIO_FOSSIL, 275).plant(110).relic(200).bone(440)
                .group(6, 6, 3, 3, 3, 3, 3, 3);
        var archList1 = scarab(6).broken(9).skullBlock(64).mobFossil(BIO_FOSSIL, 225).plant(85).relic(475).bone(200)
                .group(3, 3, 3, 3, 3, 3, 3, 3);
        var archList2 = scarab(11).broken(11).skullBlock(110).mobFossil(BIO_FOSSIL, 132).plant(50).relic(715).bone(36).
                group(3, 3, 3, 3, 3, 3, 3, 3);
        var archList3 = scarab(16).broken(13).skullBlock(144).mobFossil(BIO_FOSSIL, 50).plant(20).relic(820).bone(0).
                group(3, 3, 3, 3, 3, 3, 3, 3);

        var paleList1 = scarab(1).broken(5).skullBlock(55).mobFossil(BIO_FOSSIL, 260).plant(145).relic(20).bone(440)
                .group(18, 18, 12, 12, 12, 12, 12, 12);
        var paleList2 = scarab(1).broken(5).skullBlock(30).mobFossil(BIO_FOSSIL, 320).plant(175).relic(0).bone(326)
                .group(26, 26, 20, 20, 24, 20, 20, 20);
        var paleList3 = scarab(1).broken(5).skullBlock(36).mobFossil(BIO_FOSSIL, 350).plant(205).relic(0).bone(210)
                .group(40, 40, 32, 32, 36, 32, 32, 32);

        map.put(lootTableKey("regular_fossil"), basic(defaultList));
        map.put(lootTableKey("regular_fossil_arch_1"), basic(archList1));
        map.put(lootTableKey("regular_fossil_arch_2"), basic(archList2));
        map.put(lootTableKey("regular_fossil_arch_3"), basic(archList3));
        map.put(lootTableKey("regular_fossil_paleo_1"), basic(paleList1));
        map.put(lootTableKey("regular_fossil_paleo_2"), basic(paleList2));
        map.put(lootTableKey("regular_fossil_paleo_3"), basic(paleList3));

        defaultList = scarab(1).broken(5).skullBlock(34).mobFossil(SHALE_FOSSIL, 300).plant(85).relic(200).bone(440)
                .group();
        archList1 = scarab(6).broken(9).skullBlock(64).mobFossil(SHALE_FOSSIL, 255).plant(55).relic(475).bone(200)
                .group();
        archList2 = scarab(11).broken(11).skullBlock(80).mobFossil(SHALE_FOSSIL, 192).plant(20).relic(715).bone(36).
                group();
        archList3 = scarab(16).broken(13).skullBlock(110).mobFossil(SHALE_FOSSIL, 120).plant(0).relic(820).bone(0).
                group();

        paleList1 = scarab(1).broken(5).skullBlock(55).mobFossil(SHALE_FOSSIL, 280).plant(115).relic(20).bone(440)
                .group(18, 18, 12, 12, 12, 12, 12, 12);
        paleList2 = scarab(1).broken(5).skullBlock(30).mobFossil(SHALE_FOSSIL, 330).plant(175).relic(0).bone(326)
                .group(26, 26, 20, 20, 24, 20, 20, 20);
        paleList3 = scarab(1).broken(5).skullBlock(36).mobFossil(SHALE_FOSSIL, 370).plant(200).relic(0).bone(210)
                .group(40, 40, 32, 32, 36, 32, 32, 32);
        map.put(lootTableKey("deep_fossil"), basic(defaultList));
        map.put(lootTableKey("deep_fossil_arch_1"), basic(archList1));
        map.put(lootTableKey("deep_fossil_arch_2"), basic(archList2));
        map.put(lootTableKey("deep_fossil_arch_3"), basic(archList3));
        map.put(lootTableKey("deep_fossil_paleo_1"), basic(paleList1));
        map.put(lootTableKey("deep_fossil_paleo_2"), basic(paleList2));
        map.put(lootTableKey("deep_fossil_paleo_3"), basic(paleList3));
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        addTables();
        for (Map.Entry<ResourceKey<LootTable>, LootTable.Builder> entry : map.entrySet()) {
            biConsumer.accept(entry.getKey(), entry.getValue());
        }
    }

    private static ResourceKey<LootTable> lootTableKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, FossilsAndArcheologyRevival.location(path));
    }

    private LootTable.Builder basic(LootPoolEntryContainer.Builder<?> builder) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(builder));

    }

    private ModGenericLootTables scarab(int weight) {
        scarab = weight;
        return this;
    }

    private ModGenericLootTables broken(int weight) {
        broken = weight;
        return this;
    }

    private ModGenericLootTables skullBlock(int weight) {
        skullBlock = weight;
        return this;
    }

    private ModGenericLootTables relic(int weight) {
        relic = weight;
        return this;
    }

    private ModGenericLootTables mobFossil(DeferredItem<Item> fossil, int weight) {
        mobFossilItem = fossil.get();
        mobFossil = weight;
        return this;
    }

    private ModGenericLootTables plant(int weight) {
        plantFossil = weight;
        return this;
    }

    private ModGenericLootTables bone(int weight) {
        bone = weight;
        return this;
    }

    private EntryGroup.Builder group(int... weights) {
        EntryGroup.Builder builder = EntryGroup.list(lootItem(SCARAB_GEM.get(), scarab), lootItem(BROKEN_SWORD.get(), broken),
                lootItem(BROKEN_HELMET.get(), broken), lootItem(SKULL_BLOCK.get(), skullBlock),
                lootItem(mobFossilItem, mobFossil), lootItem(RELIC_SCRAP.get(), relic),
                lootItem(Items.BONE, bone), lootItem(PLANT_FOSSIL.get(), plantFossil));
        if (weights.length == 8) {
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.LEG_BONES.location()).setWeight(weights[0]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.ARM_BONES.location()).setWeight(weights[1]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.FOOT_BONES.location()).setWeight(weights[2]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.SKULL_BONES.location()).setWeight(weights[3]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.RIBCAGE_BONES.location()).setWeight(weights[4]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.VERTEBRAE_BONES.location()).setWeight(weights[5]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.UNIQUE_BONES.location()).setWeight(weights[6]));
            builder.append(DynamicLoot.dynamicEntry(ModItemTags.TAIL_BONES.location()).setWeight(weights[7]));
        }
        return builder;
    }

    private LootPoolSingletonContainer.Builder<?> lootItem(ItemLike item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight);
    }
}
