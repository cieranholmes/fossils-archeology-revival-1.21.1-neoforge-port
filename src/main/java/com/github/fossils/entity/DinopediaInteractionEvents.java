package com.github.fossils.entity;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.capabilities.ModCapabilities;
import com.github.fossils.client.gui.DinopediaScreen;
import com.github.fossils.entity.prehistoric.base.DinosaurEgg;
import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossils.entity.prehistoric.base.PrehistoricFish;
import com.github.fossils.item.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Opens the Dinopedia screen when right-clicking a prehistoric entity/egg/Quagga (or a mammal with an
 * embryo) while holding {@link ModItems#DINOPEDIA}. Ports source's Architectury
 * {@code InteractionEvent.INTERACT_ENTITY} handler from {@code ClientInit.registerEventHandlers()} onto
 * NeoForge's {@link PlayerInteractEvent.EntityInteract}, which - like the Architectury event - fires on
 * both logical sides.
 * <p>
 * This class is intentionally registered on the common bus (not {@code Dist.CLIENT}) so the
 * server-side branch (cancelling the interaction for a mammal with an embryo, so nothing else consumes
 * the click) still runs on a real dedicated server, not just in singleplayer. Only the actual
 * screen-opening call is delegated to {@link DinopediaScreen#open}, a client-only method - that keeps
 * client-only types (Minecraft, Screen) out of this method's own bytecode, which is what makes it safe
 * for a common-bus-registered class to reach into client code behind an {@code isClientSide} check.
 */
@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class DinopediaInteractionEvents {

    @SubscribeEvent
    static void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (!player.getItemInHand(event.getHand()).is(ModItems.DINOPEDIA.get())) {
            return;
        }
        Entity target = event.getTarget();
        if (event.getLevel().isClientSide) {
            if (target instanceof LivingEntity livingEntity) {
                if (target instanceof Animal animal && PrehistoricEntityInfo.isMammal(animal) && ModCapabilities.hasEmbryo(animal)) {
                    DinopediaScreen.open(animal);
                } else if (target instanceof DinosaurEgg || target instanceof Prehistoric || target instanceof PrehistoricFish || target instanceof Quagga) {
                    DinopediaScreen.open(livingEntity);
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        } else if (target instanceof Animal animal && PrehistoricEntityInfo.isMammal(animal) && ModCapabilities.hasEmbryo(animal)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
