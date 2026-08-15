package com.github.fossil.block.entity;

import com.github.fossil.Config;
import com.github.fossil.block.custom_blocks.CultureVatBlock;
import com.github.fossil.inventory.CultureVatMenu;
import com.github.fossil.recipe.CultureVatRecipe;
import com.github.fossil.recipe.ModRecipes;
import com.github.fossil.recipe.WithFuelRecipe;
import com.github.fossil.tags.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    private ItemStack fuel = ItemStack.EMPTY;

    public CultureVatBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CULTURE_VAT.get(), blockPos, blockState);
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FuelItem")) {
            fuel = ItemStack.parse(registries, tag.getCompound("FuelItem")).orElse(ItemStack.EMPTY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FuelItem", fuel.saveOptional(registries));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CultureVatBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (Config.machinesRequireEnergy && energyStorage.getEnergy() <= 0) {
            if (cookingProgress > 0) {
                cookingProgress = Mth.clamp(cookingProgress - 2, 0, CultureVatMenu.CULTIVATION_DURATION);
            }
            return;
        }
        int prevCookingProgress = cookingProgress;
        boolean wasFueled = litTime > 0;
        boolean wasProcessing = cookingProgress > 0;
        boolean dirty = false;
        if (litTime > 0) {
            --litTime;
        }

        if (canProcess() && (litTime == 0 || (litTime > 0 && !canProcess(fuel)))) {
            ItemStack fuelStack = items.get(FUEL_SLOT_ID);
            litDuration = litTime = getRecipeFuelTime(items.get(INPUT_SLOT_ID), fuelStack);
            fuel = fuelStack.copy();
            if (litTime > 0) {
                dirty = true;
                fuelStack.shrink(1);
                if (fuelStack.isEmpty() && fuelStack.getItem().hasCraftingRemainingItem()) {
                    items.set(FUEL_SLOT_ID, new ItemStack(fuelStack.getItem().getCraftingRemainingItem()));
                }
            }
        }

        if (litTime > 0 && canProcess(fuel)) {
            cookingProgress++;
            if (cookingProgress >= CultureVatMenu.CULTIVATION_DURATION) {
                cookingProgress = 0;
                createItem();
                dirty = true;
            }
        }
        if (prevCookingProgress != cookingProgress && Config.machinesRequireEnergy) {
            energyStorage.extractEnergy(Config.machineEnergyUsage);
        }
        if (litTime == 0 && cookingProgress > 0 || litTime > 0 && cookingProgress > 0 && !canProcess(fuel)) {
            cookingProgress = Mth.clamp(cookingProgress - 2, 0, CultureVatMenu.CULTIVATION_DURATION);
        }

        if (wasFueled != litTime > 0) {
            dirty = true;
            state = state.setValue(CultureVatBlock.ACTIVE, litTime > 0);
            level.setBlock(pos, state, 3);
        }

        if (wasProcessing != cookingProgress > 0) {
            dirty = true;
            state = state.setValue(CultureVatBlock.EMBRYO, getDNAType());
            level.setBlock(pos, state, 3);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }

        if (cookingProgress == 3001 && level.getRandom().nextInt(100) < Config.cultureVatFailChance) {
            ((CultureVatBlock) state.getBlock()).onFailedCultivation(level, pos);
        }
    }

    private ItemStack checkCultivate(ItemStack input, ItemStack fuel) {
        CultureVatRecipe recipe = ModRecipes.getCultureVatRecipeForItem(WithFuelRecipe.WithFuelInput.of(input, fuel), level);
        if (recipe != null) {
            return recipe.getResultItem(level.registryAccess());
        }
        return ItemStack.EMPTY;
    }

    private int getRecipeFuelTime(ItemStack inputStack, ItemStack fuelStack) {
        CultureVatRecipe recipe = ModRecipes.getCultureVatRecipeForItem(WithFuelRecipe.WithFuelInput.of(inputStack, fuelStack), level);
        if (recipe != null) {
            return recipe.getFuelDuration();
        }
        return ModRecipes.getCultureVatFuelValue(fuelStack.getItem());
    }

    protected boolean canProcess(ItemStack fuelStack) {
        if (Config.machinesRequireEnergy && energyStorage.getEnergy() <= Config.machineEnergyUsage) {
            return false;
        }
        ItemStack inputStack = items.get(INPUT_SLOT_ID);
        if (inputStack.isEmpty() || fuelStack.isEmpty()) {
            return false;
        }
        ItemStack result = checkCultivate(inputStack, fuelStack);
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(OUTPUT_SLOT_ID);
        return output.isEmpty() || ItemStack.isSameItem(output, result);
    }

    protected boolean canProcess() {
        return canProcess(items.get(FUEL_SLOT_ID));
    }

    protected void createItem() {
        if (canProcess(fuel)) {
            ItemStack result = checkCultivate(items.get(INPUT_SLOT_ID), fuel);
            ItemStack output = items.get(OUTPUT_SLOT_ID);
            if (output.isEmpty()) {
                items.set(OUTPUT_SLOT_ID, result);
            } else if (ItemStack.isSameItem(output, result)) {
                output.grow(result.getCount());
            }
            items.get(INPUT_SLOT_ID).shrink(1);
        }
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
        return ModRecipes.isCultureVatFuel(stack.getItem());
    }

    private boolean isValidCultureVatInput(ItemStack stack) {
        return ModRecipes.getCultureVatRecipeForItem(stack, level) != null;
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
