package com.github.fossils.entity.prehistoric.fish;

import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricFish;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AlligatorGar extends PrehistoricFish {

    public AlligatorGar(EntityType<AlligatorGar> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @NotNull PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.ALLIGATOR_GAR;
    }
}
