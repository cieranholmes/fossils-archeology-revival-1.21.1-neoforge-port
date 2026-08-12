package com.github.fossils.material;

import com.github.fossils.FossilsAndArcheologyRevival;
import com.github.fossils.block.ModBlocks;
import com.github.fossils.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, FossilsAndArcheologyRevival.MODID);
    public static final TagKey<Fluid> TAR_FLUID = TagKey.create(Registries.FLUID, FossilsAndArcheologyRevival.location("tar"));

    public static final DeferredHolder<Fluid, FlowingFluid> TAR = FLUIDS.register("tar",
            () -> new TarFluid.Source(tarProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> TAR_FLOWING = FLUIDS.register("tar_flowing",
            () -> new TarFluid.Flowing(tarProperties()));

    private static BaseFlowingFluid.Properties tarProperties() {
        return new BaseFlowingFluid.Properties(ModFluidTypes.TAR_TYPE, TAR, TAR_FLOWING)
                .bucket(ModItems.TAR_BUCKET)
                .block(ModBlocks.TAR)
                .tickRate(40);
    }

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
