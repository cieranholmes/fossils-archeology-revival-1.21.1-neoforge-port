package com.github.fossil.material;

import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * 1.21.1's Tier interface dropped the old numeric getLevel() (mining level) in favor of
 * getIncorrectBlocksForDrops(): TagKey<Block> - "blocks this tool can't correctly harvest". The old level values
 * (SCARAB=4, TOOTH_DAGGER=3) mapped to NETHERITE (can mine everything, nothing is "incorrect") and DIAMOND
 * respectively, so those vanilla incorrect-tool tags are reused here rather than 1:1 porting a numeric level.
 */
public enum ModToolTiers implements Tier {
    //Source's repair ingredient is literally the empty Ingredient (Ingredient::of, no-arg overload) - SCARAB tools
    //are intentionally not repairable via a material ingredient. Faithfully replicated, not a porting mistake.
    SCARAB(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1861, 9f, 5f, 25, Ingredient::of),
    TOOTH_DAGGER(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 250, 6.4f, 2f, 25, () -> Ingredient.of(PrehistoricEntityInfo.TYRANNOSAURUS.uniqueBoneItem));

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModToolTiers(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> supplier) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        //Deferred: PrehistoricEntityInfo.TYRANNOSAURUS.uniqueBoneItem isn't populated until
        //PrehistoricEntityInfo.register() runs during item registration, well after this enum's own
        //classload/construction - LazyLoadedValue defers the Ingredient.of(...) call until first real use
        //(an anvil repair check), by which point registration has long since completed.
        this.repairIngredient = new LazyLoadedValue<>(supplier);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
