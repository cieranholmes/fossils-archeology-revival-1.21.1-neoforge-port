package com.github.fossils.block.entity;

import com.github.fossils.inventory.FeederMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FeederBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private static final int SIZE = 3;
    private static final int MEAT_SLOT_ID = 0;
    private static final int PLANT_SLOT_ID = 1;
    private static final int FISH_SLOT_ID = 2;
    private static final int[] SLOTS_TOP = new int[]{MEAT_SLOT_ID, PLANT_SLOT_ID, FISH_SLOT_ID};
    private static final String[] NAMES = new String[]{"Meat", "Plant", "Fish"};
    private final int[] foodStored = new int[SIZE];
    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            return foodStored[index];
        }

        @Override
        public void set(int index, int value) {
            foodStored[index] = value;
        }

        @Override
        public int getCount() {
            return SIZE;
        }
    };
    protected NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public FeederBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FEEDER.get(), blockPos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        for (int i = 0; i < NAMES.length; i++) {
            foodStored[i] = tag.getShort(NAMES[i]);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        for (int i = 0; i < NAMES.length; i++) {
            tag.putShort(NAMES[i], (short) foodStored[i]);
        }
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.fossils.feeder");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FeederMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public int getContainerSize() {
        return items.size();
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
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public int getSignalStrength() {
        int total = 0;
        for (int value : foodStored) {
            total += value;
        }
        return total;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    private boolean isFood(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return isFood(index, stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side != Direction.DOWN ? SLOTS_TOP : new int[]{};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
        return canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    public int getValue(int slotIndex) {
        return foodStored[slotIndex];
    }
}
