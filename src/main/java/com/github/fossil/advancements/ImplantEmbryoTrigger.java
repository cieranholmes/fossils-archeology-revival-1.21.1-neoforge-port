package com.github.fossil.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Triggers whenever a player inserts an embryo into a mob
 */
public class ImplantEmbryoTrigger extends SimpleCriterionTrigger<ImplantEmbryoTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        trigger(player, instance -> instance.matches(stack));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
                        )
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> implantEmbryo(Item embryoItem) {
            return ModTriggers.IMPLANT_EMBRYO_TRIGGER.get().createCriterion(
                    new TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(embryoItem).build())));
        }

        public boolean matches(ItemStack stack) {
            return item.isEmpty() || item.get().test(stack);
        }
    }
}
