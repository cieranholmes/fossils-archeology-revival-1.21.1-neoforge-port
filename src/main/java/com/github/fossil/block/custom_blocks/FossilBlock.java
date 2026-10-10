package com.github.fossil.block.custom_blocks;

import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.tags.ModItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FossilBlock extends Block {

    public FossilBlock(Properties properties) {
        super(properties);
    }

    /**
     * The fossil loot tables (fossil:regular_fossil, fossil:deep_fossil and their arch/paleo variants) contain
     * {@code minecraft:dynamic} entries named after the bone item tags. Dynamic entries produce nothing unless the
     * block supplies a matching drop on the LootParams, so this override picks one prehistoric species per broken
     * block and binds its eight bone items to those names. Without it, every roll that lands on a bone entry
     * silently drops nothing - which is most noticeable with Paleontology, since it raises the bone weights.
     */
    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<PrehistoricEntityInfo> withBones = PrehistoricEntityInfo.entitiesWithBones();
        PrehistoricEntityInfo info = withBones.get(builder.getLevel().random.nextInt(withBones.size()));
        builder = builder
                .withDynamicDrop(ModItemTags.ARM_BONES.location(), c -> c.accept(new ItemStack(info.armBoneItem)))
                .withDynamicDrop(ModItemTags.FOOT_BONES.location(), c -> c.accept(new ItemStack(info.footBoneItem)))
                .withDynamicDrop(ModItemTags.LEG_BONES.location(), c -> c.accept(new ItemStack(info.legBoneItem)))
                .withDynamicDrop(ModItemTags.RIBCAGE_BONES.location(), c -> c.accept(new ItemStack(info.ribcageBoneItem)))
                .withDynamicDrop(ModItemTags.SKULL_BONES.location(), c -> c.accept(new ItemStack(info.skullBoneItem)))
                .withDynamicDrop(ModItemTags.TAIL_BONES.location(), c -> c.accept(new ItemStack(info.tailBoneItem)))
                .withDynamicDrop(ModItemTags.UNIQUE_BONES.location(), c -> c.accept(new ItemStack(info.uniqueBoneItem)))
                .withDynamicDrop(ModItemTags.VERTEBRAE_BONES.location(), c -> c.accept(new ItemStack(info.vertebraeBoneItem)));

        //The fossil tables roll a single entry, so Fortune is applied here as extra rolls rather than through a
        //loot function - matching the 1.20.1 behaviour.
        int count = 1;
        int fortune = fortuneLevel(builder);
        if (fortune > 0) {
            count += builder.getLevel().random.nextInt(fortune);
        }
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.addAll(super.getDrops(state, builder));
        }
        return items;
    }

    private static int fortuneLevel(LootParams.Builder builder) {
        @Nullable ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) {
            return 0;
        }
        Holder<Enchantment> fortune = builder.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FORTUNE);
        return EnchantmentHelper.getItemEnchantmentLevel(fortune, tool);
    }

    @Override
    public @NotNull SoundType getSoundType(@NotNull BlockState blockState) {
        return SoundType.STONE;
    }
}
