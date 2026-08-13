package com.github.fossil.block.entity;

import com.github.fossil.block.custom_blocks.CultureVatBlock;
import com.github.fossil.inventory.CultureVatMenu;
import com.github.fossil.tags.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CultureVatBlockEntity extends EnergyContainerBlockEntity {

    private static final int INPUT_SLOT_ID = 0;
    private static final int FUEL_SLOT_ID = 1;
    private static final int OUTPUT_SLOT_ID = 2;
    private static final int[] SLOTS_FOR_UP = new int[]{INPUT_SLOT_ID}; //Input
    private static final int[] SLOTS_FOR_SIDES = new int[]{INPUT_SLOT_ID, FUEL_SLOT_ID}; //Input, Fuel
    private static final int[] SLOTS_FOR_DOWN = new int[]{OUTPUT_SLOT_ID}; //Output
    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return litTime;
                }
                case 1 -> {
                    return litDuration;
                }
                case 2 -> {
                    return cookingProgress;
                }
                case 3 -> {
                    return energyStorage.getEnergy();
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> litTime = value;
                case 1 -> litDuration = value;
                case 2 -> cookingProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public CultureVatBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CULTURE_VAT.get(), blockPos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public CultureVatBlock.EmbryoType getDNAType() {
        ItemStack input = items.get(INPUT_SLOT_ID);
        if (input.isEmpty()) {
            return CultureVatBlock.EmbryoType.NONE;
        }
        if (input.is(ModItemTags.FOSSIL_SAPLINGS)) {
            return CultureVatBlock.EmbryoType.TREE;
        } else if (input.is(ModItemTags.DNA_PLANTS)) {
            return CultureVatBlock.EmbryoType.PLANT;
        } else if (input.is(ModItemTags.DNA_LIMBLESS)) {
            return CultureVatBlock.EmbryoType.LIMBLESS;
        } else if (input.is(ModItemTags.DNA_INSECTS)) {
            return CultureVatBlock.EmbryoType.INSECT;
        }
        return CultureVatBlock.EmbryoType.GENERIC;
    }

    private boolean isCultureVatFuel(ItemStack stack) {
        return false;
    }

    private boolean isValidCultureVatInput(ItemStack stack) {
        return false;
    }

    @Override
    protected boolean canProcess() {
        return false;
    }

    @Override
    protected void createItem() {
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.fossil.culture_vat");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new CultureVatMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack current = items.get(slot);
        boolean sameItems = !stack.isEmpty() && ItemStack.isSameItem(stack, current) && ItemStack.isSameItemSameComponents(stack, current);
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot == INPUT_SLOT_ID && !sameItems) {
            cookingProgress = 0;
            level.setBlock(getBlockPos(), getBlockState().setValue(CultureVatBlock.EMBRYO, getDNAType()), 3);
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == OUTPUT_SLOT_ID) {
            return false;
        }
        if (index == FUEL_SLOT_ID) {
            return isCultureVatFuel(stack);
        }
        return isValidCultureVatInput(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : (side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction != Direction.UP && index == OUTPUT_SLOT_ID;
    }
}
