package com.github.fossil.mixin;

import com.github.fossil.world.chunk.AnuLairChunkGenerator;
import com.github.fossil.world.chunk.TreasureChunkGenerator;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusMixin {

    @ModifyExpressionValue(method = "generateStructureStarts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldOptions;generateStructures()Z"))
    private static boolean alwaysGenerateStructuresForOurDimensions(boolean original, WorldGenContext worldGenContext) {
        if (worldGenContext.generator() instanceof TreasureChunkGenerator || worldGenContext.generator() instanceof AnuLairChunkGenerator) {
            return true;
        }
        return original;
    }
}
