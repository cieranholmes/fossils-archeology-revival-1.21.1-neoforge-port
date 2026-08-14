package com.github.fossil.data.providers;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.advancements.ImplantEmbryoTrigger;
import com.github.fossil.advancements.IncubateEggTrigger;
import com.github.fossil.advancements.OpenSarcophagusTrigger;
import com.github.fossil.advancements.ScarabTameTrigger;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.VanillaEntityInfo;
import com.github.fossil.tags.ModItemTags;
import com.github.fossil.world.dimension.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.function.Consumer;

import static com.github.fossil.block.ModBlocks.*;
import static com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo.*;
import static com.github.fossil.item.ModItems.*;

public class FossilAdvancements implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper efh) {
        AdvancementHolder root = Advancement.Builder.advancement().display(BIO_FOSSIL.get(),
                        Component.translatable("advancements.fossil.root.title"),
                        Component.translatable("advancements.fossil.root.description"),
                        FossilsAndArcheologyRevival.location("textures/block/ancient_stone_bricks.png"), AdvancementType.TASK, true, false, false)
                .addCriterion("requirement", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE))
                .save(saver, id("root"), efh);
        AdvancementHolder breakFossil = simple(root, "break_fossil", saver, efh, RELIC_SCRAP.get(), BIO_FOSSIL.get(), PLANT_FOSSIL.get(), SHALE_FOSSIL.get(), SKULL_BLOCK.get());
        AdvancementHolder analyzer = simple(breakFossil, saver, efh, ANALYZER.get());
        AdvancementHolder fossilSeed = tag(FERN_SEED_FOSSIL.get(), ModItemTags.FOSSIL_SEEDS, analyzer, saver, efh);
        //RESTORED_SEEDS ("restored_seed" advancement chained off FERN_SEED) dropped - FERN_SEED needs ModBlocks.FERNS
        //(FernsBlock), not ported yet. See fossils_phase7_task4_tags_providers/fossils_phase7_task6_recipe_provider memories.

        AdvancementHolder figurine = tag(STEVE_FIGURINE_DESTROYED.get(), ModItemTags.FIGURINES, breakFossil, saver, efh);
        AdvancementHolder anuLair = other(figurine, ANU_STATUE.get(), "anu_lair", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.ANU_LAIR), saver, efh);
        AdvancementHolder anubite = other(anuLair, ANUBITE_STATUE.get(), "anubite",
                KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.ANUBITE.get())), saver, efh);
        AdvancementHolder sarcophagus = other(anuLair, SARCOPHAGUS.get(), "sarcophagus",
                OpenSarcophagusTrigger.TriggerInstance.openSarcophagus(), saver, efh);
        AdvancementHolder killAnu = other(sarcophagus, ANCIENT_KEY.get(), "kill_anu",
                KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.ANU_BOSS.get())), saver, efh);
        simple(killAnu, saver, efh, ANCIENT_CLOCK.get());

        simple(breakFossil, saver, efh, FROZEN_MEAT.get());
        AdvancementHolder scarabGem = simple(breakFossil, saver, efh, SCARAB_GEM.get());
        other(scarabGem, AQUATIC_SCARAB_GEM.get(), "scarab_tame", ScarabTameTrigger.TriggerInstance.tameWithScarab(), saver, efh);

        simple(analyzer, saver, efh, STONE_TABLET.get());
        AdvancementHolder tarDrop = simple(breakFossil, saver, efh, TAR_DROP.get());
        simple(tarDrop, saver, efh, TAR_FOSSIL.get());

        AdvancementHolder brokenSword = simple(root, saver, efh, BROKEN_SWORD.get(), BROKEN_HELMET.get());
        AdvancementHolder worktable = simple(brokenSword, saver, efh, WORKTABLE.get());
        simple(worktable, saver, efh, ANCIENT_SWORD.get(), ANCIENT_HELMET.get());

        AdvancementHolder dna = tag(TRICERATOPS.dnaItem, ModItemTags.DNA, analyzer, saver, efh);
        AdvancementHolder cultureVat = simple(dna, saver, efh, CULTURE_VAT.get());
        other(cultureVat, FAILURESAURUS_FLESH.get(), "failuresaurus",
                PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(Optional.of(EntityPredicate.Builder.entity().of(ModEntities.FAILURESAURUS.get()).build())), saver, efh);
        AdvancementHolder embryo = tag(MAMMOTH.embryoItem, ModItemTags.EMBRYOS, cultureVat, saver, efh);
        AdvancementHolder dinoEgg = tag(TRICERATOPS.eggItem, ModItemTags.ALL_EGGS, cultureVat, saver, efh, "dino_eggs");
        simple(dinoEgg, saver, efh, DINOPEDIA.get());

        Advancement.Builder builder = Advancement.Builder.advancement().display(TYRANNOSAURUS.eggItem, title("all_eggs"),
                        description("all_eggs"), null, AdvancementType.CHALLENGE, true, true, false)
                .parent(dinoEgg).rewards(AdvancementRewards.Builder.experience(1000));
        for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
            if (info.eggItem != null || info.cultivatedBirdEggItem != null) {
                builder.addCriterion(key(info.entityType()).getPath(), IncubateEggTrigger.TriggerInstance.incubateEgg(info.entityType()));
            }
        }
        builder.save(saver, id("all_eggs"), efh);

        builder = Advancement.Builder.advancement().display(MAMMOTH.embryoItem, title("all_embryos"), description("all_embryos"),
                        null, AdvancementType.CHALLENGE, true, true, false)
                .parent(embryo).rewards(AdvancementRewards.Builder.experience(500));
        for (PrehistoricEntityInfo info : PrehistoricEntityInfo.values()) {
            if (info.embryoItem != null) {
                builder.addCriterion(key(info.embryoItem).getPath(), ImplantEmbryoTrigger.TriggerInstance.implantEmbryo(info.embryoItem));
            }
        }
        for (VanillaEntityInfo info : VanillaEntityInfo.values()) {
            if (info.embryoItem != null) {
                builder.addCriterion(key(info.embryoItem).getPath(), ImplantEmbryoTrigger.TriggerInstance.implantEmbryo(info.embryoItem));
            }
        }
        builder.save(saver, id("all_embryos"), efh);
    }

    private AdvancementHolder other(AdvancementHolder parent, ItemLike item, String key, Criterion<?> trigger, Consumer<AdvancementHolder> saver, ExistingFileHelper efh) {
        return Advancement.Builder.advancement().display(item, title(key), description(key),
                        null, AdvancementType.TASK, true, true, false)
                .parent(parent)
                .addCriterion("requirement", trigger)
                .save(saver, id(key), efh);
    }

    private AdvancementHolder simple(AdvancementHolder parent, String key, Consumer<AdvancementHolder> saver, ExistingFileHelper efh, ItemLike... items) {
        ItemLike item = items[0];
        Advancement.Builder builder = Advancement.Builder.advancement().display(item, title(key), description(key),
                        null, AdvancementType.TASK, true, true, false)
                .parent(parent).requirements(AdvancementRequirements.Strategy.OR);
        for (ItemLike itemLike : items) {
            builder.addCriterion(key(itemLike.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(itemLike));
        }
        return builder.save(saver, id(key), efh);
    }

    private AdvancementHolder simple(AdvancementHolder parent, Consumer<AdvancementHolder> saver, ExistingFileHelper efh, ItemLike... items) {
        return simple(parent, key(items[0].asItem()).getPath(), saver, efh, items);
    }

    private AdvancementHolder tag(ItemLike item, TagKey<Item> tag, AdvancementHolder parent, Consumer<AdvancementHolder> saver, ExistingFileHelper efh, String key) {
        return Advancement.Builder.advancement().display(item, title(key), description(key),
                        null, AdvancementType.TASK, true, true, false)
                .parent(parent)
                .addCriterion("requirement", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag)))
                .save(saver, id(key), efh);
    }

    private AdvancementHolder tag(ItemLike item, TagKey<Item> tag, AdvancementHolder parent, Consumer<AdvancementHolder> saver, ExistingFileHelper efh) {
        return tag(item, tag, parent, saver, efh, tag.location().getPath());
    }

    private Component title(String key) {
        return Component.translatable(String.format("advancements.%s.%s.title", FossilsAndArcheologyRevival.MODID, key));
    }

    private Component description(String key) {
        return Component.translatable(String.format("advancements.%s.%s.description", FossilsAndArcheologyRevival.MODID, key));
    }

    private ResourceLocation id(String key) {
        return FossilsAndArcheologyRevival.location("fossil/" + key);
    }

    private ResourceLocation key(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    private ResourceLocation key(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
    }
}
