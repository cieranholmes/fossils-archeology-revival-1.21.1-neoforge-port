package com.github.fossil.block.entity;

import com.github.fossil.inventory.SifterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SifterBlockEntity extends MachineContainerBlockEntity {

    private static final int[] SLOTS_FOR_UP = new int[]{0}; //Input
    private static final int[] SLOTS_FOR_DOWN = new int[]{1, 2, 3, 4, 5}; //Output
    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            return index == 0 ? cookingProgress : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                cookingProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    protected NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);

    public SifterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.SIFTER.get(), blockPos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public boolean isSiftable(ItemStack stack) {
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
        return Component.translatable("container.fossil.sifter");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SifterMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && isSiftable(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_UP;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        return canPlaceItem(slot, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction != Direction.UP && slot >= SLOTS_FOR_DOWN[0];
    }
}
