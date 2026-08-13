package com.github.fossils.advancements;

import com.github.fossils.item.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * Triggers whenever a Prehistoric is tamed with a {@link ModItems#SCARAB_GEM}
 */
public class ScarabTameTrigger extends SimpleCriterionTrigger<ScarabTameTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        trigger(player, instance -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player))
                        .apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> tameWithScarab() {
            return ModTriggers.SCARAB_TAME_TRIGGER.get().createCriterion(new TriggerInstance(Optional.empty()));
        }
    }
}
