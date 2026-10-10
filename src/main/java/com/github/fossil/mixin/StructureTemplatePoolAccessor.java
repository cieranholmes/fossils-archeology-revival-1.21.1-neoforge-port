package com.github.fossil.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Opens up {@link StructureTemplatePool}'s two element lists so
 * {@link com.github.fossil.world.feature.structures.VillageHouseInjector} can append to a pool that vanilla
 * (or another mod) already built, instead of this mod shipping its own copy of the pool's JSON.
 * <p>
 * {@code templates} is the flattened, weight-expanded list that {@code getShuffledTemplates}/{@code size}/
 * {@code getMaxSize} actually read, and it is a mutable {@link ObjectArrayList} we can add straight to.
 * {@code rawTemplates} is the {@code (element, weight)} form the codec round-trips through; it is
 * {@code final} and, when the pool came from JSON, an immutable list, so it has to be swapped for a copy -
 * hence {@link Mutable} on the setter. Both are kept in step so a pool we touched still serialises correctly.
 */
@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolAccessor {

    @Accessor("rawTemplates")
    List<Pair<StructurePoolElement, Integer>> getRawTemplates();

    @Mutable
    @Accessor("rawTemplates")
    void setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates);

    @Accessor("templates")
    ObjectArrayList<StructurePoolElement> getTemplates();

    /**
     * {@code maxSize} is a lazily computed cache over {@code templates}, so anything that grows that list has
     * to put it back to its "not computed yet" sentinel ({@code Integer.MIN_VALUE}).
     */
    @Accessor("maxSize")
    void setMaxSize(int maxSize);
}
