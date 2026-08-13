package com.github.fossil.advancements;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Registries.TRIGGER_TYPE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, OpenSarcophagusTrigger> OPEN_SARCOPHAGUS_TRIGGER =
            TRIGGER_TYPES.register("open_sarcophagus", OpenSarcophagusTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ScarabTameTrigger> SCARAB_TAME_TRIGGER =
            TRIGGER_TYPES.register("scarab_tame", ScarabTameTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, ImplantEmbryoTrigger> IMPLANT_EMBRYO_TRIGGER =
            TRIGGER_TYPES.register("implant_embryo", ImplantEmbryoTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, IncubateEggTrigger> INCUBATE_EGG_TRIGGER =
            TRIGGER_TYPES.register("incubate_egg", IncubateEggTrigger::new);

    public static void register(IEventBus modEventBus) {
        TRIGGER_TYPES.register(modEventBus);
    }
}
