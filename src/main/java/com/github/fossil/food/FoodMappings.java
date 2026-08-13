package com.github.fossil.food;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Simplified in-memory replacement for the source mod's JSON+tag-driven food value system.
 * Preserves the public API used by the AI goals; drops JSON loading, tag support and network sync.
 */
public abstract class FoodMappings {
    private static final Map<FoodType, Map<Item, Integer>> ITEM_VALUES = new HashMap<>();
    private static final Map<FoodType, Map<Block, Integer>> BLOCK_VALUES = new HashMap<>();
    private static final Map<FoodType, Map<EntityType<?>, Integer>> ENTITY_VALUES = new HashMap<>();

    static {
        addMeat(Items.BEEF);
        addMeat(Items.COOKED_BEEF);
        addMeat(Items.PORKCHOP);
        addMeat(Items.COOKED_PORKCHOP);
        addMeat(Items.CHICKEN);
        addMeat(Items.COOKED_CHICKEN);
        addMeat(Items.MUTTON);
        addMeat(Items.COOKED_MUTTON);
        addMeat(Items.RABBIT);
        addMeat(Items.COOKED_RABBIT);
        addMeat(Items.ROTTEN_FLESH, 10);

        addFish(Items.COD);
        addFish(Items.COOKED_COD);
        addFish(Items.SALMON);
        addFish(Items.COOKED_SALMON);
        addFish(Items.TROPICAL_FISH);

        addEgg(Items.EGG);

        addPlant(Items.WHEAT);
        addPlant(Items.CARROT);
        addPlant(Items.POTATO);
        addPlant(Items.APPLE);
        addPlant(Items.SWEET_BERRIES);
        addPlant(Blocks.SHORT_GRASS.asItem(), 5);
        addPlant(Blocks.FERN.asItem(), 5);

        addMeat(EntityType.COW, 40);
        addMeat(EntityType.PIG, 20);
        addMeat(EntityType.SHEEP, 35);
        addMeat(EntityType.CHICKEN, 7);
        addMeat(EntityType.RABBIT, 5);
        addMeat(EntityType.HORSE, 55);
        addMeat(EntityType.VILLAGER, 27);
        addMeat(EntityType.PLAYER, 27);
        addFish(EntityType.COD, 5);
        addFish(EntityType.SALMON, 5);
        addFish(EntityType.TROPICAL_FISH, 3);
    }

    private static Map<Item, Integer> itemValues(FoodType type) {
        return ITEM_VALUES.computeIfAbsent(type, t -> new HashMap<>());
    }

    private static Map<Block, Integer> blockValues(FoodType type) {
        return BLOCK_VALUES.computeIfAbsent(type, t -> new HashMap<>());
    }

    private static Map<EntityType<?>, Integer> entityValues(FoodType type) {
        return ENTITY_VALUES.computeIfAbsent(type, t -> new HashMap<>());
    }

    /**
     * Returns every item/block the given diet can eat, for the Dinopedia screen's food display.
     * Ported replacement for source's {@code FoodMappingsManager#getItemCache()} - this simplified
     * in-memory system keeps no per-diet cache, so the set is recomputed from {@code ITEM_VALUES}/
     * {@code BLOCK_VALUES} on each call, sorted by registry id like the original {@code TreeSet}.
     */
    public static Collection<ItemLike> getFoodItems(Diet diet) {
        Comparator<ItemLike> byId = Comparator.comparingInt(item -> item instanceof Block block
                ? BuiltInRegistries.BLOCK.getId(block) : Item.getId(item.asItem()));
        TreeSet<ItemLike> items = new TreeSet<>(byId);
        for (FoodType type : diet.flags()) {
            items.addAll(itemValues(type).keySet());
            items.addAll(blockValues(type).keySet());
        }
        return items;
    }

    public static int getFoodAmount(Item item, Diet diet) {
        Block block = Block.byItem(item);
        for (FoodType type : diet.flags()) {
            if (block != Blocks.AIR && blockValues(type).containsKey(block)) {
                return blockValues(type).get(block);
            }
            if (itemValues(type).containsKey(item)) {
                return itemValues(type).get(item);
            }
        }
        return 0;
    }

    public static int getFoodAmount(Block block, Diet diet) {
        for (FoodType type : diet.flags()) {
            if (blockValues(type).containsKey(block)) {
                return blockValues(type).get(block);
            }
        }
        return 0;
    }

    public static int getMobFoodPoints(LivingEntity entity, Diet diet) {
        if (entity == null) {
            return 0;
        }
        EntityType<?> entityType = entity.getType();
        for (FoodType type : diet.flags()) {
            if (entityValues(type).containsKey(entityType)) {
                int mappingPoints = entityValues(type).get(entityType);
                float scale = entity.getBbWidth() / entityType.getDimensions().width() * entity.getBbHeight() / entityType.getDimensions().height();
                return Math.round(mappingPoints * scale);
            }
        }
        if (diet.canEat(FoodType.MEAT) && entity instanceof Animal && !isAquaticMob(entity)) {
            return Math.round(entity.getBbWidth() * entity.getBbHeight() * 10);
        }
        if (diet.canEat(FoodType.FISH) && isAquaticMob(entity)) {
            return Math.round(entity.getBbWidth() * entity.getBbHeight() * 10);
        }
        return 0;
    }

    private static boolean isAquaticMob(LivingEntity entity) {
        return entity.canBreatheUnderwater() || entity instanceof WaterAnimal || entity instanceof Mob mob && mob.getNavigation() instanceof WaterBoundPathNavigation;
    }

    public static void addMeat(EntityType<?> entity, int food) {
        entityValues(FoodType.MEAT).put(entity, food);
    }

    public static void addMeat(ItemLike itemLike) {
        addMeat(itemLike, defaultNutrition(itemLike.asItem(), 7, 20));
    }

    public static void addMeat(ItemLike itemLike, int food) {
        itemValues(FoodType.MEAT).put(itemLike.asItem(), food);
    }

    public static void addFish(EntityType<?> entity, int food) {
        entityValues(FoodType.FISH).put(entity, food);
    }

    public static void addFish(Item item) {
        addFish(item, defaultNutrition(item, 7, 10));
    }

    public static void addFish(Item item, int food) {
        itemValues(FoodType.FISH).put(item, food);
    }

    public static void addEgg(Item item) {
        addEgg(item, defaultNutrition(item, 7, 15));
    }

    public static void addEgg(Item item, int food) {
        itemValues(FoodType.EGG).put(item, food);
    }

    public static void addPlant(Block block, int food) {
        itemValues(FoodType.PLANT).put(block.asItem(), food);
    }

    public static void addPlant(Item item) {
        addPlant(item, defaultNutrition(item, 5, 17));
    }

    private static int defaultNutrition(Item item, int multiplier, int fallback) {
        var food = item.getDefaultInstance().getFoodProperties(null);
        return food != null ? food.nutrition() * multiplier : fallback;
    }

    public static void addPlant(Item item, int food) {
        itemValues(FoodType.PLANT).put(item, food);
    }
}
