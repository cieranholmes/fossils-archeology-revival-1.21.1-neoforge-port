package com.github.fossil.material;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, FossilsAndArcheologyRevival.MODID);

    /**
     * Client-side rendering (still/flowing/overlay textures) is registered separately in
     * {@code ClientModEvents} via {@code RegisterClientExtensionsEvent} - the source's Architectury
     * {@code SimpleArchitecturyFluidAttributes} equivalent (sourceTexture/flowingTexture/overlayTexture)
     * was dropped during the fluids port with no NeoForge replacement written, which silently produced a
     * null texture lookup that only NPEs the first time a client actually renders a tar fluid block.
     */
    public static final DeferredHolder<FluidType, FluidType> TAR_TYPE = FLUID_TYPES.register("tar",
            () -> new FluidType(FluidType.Properties.create().temperature(400).density(3000).viscosity(8000)));

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
