package com.github.fossil.block.entity;

import com.github.fossil.block.custom_blocks.SarcophagusBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SarcophagusBlockEntity extends BlockEntity {
    public static final int STATE_LOCKED = 0;
    public static final int STATE_UNLOCKED = 1;
    public static final int STATE_OPENING = 2;
    public static final int STATE_CLOSING = 3;
    public static final int STATE_EVENT = 1;
    private int state;
    private int doorTimer;

    public SarcophagusBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.SARCOPHAGUS.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("State", state);
        tag.putInt("Timer", doorTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        state = tag.getInt("State");
        doorTimer = tag.getInt("Timer");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == STATE_EVENT) {
            state = type;
            if (type == STATE_OPENING) {
                doorTimer = 1;
            }
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    private static void tick(Level level, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        if (state.getValue(SarcophagusBlock.LAYER) != 0) {
            return;
        }
        if (blockEntity.state != STATE_CLOSING) {
            if (blockEntity.doorTimer > 0) {
                blockEntity.doorTimer++;
            }
            if (blockEntity.doorTimer >= 91) {
                level.setBlockAndUpdate(pos, state.setValue(SarcophagusBlock.LIT, false));
                level.setBlockAndUpdate(pos.above(), level.getBlockState(pos.above()).setValue(SarcophagusBlock.LIT, false));
                level.setBlockAndUpdate(pos.above(2), level.getBlockState(pos.above(2)).setValue(SarcophagusBlock.LIT, false));
                blockEntity.setState(STATE_CLOSING);
            }
        } else {
            if (blockEntity.doorTimer > 0) {
                blockEntity.doorTimer--;
            }
            if (blockEntity.doorTimer == 0) {
                blockEntity.setState(STATE_LOCKED);
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        if (state.getValue(SarcophagusBlock.LAYER) == 0) {
            tick(level, pos, state, blockEntity);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        if (state.getValue(SarcophagusBlock.LAYER) == 0) {
            tick(level, pos, state, blockEntity);
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDoorTimer() {
        return doorTimer;
    }

    public void setDoorTimer(int doorTimer) {
        this.doorTimer = doorTimer;
    }
}
