package com.github.fossils.capabilities;

import com.github.fossils.entity.prehistoric.base.EntityInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class MammalData implements INBTSerializable<CompoundTag> {
    private int embryoProgress;
    @Nullable
    private EntityInfo embryo;

    public MammalData() {
    }

    public MammalData(@Nullable EntityInfo embryo, int embryoProgress) {
        this.embryo = embryo;
        this.embryoProgress = embryoProgress;
    }

    public int getEmbryoProgress() {
        return embryoProgress;
    }

    public void setEmbryoProgress(int embryoProgress) {
        this.embryoProgress = embryoProgress;
    }

    @Nullable
    public EntityInfo getEmbryo() {
        return embryo;
    }

    public void setEmbryo(@Nullable EntityInfo embryo) {
        this.embryo = embryo;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("embryoProgress", embryoProgress);
        if (embryo != null) {
            tag.putString("embryo", embryo.name());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        setEmbryoProgress(tag.getInt("embryoProgress"));
        try {
            setEmbryo(EntityInfo.fromNbt(tag.getString("embryo")));
        } catch (IllegalArgumentException e) {
            setEmbryo(null);
        }
    }
}
