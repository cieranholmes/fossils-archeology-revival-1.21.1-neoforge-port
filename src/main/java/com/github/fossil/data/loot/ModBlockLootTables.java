package com.github.fossil.data.loot;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.ModBlocks;
import com.github.fossil.block.PrehistoricPlantInfo;
import com.github.fossil.block.custom_blocks.TallFlowerBlock;
import com.github.fossil.block.custom_blocks.*;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.fossil.block.ModBlocks.*;
import static com.github.fossil.enchantment.ModEnchantments.ARCHEOLOGY;
import static com.github.fossil.enchantment.ModEnchantments.PALEONTOLOGY;
import static com.github.fossil.item.ModItems.FERN_SEED_FOSSIL;
import static com.github.fossil.item.ModItems.FROZEN_MEAT;

/**
 * @see net.minecraft.data.loot.packs.VanillaBlockLoot
 */
public class ModBlockLootTables extends BlockLootSubProvider {

    private static final List<Block> NO_TABLE = List.of(ASH_VENT.get(), ANU_PORTAL.get(), SARCOPHAGUS.get(), TAR.get(),
            ANU_STATUE.get(), ANUBITE_STATUE.get(), ANCIENT_CHEST.get(), ANU_BARRIER_ORIGIN.get(), ANU_BARRIER_FACE.get(),
            MUTANT_TREE_TUMOR.get());
    private static final Set<Item> EXPLOSION_RESISTANT = Set.of();
    private final List<Block> tableDone = new ArrayList<>();

