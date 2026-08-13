package com.github.fossil.block.entity;

import com.github.fossil.block.custom_blocks.CustomEntityBlock;
import com.github.fossil.inventory.WorktableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class WorktableBlockEntity extends MachineContainerBlockEntity {
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
                    return cookingTotalTime;
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
                case 3 -> cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public WorktableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.WORKTABLE.get(), blockPos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    private boolean isWorktableFuel(ItemStack stack) {
        return false;
    }

    private boolean isValidWorktableInput(ItemStack stack) {
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
        return Component.translatable("container.fossil.worktable");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new WorktableMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack itemStack = items.get(slot);
        boolean sameItem = (!stack.isEmpty() && ItemStack.isSameItem(stack, itemStack) && ItemStack.isSameItemSameComponents(stack, itemStack));
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot == INPUT_SLOT_ID && !sameItem) {
            cookingProgress = 0;
            level.setBlock(getBlockPos(), getBlockState().setValue(CustomEntityBlock.ACTIVE, false), 3);
        }
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == OUTPUT_SLOT_ID) {
            return false;
        }
        if (slot == FUEL_SLOT_ID) {
            return isWorktableFuel(stack);
        }
        return isValidWorktableInput(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : (side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction != Direction.UP && slot == OUTPUT_SLOT_ID;
    }
}
