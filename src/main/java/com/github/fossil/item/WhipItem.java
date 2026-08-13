package com.github.fossil.item;

import com.github.fossil.entity.prehistoric.base.Prehistoric;
import com.github.fossil.sounds.ModSounds;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WhipItem extends Item {
    public WhipItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && player.isPassenger() && player.getVehicle() instanceof Prehistoric) {
            player.getItemInHand(usedHand).hurtAndBreak(1, player, usedHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        player.playSound(ModSounds.WHIP.get(), 1, 1);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }
}
