package com.github.fossil.client.particle;

import com.github.fossil.FossilsAndArcheologyRevival;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registration only - the client-side render factories (RegisterParticleProvidersEvent) are Phase 5 scope, same as
 * the rest of this mod's client rendering. Referencing one of these types from server code (e.g. LaserPointerItem)
 * compiles and registers fine; it just won't visibly render until Phase 5 wires up a provider.
 */
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, FossilsAndArcheologyRevival.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUBBLE = register("bubble");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TAR_BUBBLE = register("tar_bubble");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLCANO_VENT_ASH = register("volcano_vent_ash");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLCANO_VENT_ASH_EMITTER = register("volcano_vent_ash_emitter");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LASER_PARTICLE = register("laser_particle");

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String key) {
        return PARTICLE_TYPES.register(key, () -> new SimpleParticleType(false));
    }

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }
}
