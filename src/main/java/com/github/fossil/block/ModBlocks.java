package com.github.fossil.block;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.block.custom_blocks.*;
import com.github.fossil.block.custom_blocks.SkullBlock;
import com.github.fossil.block.custom_blocks.TallFlowerBlock;
import com.github.fossil.item.CustomBlockItem;
import com.github.fossil.item.ModItems;
import com.github.fossil.item.ModTabs;
import com.github.fossil.material.ModFluids;
import com.github.fossil.util.Version;
import com.github.fossil.world.feature.tree.CalamitesTreeGrower;
import com.github.fossil.world.feature.tree.CordaitesTreeGrower;
import com.github.fossil.world.feature.tree.MutantTreeGrower;
import com.github.fossil.world.feature.tree.PalmTreeGrower;
import com.github.fossil.world.feature.tree.SigillariaTreeGrower;
import com.github.fossil.world.feature.tree.TempskyaTreeGrower;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import static net.minecraft.world.level.block.state.properties.NoteBlockInstrument.*;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FossilsAndArcheologyRevival.MODID);

    public static final DeferredBlock<BubbleBlowerBlock> BUBBLE_BLOWER = registerBlock("bubble_blower",
            () -> new BubbleBlowerBlock(Properties.of().mapColor(MapColor.METAL).strength(3).sound(SoundType.METAL).requiresCorrectToolForDrops())
    );
    public static final DeferredBlock<Block> ANALYZER = registerBlock("analyzer",
            () -> new AnalyzerBlock(Properties.ofFullCopy(BUBBLE_BLOWER.get()).lightLevel(activeBlockEmission(14))));
    public static final DeferredBlock<SifterBlock> SIFTER = registerBlock("sifter",
            () -> new SifterBlock(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(BASS)
                    .strength(2.5f).sound(SoundType.WOOD)));
    public static final DeferredBlock<CultureVatBlock> CULTURE_VAT = registerBlock("culture_vat", () -> new CultureVatBlock(
            Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(HAT).strength(2f).requiresCorrectToolForDrops()
                    .lightLevel(activeBlockEmission(14)).noOcclusion()));
    public static final DeferredBlock<Block> WORKTABLE = registerBlock("worktable", () -> new WorktableBlock(
            Properties.of().mapColor(MapColor.COLOR_BROWN).ignitedByLava().instrument(BASS)
                    .strength(1f).sound(SoundType.WOOD)));
    public static final DeferredBlock<FeederBlock> FEEDER = registerBlock("feeder",
            () -> new FeederBlock(Properties.ofFullCopy(BUBBLE_BLOWER.get())));

    public static final DeferredBlock<LiquidBlock> TAR = blockWithoutBlockItem("tar",
            () -> TarBlock.get(ModFluids.TAR.get(), Properties.ofFullCopy(Blocks.WATER).isSuffocating(ModBlocks::always).isViewBlocking(ModBlocks::always)));

    public static final DeferredBlock<AnuStatueBlock> ANU_STATUE = blockWithCustomBlockItem("anu_statue",
            () -> new AnuStatueBlock(Properties.of().noOcclusion().strength(-1, 60000000)),
            block -> new CustomBlockItem(block, new Item.Properties()));
    public static final DeferredBlock<AnubiteStatueBlock> ANUBITE_STATUE = blockWithCustomBlockItem("anubite_statue",
            () -> new AnubiteStatueBlock(Properties.of().noOcclusion().strength(-1, 60000000)),
            block -> new CustomBlockItem(block, new Item.Properties()));
    public static final DeferredBlock<AnuBarrierOriginBlock> ANU_BARRIER_ORIGIN = blockWithDebugItem("anu_barrier_origin",
            () -> new AnuBarrierOriginBlock(Properties.ofFullCopy(Blocks.BARRIER).mapColor(MapColor.NONE)));
    public static final DeferredBlock<AnuBarrierFaceBlock> ANU_BARRIER_FACE = blockWithDebugItem("anu_barrier_face",
            () -> new AnuBarrierFaceBlock(Properties.ofFullCopy(Blocks.BARRIER).mapColor(MapColor.NONE)));
    public static final DeferredBlock<AnuPortal> ANU_PORTAL = blockWithDebugItem("anu_portal",
            () -> new AnuPortal(Properties.ofFullCopy(Blocks.NETHER_PORTAL)));
    public static final DeferredBlock<AncientChestBlock> ANCIENT_CHEST = blockWithCustomBlockItem("ancient_chest",
            () -> new AncientChestBlock(Properties.of().mapColor(MapColor.WOOD).instrument(BASS)
                    .noOcclusion().strength(-1, 3600000)),
            block -> new CustomBlockItem(block, new Item.Properties()));
    public static final DeferredBlock<Block> SARCOPHAGUS = blockWithCustomBlockItem("sarcophagus",
            () -> new SarcophagusBlock(Properties.of().noOcclusion().strength(-1, 60000000)
                    .lightLevel(state -> state.getValue(SarcophagusBlock.LIT) ? 7 : 0)),
            block -> new CustomBlockItem(block, new Item.Properties()));
    public static final DeferredBlock<Block> FAKE_OBSIDIAN = blockWithDebugItem("fake_obsidian",
            () -> new FakeObsidian(Properties.ofFullCopy(Blocks.OBSIDIAN)));
    public static final DeferredBlock<VolcanoAshVent> ASH_VENT = blockWithDebugItem("ash_vent",
            VolcanoAshVent::new);

    public static final DeferredBlock<DrumBlock> DRUM = registerBlock("drum",
            () -> new DrumBlock(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(BASS)
                    .sound(SoundType.WOOD)));
    public static final DeferredBlock<BedBlock> COMFY_BED = registerBlock("comfy_bed", () -> new ComfyBedBlock(
            Properties.of().mapColor(MapColor.WOOL).ignitedByLava().sound(SoundType.WOOD).strength(0.2f).noOcclusion()));

    public static final DeferredBlock<Block> SHELL = registerBlock("shell",
            () -> new ShellBlock(Properties.of().mapColor(MapColor.STONE).instrument(BASEDRUM).strength(1).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<DropExperienceBlock> AMBER_ORE = registerBlock("amber_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5), Properties.of().mapColor(MapColor.STONE).instrument(BASEDRUM).strength(3f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> AMBER_BLOCK = registerBlock("amber_block",
            () -> new Block(Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(BASEDRUM).strength(3f).requiresCorrectToolForDrops().noOcclusion()
                    .isViewBlocking(ModBlocks::never)));
    public static final DeferredBlock<AmberChunkBlock> AMBER_CHUNK = registerBlock("amber_chunk",
            () -> new AmberChunkBlock(Properties.ofFullCopy(AMBER_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<AmberChunkBlock> AMBER_CHUNK_DOMINICAN = registerBlock("amber_chunk_dominican",
            () -> new AmberChunkBlock(Properties.ofFullCopy(AMBER_BLOCK.get()).noOcclusion()));
    public static final DeferredBlock<AmberChunkBlock> AMBER_CHUNK_MOSQUITO = registerBlock("amber_chunk_mosquito",
            () -> new AmberChunkBlock(Properties.ofFullCopy(AMBER_BLOCK.get()).noOcclusion()));

    public static final DeferredBlock<IcedDirtBlock> ICED_DIRT = registerBlock("iced_dirt",
            () -> new IcedDirtBlock(Properties.of().mapColor(MapColor.DIRT).strength(1, 4).sound(SoundType.MOSS).randomTicks()));
    public static final DeferredBlock<ColoredFallingBlock> DENSE_SAND = registerBlock("dense_sand",
            () -> new ColoredFallingBlock(new ColorRGBA(0x8C765C), Properties.of().mapColor(MapColor.SAND).instrument(SNARE)
                    .strength(3f, 15f).sound(SoundType.SAND)));
    public static final DeferredBlock<SkullBlock> SKULL_BLOCK = registerBlock("skull",
            () -> new SkullBlock(Properties.of().mapColor(MapColor.SAND).instrument(XYLOPHONE).strength(2, 15f)
                    .requiresCorrectToolForDrops().sound(SoundType.BONE_BLOCK)));
    public static final DeferredBlock<SkullBlock> SKULL_LANTERN = registerBlock("skull_lantern",
            () -> new SkullBlock(Properties.of().mapColor(MapColor.SAND).instrument(XYLOPHONE).lightLevel(value -> 14).strength(2, 15f)
                    .requiresCorrectToolForDrops().sound(SoundType.BONE_BLOCK)));
    public static final DeferredBlock<Block> SLIME_TRAIL = registerBlock("slime_trail",
            () -> new RailBlock(Properties.ofFullCopy(Blocks.SLIME_BLOCK).noCollission()));

    public static final DeferredBlock<ChainFenceBlock> LARGE_CHAIN_FENCE = registerBlock("large_chain_fence",
            () -> new ChainFenceBlock(Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final DeferredBlock<ChainFenceBlock> SMALL_CHAIN_FENCE = registerBlock("small_chain_fence",
            () -> new ChainFenceBlock(Properties.ofFullCopy(Blocks.IRON_BARS)));

    public static final DeferredBlock<Block> ANCIENT_STONE = registerBlock("ancient_stone",
            () -> new Block(Properties.of().mapColor(MapColor.STONE).instrument(BASEDRUM).strength(1.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ANCIENT_STONE_BRICKS = registerBlock("ancient_stone_bricks",
            () -> new Block(Properties.ofFullCopy(ANCIENT_STONE.get())));
    public static final DeferredBlock<SlabBlock> ANCIENT_STONE_SLAB = registerBlock("ancient_stone_slab",
            () -> new SlabBlock(Properties.ofFullCopy(ANCIENT_STONE.get())));
    public static final DeferredBlock<StairBlock> ANCIENT_STONE_STAIRS = registerBlock("ancient_stone_stairs",
            () -> new StairBlock(ANCIENT_STONE.get().defaultBlockState(), Properties.ofFullCopy(ANCIENT_STONE.get())));
    public static final DeferredBlock<WallBlock> ANCIENT_STONE_WALL = registerBlock("ancient_stone_wall",
            () -> new WallBlock(Properties.ofFullCopy(ANCIENT_STONE.get())));
    public static final DeferredBlock<Block> ANCIENT_WOOD_PLANKS = registerBlock("ancient_wood_planks",
            () -> new Block(Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(BASS)
                    .strength(2f, 3f).sound(SoundType.WOOD)));
    public static final DeferredBlock<SlabBlock> ANCIENT_WOOD_SLAB = registerBlock("ancient_wood_slab",
            () -> new SlabBlock(Properties.ofFullCopy(ANCIENT_WOOD_PLANKS.get())));
    public static final DeferredBlock<RotatedPillarBlock> ANCIENT_WOOD_LOG = registerBlock("ancient_wood_log",
            () -> FlammableRotatedPillarBlock.get(Properties.ofFullCopy(ANCIENT_WOOD_PLANKS.get())));
    public static final DeferredBlock<StairBlock> ANCIENT_WOOD_STAIRS = registerBlock("ancient_wood_stairs",
            () -> new StairBlock(ANCIENT_WOOD_PLANKS.get().defaultBlockState(), Properties.ofFullCopy(ANCIENT_WOOD_PLANKS.get())));
    public static final DeferredBlock<ClearGlassBlock> REINFORCED_GLASS = registerBlock("reinforced_glass",
            () -> new ClearGlassBlock(Properties.ofFullCopy(Blocks.GLASS).strength(3f, 25f)));
    public static final DeferredBlock<ClearGlassBlock> ANCIENT_GLASS = registerBlock("ancient_glass",
            () -> new ClearGlassBlock(Properties.ofFullCopy(Blocks.GLASS).strength(1f)));

    //Fossil Blocks
    public static final DeferredBlock<FossilBlock> CALCITE_FOSSIL = registerBlock("fossil_calcite",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.CALCITE).sound(SoundType.CALCITE).strength(1.25f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> DEEPSLATE_FOSSIL = registerBlock("fossil_deepslate",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.DEEPSLATE).sound(SoundType.DEEPSLATE).strength(4, 7).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> DRIPSTONE_FOSSIL = registerBlock("fossil_dripstone",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.DRIPSTONE_BLOCK).sound(SoundType.DRIPSTONE_BLOCK).strength(3, 2).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> RED_SANDSTONE_FOSSIL = registerBlock("fossil_red_sandstone",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.RED_SANDSTONE).strength(1.6f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> SANDSTONE_FOSSIL = registerBlock("fossil_sandstone",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.SANDSTONE).strength(1.6f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> STONE_FOSSIL = registerBlock("fossil_stone",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.STONE).strength(3, 6).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FossilBlock> TUFF_FOSSIL = registerBlock("fossil_tuff",
            () -> new FossilBlock(Properties.ofFullCopy(Blocks.TUFF).sound(SoundType.TUFF).strength(3, 6).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> TARRED_DIRT = registerBlock("tarred_dirt",
            () -> new Block(Properties.ofFullCopy(Blocks.DIRT)));
    public static final DeferredBlock<PermafrostBlock> PERMAFROST_BLOCK = registerBlock("permafrost_block",
            () -> new PermafrostBlock(Properties.of().mapColor(MapColor.COLOR_BLUE).instrument(BASEDRUM).strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> VOLCANIC_ASH = registerBlock("volcanic_ash",
            () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2f).requiresCorrectToolForDrops().sound(SoundType.GRAVEL)));
    public static final DeferredBlock<Block> VOLCANIC_ROCK = registerBlock("volcanic_rock",
            () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(BASEDRUM).strength(1f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> VOLCANIC_BRICKS = registerBlock("volcanic_bricks",
            () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(BASEDRUM).strength(1.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> VOLCANIC_BRICK_SLAB = registerBlock("volcanic_brick_slab",
            () -> new SlabBlock(Properties.ofFullCopy(VOLCANIC_BRICKS.get())));
    public static final DeferredBlock<StairBlock> VOLCANIC_BRICK_STAIRS = registerBlock("volcanic_brick_stairs",
            () -> new StairBlock(VOLCANIC_BRICKS.get().defaultBlockState(), Properties.ofFullCopy(VOLCANIC_BRICKS.get())));
    public static final DeferredBlock<WallBlock> VOLCANIC_BRICK_WALL = registerBlock("volcanic_brick_wall",
            () -> new WallBlock(Properties.ofFullCopy(VOLCANIC_BRICKS.get())));
    public static final DeferredBlock<Block> VOLCANIC_TILES = registerBlock("volcanic_tiles",
            () -> new Block(Properties.ofFullCopy(VOLCANIC_BRICKS.get())));
    public static final DeferredBlock<SlabBlock> VOLCANIC_TILE_SLAB = registerBlock("volcanic_tile_slab",
            () -> new SlabBlock(Properties.ofFullCopy(VOLCANIC_TILES.get())));
    public static final DeferredBlock<StairBlock> VOLCANIC_TILE_STAIRS = registerBlock("volcanic_tile_stairs",
            () -> new StairBlock(VOLCANIC_TILES.get().defaultBlockState(), Properties.ofFullCopy(VOLCANIC_TILES.get())));
    public static final DeferredBlock<WallBlock> VOLCANIC_TILE_WALL = registerBlock("volcanic_tile_wall",
            () -> new WallBlock(Properties.ofFullCopy(VOLCANIC_TILES.get())));
    public static final DeferredBlock<Block> CALAMITES_PLANKS = planks(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<StairBlock> CALAMITES_STAIRS = stairs("calamites", CALAMITES_PLANKS);
    public static final DeferredBlock<SlabBlock> CALAMITES_SLAB = slab(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<FenceBlock> CALAMITES_FENCE = fence(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<FenceGateBlock> CALAMITES_FENCE_GATE = fenceGate(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<DoorBlock> CALAMITES_DOOR = door(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<TrapDoorBlock> CALAMITES_TRAPDOOR = trapDoor(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<ButtonBlock> CALAMITES_BUTTON = woodenButton(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<PressurePlateBlock> CALAMITES_PRESSURE_PLATE = pressurePlate(ModWoodTypes.CALAMITES);
    public static final DeferredBlock<RotatedPillarBlock> CALAMITES_LOG = log(ModWoodTypes.CALAMITES, false);
    public static final DeferredBlock<RotatedPillarBlock> CALAMITES_WOOD = wood(ModWoodTypes.CALAMITES, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CALAMITES_LOG = log(ModWoodTypes.CALAMITES, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CALAMITES_WOOD = wood(ModWoodTypes.CALAMITES, true);
    public static final DeferredBlock<LeavesBlock> CALAMITES_LEAVES = registerBlock("calamites_leaves", () -> FossilLeavesBlock.get(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> CALAMITES_SAPLING = registerBlock("calamites_sapling",
            () -> new SaplingBlock(CalamitesTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<Block> CORDAITES_PLANKS = planks(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<StairBlock> CORDAITES_STAIRS = stairs("cordaites", CORDAITES_PLANKS);
    public static final DeferredBlock<SlabBlock> CORDAITES_SLAB = slab(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<FenceBlock> CORDAITES_FENCE = fence(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<FenceGateBlock> CORDAITES_FENCE_GATE = fenceGate(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<DoorBlock> CORDAITES_DOOR = door(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<TrapDoorBlock> CORDAITES_TRAPDOOR = trapDoor(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<ButtonBlock> CORDAITES_BUTTON = woodenButton(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<PressurePlateBlock> CORDAITES_PRESSURE_PLATE = pressurePlate(ModWoodTypes.CORDAITES);
    public static final DeferredBlock<RotatedPillarBlock> CORDAITES_LOG = log(ModWoodTypes.CORDAITES, false);
    public static final DeferredBlock<RotatedPillarBlock> CORDAITES_WOOD = wood(ModWoodTypes.CORDAITES, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CORDAITES_LOG = log(ModWoodTypes.CORDAITES, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CORDAITES_WOOD = wood(ModWoodTypes.CORDAITES, true);
    public static final DeferredBlock<LeavesBlock> CORDAITES_LEAVES = registerBlock("cordaites_leaves", () -> FossilLeavesBlock.get(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> CORDAITES_SAPLING = registerBlock("cordaites_sapling",
            () -> new SaplingBlock(CordaitesTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> MUTANT_TREE_PLANKS = planks(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<StairBlock> MUTANT_TREE_STAIRS = stairs("mutant_tree", MUTANT_TREE_PLANKS);
    public static final DeferredBlock<SlabBlock> MUTANT_TREE_SLAB = slab(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<FenceBlock> MUTANT_TREE_FENCE = fence(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<FenceGateBlock> MUTANT_TREE_FENCE_GATE = fenceGate(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<DoorBlock> MUTANT_TREE_DOOR = door(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<TrapDoorBlock> MUTANT_TREE_TRAPDOOR = trapDoor(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<ButtonBlock> MUTANT_TREE_BUTTON = woodenButton(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<PressurePlateBlock> MUTANT_TREE_PRESSURE_PLATE = pressurePlate(ModWoodTypes.MUTANT_TREE);
    public static final DeferredBlock<RotatedPillarBlock> MUTANT_TREE_LOG = log(ModWoodTypes.MUTANT_TREE, false);
    public static final DeferredBlock<RotatedPillarBlock> MUTANT_TREE_WOOD = wood(ModWoodTypes.MUTANT_TREE, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_MUTANT_TREE_LOG = log(ModWoodTypes.MUTANT_TREE, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_MUTANT_TREE_WOOD = wood(ModWoodTypes.MUTANT_TREE, true);
    public static final DeferredBlock<LeavesBlock> MUTANT_TREE_LEAVES = registerBlock("mutant_tree_leaves", () -> FossilLeavesBlock.get(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> MUTANT_TREE_SAPLING = registerBlock("mutant_tree_sapling",
            () -> new SaplingBlock(MutantTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<Block> MUTANT_TREE_TUMOR = blockWithDebugItem("mutant_tree_tumor",
            () -> new MutantTreeTumor(Properties.of().ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .noOcclusion().dynamicShape().sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> MUTANT_TREE_VINE = registerBlock("mutant_tree_vine",
            () -> new VineBlock(Properties.of().mapColor(MapColor.PLANT).replaceable().ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .noCollission().lightLevel(value -> 10).randomTicks().strength(0.2f).sound(SoundType.VINE)));

    public static final DeferredBlock<Block> PALM_PLANKS = planks(ModWoodTypes.PALM);
    public static final DeferredBlock<StairBlock> PALM_STAIRS = stairs("palm", PALM_PLANKS);
    public static final DeferredBlock<SlabBlock> PALM_SLAB = slab(ModWoodTypes.PALM);
    public static final DeferredBlock<FenceBlock> PALM_FENCE = fence(ModWoodTypes.PALM);
    public static final DeferredBlock<FenceGateBlock> PALM_FENCE_GATE = fenceGate(ModWoodTypes.PALM);
    public static final DeferredBlock<DoorBlock> PALM_DOOR = door(ModWoodTypes.PALM);
    public static final DeferredBlock<TrapDoorBlock> PALM_TRAPDOOR = trapDoor(ModWoodTypes.PALM);
    public static final DeferredBlock<ButtonBlock> PALM_BUTTON = woodenButton(ModWoodTypes.PALM);
    public static final DeferredBlock<PressurePlateBlock> PALM_PRESSURE_PLATE = pressurePlate(ModWoodTypes.PALM);
    public static final DeferredBlock<RotatedPillarBlock> PALM_LOG = log(ModWoodTypes.PALM, false);
    public static final DeferredBlock<RotatedPillarBlock> PALM_WOOD = wood(ModWoodTypes.PALM, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_PALM_LOG = log(ModWoodTypes.PALM, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_PALM_WOOD = wood(ModWoodTypes.PALM, true);
    public static final DeferredBlock<LeavesBlock> PALM_LEAVES = registerBlock("palm_leaves",
            () -> FossilLeavesBlock.get(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> PALM_SAPLING = registerBlock("palm_sapling",
            () -> new SaplingBlock(PalmTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> SIGILLARIA_PLANKS = planks(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<StairBlock> SIGILLARIA_STAIRS = stairs("sigillaria", SIGILLARIA_PLANKS);
    public static final DeferredBlock<SlabBlock> SIGILLARIA_SLAB = slab(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<FenceBlock> SIGILLARIA_FENCE = fence(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<FenceGateBlock> SIGILLARIA_FENCE_GATE = fenceGate(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<DoorBlock> SIGILLARIA_DOOR = door(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<TrapDoorBlock> SIGILLARIA_TRAPDOOR = trapDoor(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<ButtonBlock> SIGILLARIA_BUTTON = woodenButton(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<PressurePlateBlock> SIGILLARIA_PRESSURE_PLATE = pressurePlate(ModWoodTypes.SIGILLARIA);
    public static final DeferredBlock<RotatedPillarBlock> SIGILLARIA_LOG = log(ModWoodTypes.SIGILLARIA, false);
    public static final DeferredBlock<RotatedPillarBlock> SIGILLARIA_WOOD = wood(ModWoodTypes.SIGILLARIA, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SIGILLARIA_LOG = log(ModWoodTypes.SIGILLARIA, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SIGILLARIA_WOOD = wood(ModWoodTypes.SIGILLARIA, true);
    public static final DeferredBlock<LeavesBlock> SIGILLARIA_LEAVES = registerBlock("sigillaria_leaves", () -> FossilLeavesBlock.get(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> SIGILLARIA_SAPLING = registerBlock("sigillaria_sapling",
            () -> new SaplingBlock(SigillariaTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> TEMPSKYA_PLANKS = planks(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<StairBlock> TEMPSKYA_STAIRS = stairs("tempskya", TEMPSKYA_PLANKS);
    public static final DeferredBlock<SlabBlock> TEMPSKYA_SLAB = slab(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<FenceBlock> TEMPSKYA_FENCE = fence(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<FenceGateBlock> TEMPSKYA_FENCE_GATE = fenceGate(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<DoorBlock> TEMPSKYA_DOOR = door(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<TrapDoorBlock> TEMPSKYA_TRAPDOOR = trapDoor(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<ButtonBlock> TEMPSKYA_BUTTON = woodenButton(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<PressurePlateBlock> TEMPSKYA_PRESSURE_PLATE = pressurePlate(ModWoodTypes.TEMPSKYA);
    public static final DeferredBlock<RotatedPillarBlock> TEMPSKYA_LOG = log(ModWoodTypes.TEMPSKYA, false);
    public static final DeferredBlock<RotatedPillarBlock> TEMPSKYA_WOOD = wood(ModWoodTypes.TEMPSKYA, false);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_TEMPSKYA_LOG = log(ModWoodTypes.TEMPSKYA, true);
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_TEMPSKYA_WOOD = wood(ModWoodTypes.TEMPSKYA, true);
    public static final DeferredBlock<Block> TEMPSKYA_SAPLING = registerBlock("tempskya_sapling",
            () -> new SaplingBlock(TempskyaTreeGrower.GROWER, Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<Block> TEMPSKYA_TOP = registerBlock("tempskya_top",
            () -> new TempskyaTopBlock(Properties.of().mapColor(MapColor.PLANT).ignitedByLava().pushReaction(PushReaction.DESTROY).noOcclusion().sound(SoundType.GRASS)));
    public static final DeferredBlock<Block> TEMPSKYA_LEAF = registerBlock("tempskya_leaf",
            () -> new TempskyaLeafBlock(Properties.of().mapColor(MapColor.PLANT).ignitedByLava().pushReaction(PushReaction.DESTROY).noCollission().noOcclusion().sound(SoundType.GRASS)));

    public static final List<DeferredBlock<VaseBlock>> VASES = new ArrayList<>();
    public static final List<Pair<DyeColor, DeferredBlock<VaseBlock>>> VASES_WITH_COLOR = new ArrayList<>();
    public static final DeferredBlock<VaseBlock> VOLUTE_VASE_DAMAGED = volute(VaseBlock.VaseVariant.DAMAGED);
    public static final DeferredBlock<VaseBlock> VOLUTE_VASE_RESTORED = volute(VaseBlock.VaseVariant.RESTORED);
    public static final DeferredBlock<VaseBlock> KYLIX_VASE_DAMAGED = kylix(VaseBlock.VaseVariant.DAMAGED);
    public static final DeferredBlock<VaseBlock> KYLIX_VASE_RESTORED = kylix(VaseBlock.VaseVariant.RESTORED);
    public static final DeferredBlock<VaseBlock> AMPHORA_VASE_DAMAGED = amphora(VaseBlock.VaseVariant.DAMAGED);
    public static final DeferredBlock<VaseBlock> AMPHORA_VASE_RESTORED = amphora(VaseBlock.VaseVariant.RESTORED);
    public static final List<DeferredBlock<FigurineBlock>> FIGURINES = new ArrayList<>();
    public static final DeferredBlock<FigurineBlock> ANU_FIGURINE_DESTROYED = registerAnu(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> ANU_FIGURINE_RESTORED = registerAnu(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> ANU_FIGURINE_PRISTINE = registerAnu(FigurineBlock.FigurineVariant.PRISTINE);
    public static final DeferredBlock<FigurineBlock> ENDERMAN_FIGURINE_DESTROYED = registerEnderman(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> ENDERMAN_FIGURINE_RESTORED = registerEnderman(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> ENDERMAN_FIGURINE_PRISTINE = registerEnderman(FigurineBlock.FigurineVariant.PRISTINE);
    public static final DeferredBlock<FigurineBlock> PIGLIN_FIGURINE_DESTROYED = registerPiglin(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> PIGLIN_FIGURINE_RESTORED = registerPiglin(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> PIGLIN_FIGURINE_PRISTINE = registerPiglin(FigurineBlock.FigurineVariant.PRISTINE);
    public static final DeferredBlock<FigurineBlock> SKELETON_FIGURINE_DESTROYED = registerSkeleton(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> SKELETON_FIGURINE_RESTORED = registerSkeleton(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> SKELETON_FIGURINE_PRISTINE = registerSkeleton(FigurineBlock.FigurineVariant.PRISTINE);
    public static final DeferredBlock<FigurineBlock> STEVE_FIGURINE_DESTROYED = registerSteve(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> STEVE_FIGURINE_RESTORED = registerSteve(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> STEVE_FIGURINE_PRISTINE = registerSteve(FigurineBlock.FigurineVariant.PRISTINE);
    public static final DeferredBlock<FigurineBlock> ZOMBIE_FIGURINE_DESTROYED = registerZombie(FigurineBlock.FigurineVariant.DESTROYED);
    public static final DeferredBlock<FigurineBlock> ZOMBIE_FIGURINE_RESTORED = registerZombie(FigurineBlock.FigurineVariant.RESTORED);
    public static final DeferredBlock<FigurineBlock> ZOMBIE_FIGURINE_PRISTINE = registerZombie(FigurineBlock.FigurineVariant.PRISTINE);

    static {
        for (DyeColor color : DyeColor.values()) {
            VASES_WITH_COLOR.add(new Pair<>(color, vase("amphora", color.getSerializedName(), AmphoraVaseBlock::new)));
            VASES_WITH_COLOR.add(new Pair<>(color, vase("kylix", color.getSerializedName(), KylixVaseBlock::new)));
            VASES_WITH_COLOR.add(new Pair<>(color, vase("volute", color.getSerializedName(), VoluteVaseBlock::new)));
        }
    }

    private static ToIntFunction<BlockState> activeBlockEmission(int lightValue) {
        return arg -> Boolean.TRUE.equals(arg.getValue(CustomEntityBlock.ACTIVE)) ? lightValue : 0;
    }

    private static BlockBehaviour.Properties woodProp(ModWoodTypes.WoodInfo woodInfo) {
        return Properties.of().mapColor(woodInfo.mapColor()).ignitedByLava().instrument(BASS).strength(2, 3).sound(woodInfo.woodType().soundType());
    }

    private static DeferredBlock<Block> planks(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_planks", () -> new Block(woodProp(woodInfo)));
    }

    private static DeferredBlock<StairBlock> stairs(String name, DeferredBlock<Block> base) {
        return registerBlock(name + "_stairs", () -> new StairBlock(base.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(base.get())));
    }

    private static DeferredBlock<SlabBlock> slab(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_slab", () -> new SlabBlock(woodProp(woodInfo)));
    }

    private static DeferredBlock<FenceBlock> fence(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_fence", () -> new FenceBlock(woodProp(woodInfo)));
    }

    private static DeferredBlock<FenceGateBlock> fenceGate(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_fence_gate", () -> new FenceGateBlock(woodInfo.woodType(), woodProp(woodInfo)));
    }

    private static DeferredBlock<DoorBlock> door(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_door", () -> new DoorBlock(woodInfo.setType(), woodProp(woodInfo).strength(3).noOcclusion()));
    }

    private static DeferredBlock<TrapDoorBlock> trapDoor(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_trapdoor", () -> new TrapDoorBlock(woodInfo.setType(), woodProp(woodInfo)
                .strength(3).noOcclusion().isValidSpawn(ModBlocks::never)));
    }

    private static DeferredBlock<ButtonBlock> woodenButton(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_button", () -> new ButtonBlock(woodInfo.setType(), 30, Properties.of().pushReaction(PushReaction.DESTROY)
                .strength(0.5f).noCollission()));
    }

    private static DeferredBlock<PressurePlateBlock> pressurePlate(ModWoodTypes.WoodInfo woodInfo) {
        return registerBlock(woodInfo.name() + "_pressure_plate", () -> new PressurePlateBlock(woodInfo.setType(),
                woodProp(woodInfo).strength(0.5f).noCollission()));
    }

    private static DeferredBlock<RotatedPillarBlock> log(ModWoodTypes.WoodInfo woodInfo, boolean stripped) {
        return rotatedPillar((stripped ? "stripped_" : "") + woodInfo.name() + "_log", woodProp(woodInfo).strength(2));
    }

    private static DeferredBlock<RotatedPillarBlock> wood(ModWoodTypes.WoodInfo woodInfo, boolean stripped) {
        return rotatedPillar((stripped ? "stripped_" : "") + woodInfo.name() + "_wood", woodProp(woodInfo).strength(2));
    }

    private static DeferredBlock<RotatedPillarBlock> rotatedPillar(String name, Properties properties) {
        return registerBlock(name, () -> FlammableRotatedPillarBlock.get(properties));
    }

    private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static DeferredBlock<VaseBlock> volute(VaseBlock.VaseVariant variant) {
        return vase("volute", variant.getSerializedName(), VoluteVaseBlock::new);
    }

    private static DeferredBlock<VaseBlock> kylix(VaseBlock.VaseVariant variant) {
        return vase("kylix", variant.getSerializedName(), KylixVaseBlock::new);
    }

    private static DeferredBlock<VaseBlock> amphora(VaseBlock.VaseVariant variant) {
        return vase("amphora", variant.getSerializedName(), AmphoraVaseBlock::new);
    }

    private static DeferredBlock<VaseBlock> vase(String name, String variant, Supplier<VaseBlock> supplier) {
        DeferredBlock<VaseBlock> toReturn = registerBlock("vase_" + name + "_" + variant, supplier);
        VASES.add(toReturn);
        return toReturn;
    }

    private static DeferredBlock<FigurineBlock> registerAnu(FigurineBlock.FigurineVariant variant) {
        return figurine("anu", variant, () -> new FigurineAnuBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> registerEnderman(FigurineBlock.FigurineVariant variant) {
        return figurine("enderman", variant, () -> new FigurineEndermanBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> registerPiglin(FigurineBlock.FigurineVariant variant) {
        return figurine("piglin", variant, () -> new FigurinePiglinBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> registerSkeleton(FigurineBlock.FigurineVariant variant) {
        return figurine("skeleton", variant, () -> new FigurineSkeletonBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> registerSteve(FigurineBlock.FigurineVariant variant) {
        return figurine("steve", variant, () -> new FigurineSteveBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> registerZombie(FigurineBlock.FigurineVariant variant) {
        return figurine("zombie", variant, () -> new FigurineZombieBlock(variant));
    }

    private static DeferredBlock<FigurineBlock> figurine(String name, FigurineBlock.FigurineVariant variant, Supplier<FigurineBlock> supplier) {
        DeferredBlock<FigurineBlock> toReturn = registerBlock("figurine_" + name + "_" + variant.getSerializedName(), supplier);
        FIGURINES.add(toReturn);
        return toReturn;
    }

    public static DeferredBlock<ShortFlowerBlock> registerShortFlower(String name, VoxelShape shape) {
        return registerBlock(name, () -> new ShortFlowerBlock(shape));
    }

    public static DeferredBlock<TallFlowerBlock> registerTallFlower(String name, VoxelShape shape) {
        return registerBlock(name, () -> new TallFlowerBlock(shape));
    }

    public static DeferredBlock<FourTallFlowerBlock> registerFourTallFlower(String name, VoxelShape shape) {
        return registerBlock(name, () -> new FourTallFlowerBlock(shape));
    }

    public static DeferredBlock<GrowableFlowerBlock> registerGrowableFlower(String name, Supplier<? extends BushBlock> tallFlower,
                                                                             VoxelShape shape) {
        return registerBlock(name, () -> new GrowableFlowerBlock(tallFlower, shape));
    }

    public static <T extends Block> DeferredBlock<T> blockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> DeferredBlock<T> blockWithCustomBlockItem(String name, Supplier<T> block,
                                                                                Function<T, CustomBlockItem> blockItem) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        DeferredItem<Item> item = ModItems.ITEMS.register(name, () -> blockItem.apply(toReturn.get()));
        ModTabs.addToBlockTab(item);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> blockWithDebugItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        if (Version.debugEnabled()) {
            DeferredItem<Item> item = registerBlockItem(name, toReturn);
            ModTabs.addToBlockTab(item);
        }
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        DeferredItem<Item> item = registerBlockItem(name, toReturn);
        ModTabs.addToBlockTab(item);
        return toReturn;
    }

    private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block) {
        return ModItems.ITEMS.register(name, () -> new CustomBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        PrehistoricPlantInfo.register();
        BLOCKS.register(modEventBus);
    }
}
