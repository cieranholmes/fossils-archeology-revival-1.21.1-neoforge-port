package com.github.fossil.entity.prehistoric.swimming;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Lonchodomas extends Trilobite {

    public Lonchodomas(EntityType<Lonchodomas> entityType, Level level) {
        super(entityType, level);
        textureLocation = FossilsAndArcheologyRevival.location("textures/entity/lonchodomas/lonchodomas.png");
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.LONCHODOMAS;
    }
}
