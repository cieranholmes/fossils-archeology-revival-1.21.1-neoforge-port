package com.github.fossils.entity;

import com.github.fossils.sounds.ModSounds;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnuDead extends LivingEntity {
    private static final int MAX_LIFESPAN = 5960;
    private static final List<ItemStack> ARMOR = NonNullList.withSize(1, ItemStack.EMPTY);

    public AnuDead(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        noCulling = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes().add(Attributes.MAX_HEALTH, 300);
    }

    @Override
    public void travel(Vec3 travelVector) {
        setDeltaMovement(0, 0, 0);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        //Treasure-room teleport reward dropped: ModDimensions (the custom Anu dimension/worldgen system) isn't ported yet
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        if (tickCount >= MAX_LIFESPAN) {
            discard();
        }
        if (tickCount == 40) {
            playSound(ModSounds.ANU_DEATH.get(), 1, 1);
        }
        for (int i = 0; i < 2; ++i) {
            level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0.1, 0);
        }
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == damageSources().fellOutOfWorld()) {
            discard();
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 5;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        //For compatibility with Ad Astra which can't handle an empty list here
        return ARMOR;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
