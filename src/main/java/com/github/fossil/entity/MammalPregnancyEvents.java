package com.github.fossil.entity;

import com.github.fossil.Config;
import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.advancements.ModTriggers;
import com.github.fossil.capabilities.ModCapabilities;
import com.github.fossil.entity.prehistoric.base.EntityInfo;
import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.util.Util;
import com.github.fossil.item.MammalEmbryoItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Mammal embryo implant + pregnancy progression, replacing source's Architectury InteractionEvent.INTERACT_ENTITY
 * (embryo branch only - the WHIP branch is unrelated to this and handled elsewhere) and Forge-side
 * LivingEvent.LivingTickEvent. The progress-increment branch deliberately runs on both logical sides every tick,
 * same as source - each side just increments its own local attachment copy in lockstep, no packet involved
 * (ModCapabilities.setEmbryoProgress doesn't sync). Only pregnancy start/stop (via ModCapabilities.startPregnancy/
 * stopPregnancy) actually syncs, and growEntity (the real entity-spawning) is strictly server-side, matching
 * source's ForgeModEvents.onLivingUpdate exactly. Source's growEntity also had a client-side "spawn heart
 * particles" branch, but since the only real call site (onLivingUpdate) already guards the call to server-only,
 * that branch was unreachable dead code there - not ported.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class MammalPregnancyEvents {

    @SubscribeEvent
    static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (stack.getItem() instanceof MammalEmbryoItem embryoItem && event.getTarget() instanceof Animal animal
                && PrehistoricEntityInfo.isMammal(animal) && !animal.isBaby()) {
            if (ModCapabilities.hasEmbryo(animal)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
                return;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                ModTriggers.IMPLANT_EMBRYO_TRIGGER.get().trigger(serverPlayer, stack);
                ModCapabilities.startPregnancy(animal, embryoItem.getInfo());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else {
                Util.spawnParticles(animal, ParticleTypes.SMOKE, 7);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    static void onTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Animal animal) {
            int currentProgress = ModCapabilities.getEmbryoProgress(animal);
            if (currentProgress == 0) {
                return;
            }
            if (currentProgress >= Config.pregnancyDuration) {
                if (!animal.level().isClientSide) {
                    growEntity(ModCapabilities.getEmbryo(animal), animal);
                    ModCapabilities.stopPregnancy(animal);
                }
            } else {
                ModCapabilities.setEmbryoProgress(animal, currentProgress + 1);
            }
        }
    }

    private static void growEntity(EntityInfo embryo, LivingEntity parent) {
        ServerLevel level = (ServerLevel) parent.level();
        RandomSource random = level.random;
        Entity newEntity = embryo.entityType().create(level);
        if (newEntity == null) {
            return;
        }
        int result = random.nextInt(100);
        if (newEntity instanceof AbstractHorse newHorse) {
            if (parent instanceof AbstractHorse parentHorse && parent.getClass().equals(newEntity.getClass())) {
                if (result < 10) {
                    if (parentHorse.getOwnerUUID() != null) {
                        newHorse.setOwnerUUID(parentHorse.getOwnerUUID());
                        newHorse.setTamed(true);
                    }
                } else {
                    newEntity = parentHorse.getBreedOffspring(level, parentHorse);
                }
            } else {
                newHorse.finalizeSpawn(level, level.getCurrentDifficultyAt(newHorse.blockPosition()), MobSpawnType.BREEDING, null);
            }
        } else if (newEntity instanceof Quagga newQuagga) {
            double health = parent.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + newQuagga.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + newQuagga.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
            newQuagga.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health / 3);
            double speed = parent.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() + newQuagga.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() + newQuagga.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            newQuagga.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed / 3);
        } else if (newEntity instanceof AgeableMob ageableMob) {
            if (parent.getClass().equals(newEntity.getClass())) {
                newEntity = ((AgeableMob) parent).getBreedOffspring(level, ageableMob);
            } else {
                ageableMob.finalizeSpawn(level, level.getCurrentDifficultyAt(parent.blockPosition()), MobSpawnType.BREEDING, null);
            }
        }
        if (newEntity instanceof Prehistoric prehistoric) {
            Player player = level.getNearestPlayer(parent, 15);
            if (player != null) {
                prehistoric.tame(player);
            }
            prehistoric.setAgeInDays(0);
        } else if (newEntity instanceof Animal animalOffspring) {
            animalOffspring.setBaby(true);
        }
        newEntity.moveTo(parent.getX(), parent.getY(), parent.getZ(), parent.getYRot(), parent.getXRot());
        level.addFreshEntity(newEntity);
    }
}
