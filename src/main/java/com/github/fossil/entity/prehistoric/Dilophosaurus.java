package com.github.fossil.entity.prehistoric;

import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Dilophosaurus extends Prehistoric {
    public static final String ANIMATIONS = "dilophosaurus.animation.json";
    public static final String ATTACK1 = "animation.dilophosaurus.attack1";
    public static final String ATTACK2 = "animation.dilophosaurus.attack2";
    public static final String ATTACK3 = "animation.dilophosaurus.attack3";
    public static final String CALL = "animation.dilophosaurus.call";
    public static final String EAT = "animation.dilophosaurus.eat";
    public static final String FALL = "animation.dilophosaurus.jump/fall";
    public static final String IDLE = "animation.dilophosaurus.idle";
    public static final String INFLATE_START = "animation.dilophosaurus.inflate_start";
    public static final String INFLATE_HOLD = "animation.dilophosaurus.inflate_hold";
    public static final String INFLATE_END = "animation.dilophosaurus.inflate_end";
    public static final String RUN = "animation.dilophosaurus.run";
    public static final String SIT1 = "animation.dilophosaurus.sit1";
    public static final String SIT2 = "animation.dilophosaurus.sit2";
    public static final String SLEEP1 = "animation.dilophosaurus.sleep1";
    public static final String SLEEP2 = "animation.dilophosaurus.sleep2";
    public static final String SPEAK = "animation.dilophosaurus.speak";
    public static final String SWIM = "animation.dilophosaurus.swim";
    public static final String WALK = "animation.dilophosaurus.walk";


    public Dilophosaurus(EntityType<Dilophosaurus> type, Level level) {
        super(type, level);
    }

    @Override
    public PrehistoricEntityInfo info() {
        return PrehistoricEntityInfo.DILOPHOSAURUS;
    }

    @Override
    public Item getOrderItem() {
        return Items.BONE;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0, -0.25, 0);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.DILOPHOSAURUS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.DILOPHOSAURUS_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.DILOPHOSAURUS_DEATH.get();
    }
}
