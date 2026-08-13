package com.github.fossils.item;

import com.github.fossils.entity.Javelin;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class JavelinItem extends TieredItem {
    private final boolean ancient;

    public JavelinItem(Tier tier, boolean ancient) {
        super(tier, new JavelinProperties().stacksToWithDurability(16, 30));
        this.ancient = ancient;
    }

    public JavelinItem(Tier tier) {
        this(tier, false);
    }

    //1.21.1 moved enchantments to a data-driven registry - the old Enchantments.X_ARROWS constants (Enchantment
    //instances) became ResourceKey<Enchantment> (renamed X, no "_ARROWS" suffix), needing a registry lookup to
    //get the Holder<Enchantment> that EnchantmentHelper now requires.
    private static int enchantmentLevel(Level level, net.minecraft.resources.ResourceKey<Enchantment> key, ItemStack stack) {
        Holder<Enchantment> holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
        return EnchantmentHelper.getTagEnchantmentLevel(holder, stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeCharged) {
        if (!(shooter instanceof Player player)) {
            return;
        }
        boolean infiniteAmmo = player.getAbilities().instabuild || enchantmentLevel(level, Enchantments.INFINITY, stack) > 0;
        int i = this.getUseDuration(stack, shooter) - timeCharged;
        float speed = BowItem.getPowerForTime(i);
        if (speed < 0.1) {
            return;
        }
        if (!level.isClientSide) {
            int damage = stack.getMaxDamage() - (stack.getDamageValue() + (player.getAbilities().instabuild ? 0 : 1));
            //Power/Punch are handled automatically now via firedFromWeapon (see Javelin's constructor) - only
            //Flame needs a manual read since AbstractArrow has no generic "ignite on hit" enchantment hook.
            Javelin javelin = new Javelin(level, shooter, getTier(), ancient, damage, stack);
            javelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, speed * 3, 1);
            if (speed == 1) {
                javelin.setCritArrow(true);
            }
            int flameLevel = enchantmentLevel(level, Enchantments.FLAME, stack);
            if (flameLevel > 0) {
                javelin.igniteForSeconds(100);
            }
            if (infiniteAmmo) {
                javelin.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(javelin);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1,
                1 / (level.random.nextFloat() * 0.4f + 1.2f) + speed * 0.5f);
        if (!infiniteAmmo) {
            stack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    public static class JavelinProperties extends Item.Properties {

        //durability() forces max stack size to 1 as a side effect (still true in 1.21.1) - calling stacksTo()
        //afterward overwrites that back to the real stack size via the same component-based Properties API,
        //no direct field access needed anymore (Item.Properties.maxStackSize isn't a settable field in 1.21.1).
        public JavelinProperties stacksToWithDurability(int maxStackSize, int durability) {
            durability(durability);
            stacksTo(maxStackSize);
            return this;
        }
    }
}