    public ModBlockLootTables(HolderLookup.Provider registries) {
        super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        addCustom(FAKE_OBSIDIAN.get(), createSingleItemTable(Blocks.OBSIDIAN));

        Block amberOre = AMBER_ORE.get();
        addCustom(amberOre, createSilkTouchDispatchTable(amberOre, applyExplosionCondition(amberOre,
                LootItem.lootTableItem(AMBER_CHUNK_MOSQUITO.get()).when(LootItemRandomChanceCondition.randomChance(0.05f)).apply(ApplyBonusCount.addOreBonusCount(fortune()))
                        .otherwise(LootItem.lootTableItem(AMBER_CHUNK.get()).apply(ApplyBonusCount.addOreBonusCount(fortune()))))));
        addCustom(ICED_DIRT.get(), createSilkTouchOnlyTable(ICED_DIRT.get()));
        addCustom(PERMAFROST_BLOCK.get(), multiple(PERMAFROST_BLOCK.get(), 20, FERN_SEED_FOSSIL.get(),
                SKULL_BLOCK.get(), FROZEN_MEAT.get(), Items.BONE, Items.BOOK));

        addCustom(SLIME_TRAIL.get(), randomItem(Items.SLIME_BALL, 0.33f));

        addCustom(CALAMITES_LEAVES.get(), createLeavesDrops(CALAMITES_LEAVES.get(), CALAMITES_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f));
        addCustom(CORDAITES_LEAVES.get(), createLeavesDrops(CORDAITES_LEAVES.get(), CORDAITES_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f));
        addCustom(MUTANT_TREE_LEAVES.get(), createSilkTouchOrShearsDispatchTable(MUTANT_TREE_LEAVES.get(), applyExplosionCondition(MUTANT_TREE_LEAVES.get(), LootItem.lootTableItem(Items.STICK))));
        addCustom(PALM_LEAVES.get(), createLeavesDrops(PALM_LEAVES.get(), PALM_SAPLING.get(), 0.075f, 0.09375f, 0.125f, 0.15f));
        addCustom(SIGILLARIA_LEAVES.get(), createLeavesDrops(SIGILLARIA_LEAVES.get(), SIGILLARIA_SAPLING.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f));
        addCustom(TEMPSKYA_LEAF.get(), createLeavesDrops(TEMPSKYA_LEAF.get(), TEMPSKYA_SAPLING.get(), 0.35f, 0.4f, 0.58f, 0.7f));

        Block tempskyaTop = TEMPSKYA_TOP.get();
        var tempskyaCondition = LootItem.lootTableItem(tempskyaTop).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(tempskyaTop)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TempskyaTopBlock.HALF, DoubleBlockHalf.LOWER)));
        addCustom(tempskyaTop, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(tempskyaCondition)
                .when(ExplosionCondition.survivesExplosion())).setParamSet(LootContextParamSets.BLOCK));

        for (PrehistoricPlantInfo info : PrehistoricPlantInfo.values()) {
            BushBlock flower = info.getPlantBlock();
            if (info.berryAge() != 0 && info.maxAge() != 0) {
                addCustom(flower, berryBlock(info));
            } else {
                var condition = LootItem.lootTableItem(flower);
                if (flower instanceof TallFlowerBlock) {
                    condition.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(flower).setProperties(
                            StatePropertiesPredicate.Builder.properties().hasProperty(TallFlowerBlock.HALF, DoubleBlockHalf.LOWER)));
                } else if (flower instanceof FourTallFlowerBlock) {
                    condition.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(flower).setProperties(
                            StatePropertiesPredicate.Builder.properties().hasProperty(FourTallFlowerBlock.LAYER, 0)));
                }
                addCustom(flower, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(condition)
                        .when(ExplosionCondition.survivesExplosion())).setParamSet(LootContextParamSets.BLOCK));
            }
        }
        var fossils = AlternativesEntry.alternatives(
                fossilReference("regular_fossil_arch_1", enchant(ARCHEOLOGY, 1)),
                fossilReference("regular_fossil_arch_2", enchant(ARCHEOLOGY, 2)),
                fossilReference("regular_fossil_arch_3", enchant(ARCHEOLOGY, 3)),
                fossilReference("regular_fossil_paleo_1", enchant(PALEONTOLOGY, 1)),
                fossilReference("regular_fossil_paleo_2", enchant(PALEONTOLOGY, 2)),
                fossilReference("regular_fossil_paleo_3", enchant(PALEONTOLOGY, 3)),
                fossilReference("regular_fossil", null));
        addCustom(CALCITE_FOSSIL.get(), createSilkTouchDispatchTable(CALCITE_FOSSIL.get(), applyExplosionCondition(CALCITE_FOSSIL.get(), fossils)));
        addCustom(DRIPSTONE_FOSSIL.get(), createSilkTouchDispatchTable(DRIPSTONE_FOSSIL.get(), applyExplosionCondition(DRIPSTONE_FOSSIL.get(), fossils)));
        addCustom(RED_SANDSTONE_FOSSIL.get(), createSilkTouchDispatchTable(RED_SANDSTONE_FOSSIL.get(), applyExplosionCondition(RED_SANDSTONE_FOSSIL.get(), fossils)));
        addCustom(SANDSTONE_FOSSIL.get(), createSilkTouchDispatchTable(SANDSTONE_FOSSIL.get(), applyExplosionCondition(SANDSTONE_FOSSIL.get(), fossils)));
        addCustom(STONE_FOSSIL.get(), createSilkTouchDispatchTable(STONE_FOSSIL.get(), applyExplosionCondition(STONE_FOSSIL.get(), fossils)));

        var deepSlateFossils = AlternativesEntry.alternatives(
                fossilReference("deep_fossil_arch_1", enchant(ARCHEOLOGY, 1)),
                fossilReference("deep_fossil_arch_2", enchant(ARCHEOLOGY, 2)),
                fossilReference("deep_fossil_arch_3", enchant(ARCHEOLOGY, 3)),
                fossilReference("deep_fossil_paleo_1", enchant(PALEONTOLOGY, 1)),
                fossilReference("deep_fossil_paleo_2", enchant(PALEONTOLOGY, 2)),
                fossilReference("deep_fossil_paleo_3", enchant(PALEONTOLOGY, 3)),
                fossilReference("deep_fossil", null));
        addCustom(DEEPSLATE_FOSSIL.get(), createSilkTouchDispatchTable(DEEPSLATE_FOSSIL.get(), applyExplosionCondition(DEEPSLATE_FOSSIL.get(), deepSlateFossils)));
        addCustom(TUFF_FOSSIL.get(), createSilkTouchDispatchTable(TUFF_FOSSIL.get(), applyExplosionCondition(TUFF_FOSSIL.get(), deepSlateFossils)));

        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            Block block = entry.get();
            if (block instanceof ClearGlassBlock) {
                dropWhenSilkTouch(block);
            } else if (block instanceof SlabBlock) {
                addCustom(block, createSlabItemTable(block));
            } else if (block instanceof DoorBlock || block instanceof AmphoraVaseBlock) {
                addCustom(block, createDoorTable(block));
            } else if (block instanceof BedBlock) {
                addCustom(block, createSinglePropConditionTable(block, BedBlock.PART, BedPart.HEAD));
            } else if (!NO_TABLE.contains(block) && !tableDone.contains(block)) {
                dropSelf(block);
            }
            tableDone.add(block);
        }

        // TAR copies Blocks.WATER's Properties (Properties.ofFullCopy), which carries .noLootTable() - its
        // getLootTable() is already BuiltInLootTables.EMPTY, so adding an explicit entry for it here would
        // never be consumed by the base-class generate() loop and would fail its "leftover map entries" check.
        NO_TABLE.forEach(block -> {
            if (block.getLootTable() != BuiltInLootTables.EMPTY) {
                add(block, noDrop());
            }
        });
    }

    private void addCustom(Block block, LootTable.Builder table) {
        add(block, table);
        tableDone.add(block);
    }

    private LootTable.Builder randomItem(Item item, float chance) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(item).when(LootItemRandomChanceCondition.randomChance(chance))).when(ExplosionCondition.survivesExplosion())).setParamSet(LootContextParamSets.BLOCK);
    }

    private LootTable.Builder multiple(Block block, int weight, ItemLike... items) {
        EntryGroup.Builder group = EntryGroup.list();
        for (ItemLike item : items) {
            group.append(LootItem.lootTableItem(item).setWeight(weight));
        }
        return createSilkTouchDispatchTable(block, applyExplosionCondition(block, group));
    }

    private LootPoolSingletonContainer.Builder<?> fossilReference(String location, LootItemCondition.Builder condition) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, FossilsAndArcheologyRevival.location(location));
        if (condition == null) {
            return NestedLootTable.lootTableReference(key);
        }
        return NestedLootTable.lootTableReference(key).when(condition);
    }

    private Holder<Enchantment> fortune() {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
    }

    private LootItemCondition.Builder enchant(ResourceKey<Enchantment> enchantment, int value) {
        Holder<Enchantment> holder = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
        return MatchTool.toolMatches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
                ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(holder, MinMaxBounds.Ints.exactly(value))))));
    }

    private LootTable.Builder berryBlock(PrehistoricPlantInfo info) {
        if (info.getPlantBlock() instanceof BerryBushBlock block) {
            var lootTable = LootTable.lootTable();
            for (int i = info.berryAge(); i <= info.maxAge() ; i++) {
                lootTable = lootTable.withPool(
                        LootPool.lootPool()
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(info.getPlantBlock())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(block.ageProperty(), i)))
                                .add(LootItem.lootTableItem(info.berryItem().get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1 + (i - info.berryAge()), 2 + (i - info.berryAge()))))
                                .apply(ApplyBonusCount.addUniformBonusCount(fortune()))
                );
            }
            return applyExplosionDecay(info.getPlantBlock(), lootTable);
        }
        return LootTable.lootTable();
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<Block> list = new ArrayList<>();
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            list.add(entry.get());
        }
        return list;
    }
}
