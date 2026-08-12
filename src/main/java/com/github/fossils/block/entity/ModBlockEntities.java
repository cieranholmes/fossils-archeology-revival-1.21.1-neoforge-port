package com.github.fossils.block.entity;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnalyzerBlockEntity>> ANALYZER = register(AnalyzerBlockEntity::new, ModBlocks.ANALYZER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BubbleBlowerBlockEntity>> BUBBLE_BLOWER = register(BubbleBlowerBlockEntity::new, ModBlocks.BUBBLE_BLOWER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CultureVatBlockEntity>> CULTURE_VAT = register(CultureVatBlockEntity::new, ModBlocks.CULTURE_VAT);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeederBlockEntity>> FEEDER = register(FeederBlockEntity::new, ModBlocks.FEEDER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SifterBlockEntity>> SIFTER = register(SifterBlockEntity::new, ModBlocks.SIFTER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorktableBlockEntity>> WORKTABLE = register(WorktableBlockEntity::new, ModBlocks.WORKTABLE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnuBarrierBlockEntity>> ANU_BARRIER = register(AnuBarrierBlockEntity::new, ModBlocks.ANU_BARRIER_ORIGIN);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnuStatueBlockEntity>> ANU_STATUE = register(AnuStatueBlockEntity::new, ModBlocks.ANU_STATUE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnubiteStatueBlockEntity>> ANUBITE_STATUE = register(AnubiteStatueBlockEntity::new, ModBlocks.ANUBITE_STATUE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AncientChestBlockEntity>> ANCIENT_CHEST = register(AncientChestBlockEntity::new, ModBlocks.ANCIENT_CHEST);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS = register(SarcophagusBlockEntity::new, ModBlocks.SARCOPHAGUS);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FakeObsidianBlockEntity>> FAKE_OBSIDIAN = register(FakeObsidianBlockEntity::new, ModBlocks.FAKE_OBSIDIAN);

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(BlockEntityType.BlockEntitySupplier<T> supplier, DeferredBlock<? extends Block> block) {
        return BLOCK_ENTITIES.register(block.getId().getPath(), () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
