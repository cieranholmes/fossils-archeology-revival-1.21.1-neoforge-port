package com.github.fossil.block.entity;

import com.github.fossil.inventory.AnalyzerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class AnalyzerBlockEntity extends EnergyContainerBlockEntity {

    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}; //Input
    private static final int[] SLOTS_FOR_DOWN = new int[]{9, 10, 11, 12}; //Output
    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookingProgress;
                case 1 -> energyStorage.getEnergy();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                cookingProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    protected NonNullList<ItemStack> items = NonNullList.withSize(13, ItemStack.EMPTY);

    public AnalyzerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ANALYZER.get(), blockPos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    private boolean isAnalyzable(ItemStack itemStack) {
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
        return Component.translatable("container.fossil.analyzer");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AnalyzerMenu(containerId, inventory, this, dataAccess);
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
        return slot < SLOTS_FOR_DOWN[0] && isAnalyzable(stack);
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
