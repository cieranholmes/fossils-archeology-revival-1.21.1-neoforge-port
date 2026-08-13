package com.github.fossil.entity.prehistoric.base;

import com.github.fossil.food.Diet;
import com.github.fossil.item.BirdEggItem;
import com.github.fossil.item.DNAItem;
import com.github.fossil.item.FishEggItem;
import com.github.fossil.item.MammalEmbryoItem;
import com.github.fossil.item.ModItems;
import com.github.fossil.item.ModTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum VanillaEntityInfo implements EntityInfo {
    AXOLOTL(EntityType.AXOLOTL, PrehistoricMobType.FISH, Diet.INSECTIVORE),
    BAT(EntityType.BAT, PrehistoricMobType.MAMMAL, Diet.INSECTIVORE),
    BEE(EntityType.BAT, PrehistoricMobType.OTHER, Diet.HERBIVORE),
    CAT(EntityType.CAT, PrehistoricMobType.MAMMAL, Diet.CARNIVORE),
    CHICKEN(EntityType.CHICKEN, PrehistoricMobType.VANILLA_BIRD, Diet.HERBIVORE),
    CAMEL(EntityType.CAMEL, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    COD(EntityType.COD, PrehistoricMobType.FISH, Diet.PISCI_CARNIVORE),
    COW(EntityType.COW, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    DOLPHIN(EntityType.DOLPHIN, PrehistoricMobType.MAMMAL, Diet.PISCI_CARNIVORE),
    DONKEY(EntityType.DONKEY, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    FOX(EntityType.FOX, PrehistoricMobType.MAMMAL, Diet.OMNIVORE),
    FROG(EntityType.FROG, PrehistoricMobType.OTHER, Diet.HERBIVORE),
    GOAT(EntityType.GOAT, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    GLOW_SQUID(EntityType.GLOW_SQUID, PrehistoricMobType.FISH, Diet.PISCI_CARNIVORE),
    HOGLIN(EntityType.HOGLIN, PrehistoricMobType.MAMMAL, Diet.OMNIVORE),
    HORSE(EntityType.HORSE, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    LLAMA(EntityType.LLAMA, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    MOOSHROOM(EntityType.MOOSHROOM, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    OCELOT(EntityType.OCELOT, PrehistoricMobType.MAMMAL, Diet.CARNIVORE),
    PANDA(EntityType.PANDA, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    PARROT(EntityType.PARROT, PrehistoricMobType.VANILLA_BIRD, Diet.HERBIVORE),
    PIG(EntityType.PIG, PrehistoricMobType.MAMMAL, Diet.OMNIVORE),
    POLAR_BEAR(EntityType.POLAR_BEAR, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    PUFFERFISH(EntityType.PUFFERFISH, PrehistoricMobType.FISH, Diet.OMNIVORE),
    RABBIT(EntityType.RABBIT, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    SALMON(EntityType.SALMON, PrehistoricMobType.FISH, Diet.PISCI_CARNIVORE),
    SHEEP(EntityType.SHEEP, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    SNIFFER(EntityType.SNIFFER, PrehistoricMobType.OTHER, Diet.HERBIVORE),
    SQUID(EntityType.SQUID, PrehistoricMobType.FISH, Diet.PISCI_CARNIVORE),
    STRIDER(EntityType.STRIDER, PrehistoricMobType.MAMMAL, Diet.HERBIVORE),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, PrehistoricMobType.FISH, Diet.PISCI_CARNIVORE),
    TURTLE(EntityType.TURTLE, PrehistoricMobType.OTHER, Diet.HERBIVORE),
    WOLF(EntityType.WOLF, PrehistoricMobType.MAMMAL, Diet.CARNIVORE);
    private final EntityType<? extends Entity> entityType;
    public final PrehistoricMobType mobType;
    public final Diet diet;
    public final String resourceName;
    public final Supplier<Component> displayName;
    //Populated by Phase 2 (Items) item registration - null until then
    public Item dnaItem;
    public Item eggItem;
    public Item embryoItem;
    public Item cultivatedBirdEggItem;

    VanillaEntityInfo(EntityType<? extends Mob> entityType, PrehistoricMobType mobType, Diet diet) {
        this.entityType = entityType;
        this.mobType = mobType;
        this.diet = diet;
        this.resourceName = this.name().toLowerCase(Locale.ROOT);
        this.displayName = () -> Component.translatable("entity.minecraft." + resourceName);
    }

    /**
     * Populates dnaItem always, plus eggItem/embryoItem/cultivatedBirdEggItem depending on mobType - a subset of
     * PrehistoricEntityInfo.register()'s logic since vanilla mobs reuse their own vanilla item/spawn egg, no
     * bones/food/bucket registration needed here. Must run before ModItems.ITEMS.register(...) fires.
     */
    public static void register() {
        for (VanillaEntityInfo info : values()) {
            DeferredItem<Item> dnaHolder = ModItems.ITEMS.register(info.resourceName + "_dna", () -> {
                DNAItem item = new DNAItem(info);
                info.dnaItem = item;
                return item;
            });
            ModTabs.addToMobItemTab(dnaHolder);

            if (info.mobType == PrehistoricMobType.FISH) {
                registerItem("egg_item", info, () -> new FishEggItem(info), item -> info.eggItem = item);
            } else if (info.mobType == PrehistoricMobType.MAMMAL) {
                registerItem("syringe", info, () -> new MammalEmbryoItem(info), item -> info.embryoItem = item);
            } else if (info.mobType == PrehistoricMobType.VANILLA_BIRD) {
                registerItem("egg_item", info, () -> new BirdEggItem(info, true), item -> info.cultivatedBirdEggItem = item);
            }
        }
    }

    private static void registerItem(String name, VanillaEntityInfo info, Supplier<Item> itemFactory, Consumer<Item> listener) {
        DeferredItem<Item> holder = ModItems.ITEMS.register(name + "_" + info.resourceName, () -> {
            Item item = itemFactory.get();
            listener.accept(item);
            return item;
        });
        ModTabs.addToMobItemTab(holder);
    }

    @Override
    public EntityType<? extends Entity> entityType() {
        return entityType;
    }

    @Override
    public PrehistoricMobType mobType() {
        return mobType;
    }

    @Override
    public @Nullable Item getDNAResult() {
        if (eggItem != null) {
            return eggItem;
        } else if (embryoItem != null) {
            return embryoItem;
        } else if (cultivatedBirdEggItem != null) {
            return cultivatedBirdEggItem;
        }
        return null;
    }

    @Override
    public Supplier<Component> displayName() {
        return displayName;
    }

    @Override
    public Item dnaItem() {
        return dnaItem;
    }
}
