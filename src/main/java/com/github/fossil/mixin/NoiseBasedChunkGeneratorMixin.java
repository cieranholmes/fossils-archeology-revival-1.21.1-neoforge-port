package com.github.fossil.mixin;

import com.github.fossil.world.surfacerules.TerraBlenderSurfaceRuleFix;
import com.github.fossil.world.surfacerules.VolcanoSurfaceRuleInjector;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps modded surface rules working for the overworld in heavily modded setups. Two layers, because
 * the first one alone was verified insufficient in the wild:
 * <ol>
 *   <li>re-apply TerraBlender's missing dimension tag just before surface building, which restores
 *       the dispatch for <em>every</em> mod when nothing else has taken over the getter; and</li>
 *   <li>failing that, splice this mod's own rule into the chain at the point it is consumed.</li>
 * </ol>
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    /** The outer overload is the one that still has the WorldGenRegion, and therefore the dimension. */
    @Inject(
            method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
            at = @At("HEAD")
    )
    private void fossil$ensureTerraBlenderDispatch(WorldGenRegion region, StructureManager structureManager,
                                                   RandomState randomState, ChunkAccess chunk, CallbackInfo ci) {
        TerraBlenderSurfaceRuleFix.ensureDispatch((NoiseBasedChunkGenerator) (Object) this, region.getLevel());
    }

    @ModifyExpressionValue(
            method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;surfaceRule()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    )
    private SurfaceRules.RuleSource fossil$injectVolcanoSurfaceRule(SurfaceRules.RuleSource original) {
        return VolcanoSurfaceRuleInjector.ensureVolcanoRule(original);
    }
}
