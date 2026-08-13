package com.github.fossil.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vanilla's {@code BedBlockEntity} always reports {@code BlockEntityType.BED} (hardcoded in both its
 * constructors), and that type's registered "valid blocks" whitelist is a fixed set of the 16 vanilla
 * dye-colored bed blocks - {@code fossil:comfy_bed} isn't in it. That mismatch is invisible during normal
 * gameplay placement, but structure-template placement (e.g. the Anu Castle NBT) validates the live block
 * entity's type against what's recorded in the template and throws if they don't match. This subclass
 * overrides {@link #getType()} (NeoForge-patched to be non-final specifically for this scenario - see the
 * "Neo: always use getType()" comment on the base class) to report our own registered type instead, while
 * still extending {@code BedBlockEntity} so vanilla's {@code BedRenderer} (typed to {@code BedBlockEntity})
 * keeps working unchanged.
 */
public class ComfyBedBlockEntity extends BedBlockEntity {
    public ComfyBedBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState, DyeColor.WHITE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.COMFY_BED.get();
    }
}
