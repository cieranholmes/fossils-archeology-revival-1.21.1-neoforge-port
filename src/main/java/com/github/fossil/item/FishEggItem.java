package com.github.fossil.item;

import com.github.fossil.Config;
import com.github.fossil.advancements.ModTriggers;
import com.github.fossil.entity.prehistoric.base.EntityInfo;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class FishEggItem extends EggItem {

    public FishEggItem(EntityInfo info) {
        super(info, "fish_egg");
    }

    @Override
    protected boolean spawnMob(ServerPlayer player, ServerLevel level, double x, double y, double z, boolean aquatic) {
        Entity entity = info.entityType().create(level);
        if (entity instanceof Mob mob) {
            ModTriggers.INCUBATE_EGG_TRIGGER.get().trigger(player, entity);
            entity.moveTo(x, y + 0.5, z, level.random.nextFloat() * 360, 0);
            if (mob instanceof Prehistoric) {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.BREEDING, new Prehistoric.PrehistoricGroupData(-1));
            } else {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.BREEDING, null);
            }
            if (Config.fishArePersistent) {
                mob.setPersistenceRequired();
            }
            level.addFreshEntity(entity);
            return true;
        }
        return false;
    }
}
