package com.github.fossil.block.entity;

import com.github.fossil.block.custom_blocks.SifterBlock;
import com.github.fossil.inventory.SifterMenu;
import com.github.fossil.recipe.ModRecipes;
import com.github.fossil.recipe.SifterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, SifterBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean wasProcessing = cookingProgress > 0;
        boolean dirty = false;

        if (canProcess()) {
            cookingProgress++;
            if (cookingProgress >= SifterMenu.SIFTER_DURATION) {
                cookingProgress = 0;
                createItem();
                dirty = true;
            }
        } else {
            cookingProgress = Mth.clamp(cookingProgress - 2, 0, SifterMenu.SIFTER_DURATION);
        }

        if (wasProcessing != cookingProgress > 0) {
            dirty = true;
            state = state.setValue(SifterBlock.ACTIVE, cookingProgress > 0);
            level.setBlock(pos, state, 3);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    public boolean isSiftable(ItemStack stack) {
        return ModRecipes.getSifterRecipeForItem(stack, level) != null;
    }

    @Override
    protected boolean canProcess() {
        ItemStack itemStack = items.get(0);
        if (!itemStack.isEmpty() && isSiftable(itemStack)) {
            for (int outputIndex = 5; outputIndex > 0; outputIndex--) {
                if (items.get(outputIndex).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void createItem() {
        if (canProcess()) {
            SifterRecipe recipe = ModRecipes.getSifterRecipeForItem(items.get(0), level);
            ItemStack result = recipe.assemble(new SingleRecipeInput(items.get(0)), level.registryAccess()).copy();
            for (int slot = 1; slot <= 5; slot++) {
                ItemStack stackInSlot = items.get(slot);
                if (ItemStack.isSameItem(stackInSlot, result) && stackInSlot.getCount() + result.getCount() < 64) {
                    stackInSlot.grow(result.getCount());
                    shrinkInput();
                    return;
                }
            }
            for (int slot = 1; slot <= 5; slot++) {
                ItemStack stackInSlot = items.get(slot);
                if (stackInSlot.isEmpty()) {
                    items.set(slot, result);
                    shrinkInput();
                    return;
                }
            }
        }
    }

    private void shrinkInput() {
        if (items.get(0).getCount() > 1) {
            items.get(0).shrink(1);
        } else {
            items.set(0, ItemStack.EMPTY);
        }
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
