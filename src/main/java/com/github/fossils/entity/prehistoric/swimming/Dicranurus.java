package com.github.fossils.entity.prehistoric.swimming;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Dicranurus extends Trilobite {

    public Dicranurus(EntityType<Dicranurus> entityType, Level level) {
        super(entityType, level);
        textureLocation = FossilsAndArcheologyRevival.location("textures/entity/dicranurus/dicranurus.png");
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.DICRANURUS;
    }
}
