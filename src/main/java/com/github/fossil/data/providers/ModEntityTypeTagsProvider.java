package com.github.fossil.data.providers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.entity.prehistoric.base.PrehistoricMobType;
import com.github.fossil.entity.prehistoric.base.VanillaEntityInfo;
import com.github.fossil.tags.ModEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.entity.EntityType.*;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookup, FossilsAndArcheologyRevival.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addTag(ModEntityTypeTags.LIVESTOCK, AXOLOTL, CHICKEN, COW, DONKEY, GOAT, HORSE, LLAMA, MULE, PANDA, PIG, RABBIT, SHEEP);
        var mammal = tag(ModEntityTypeTags.MAMMAL);
        for (VanillaEntityInfo info : VanillaEntityInfo.values()) {
            if (info.mobType == PrehistoricMobType.MAMMAL) {
                mammal.add(info.entityType());
            }
        }
        addTag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES, ModEntities.MAMMOTH.get(), ModEntities.ELASMOTHERIUM.get());
    }

    protected IntrinsicTagAppender<EntityType<?>> addTag(TagKey<EntityType<?>> key, EntityType<?>... toAdd) {
        return tag(key).add(Arrays.stream(toAdd).toArray(EntityType<?>[]::new));
    }
}
