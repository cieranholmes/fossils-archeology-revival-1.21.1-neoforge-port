package com.github.fossils.loot;

import com.github.fossils.entity.prehistoric.base.Prehistoric;
import com.github.fossils.tags.ModItemTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * Changes the amount of food a {@link Prehistoric} drops based on age
 */
public class CustomizeToDinoFunction extends LootItemConditionalFunction {
    public static final MapCodec<CustomizeToDinoFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .and(LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(f -> f.entityTarget))
                    .apply(instance, CustomizeToDinoFunction::new)
    );

    private final LootContext.EntityTarget entityTarget;

    private CustomizeToDinoFunction(List<LootItemCondition> conditions, LootContext.EntityTarget entityTarget) {
        super(conditions);
        this.entityTarget = entityTarget;
    }

    public static LootItemConditionalFunction.Builder<?> apply(LootContext.EntityTarget target) {
        return simpleBuilder(conditions -> new CustomizeToDinoFunction(conditions, target));
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (!stack.isEmpty()) {
            Entity entity = context.getParamOrNull(entityTarget.getParam());
            if (entity instanceof Prehistoric prehistoric) {
                if (stack.is(ModItemTags.UNCOOKED_MEAT)) {
                    stack.setCount(Math.min(prehistoric.getAgeInDays(), prehistoric.data().adultAgeDays()));
                    return prehistoric.isOnFire() ? new ItemStack(prehistoric.info().cookedFoodItem, stack.getCount()) : stack;
                }
            }
        }
        return stack;
    }

    @Override
    public LootItemFunctionType<CustomizeToDinoFunction> getType() {
        return ModLootItemFunctionTypes.CUSTOMIZE_TO_DINOSAUR.get();
    }
}
