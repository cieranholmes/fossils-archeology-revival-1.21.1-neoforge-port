package com.github.fossil.item;

import com.github.fossil.FossilsAndArcheologyRevival;
import com.github.fossil.entity.ModEntities;
import com.github.fossil.entity.ToyBase;
import com.github.fossil.entity.prehistoric.base.PrehistoricEntityInfo;
import com.github.fossil.entity.prehistoric.base.VanillaEntityInfo;
import com.github.fossil.material.ModArmorMaterials;
import com.github.fossil.material.ModFluids;
import com.github.fossil.material.ModToolTiers;
import com.github.fossil.util.TimePeriod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FossilsAndArcheologyRevival.MODID);

    public static final DeferredItem<Item> LASER_POINTER = register("laser_pointer",
            () -> new LaserPointerItem(new Item.Properties().stacksTo(1)),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> TAR_BUCKET = register("tar_bucket",
            () -> new BucketItem(ModFluids.TAR.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> TAR_DROP = register("tar_drop",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    //RecordItem was removed entirely in 1.21.1 - music discs are now plain Items carrying a jukeboxPlayable
    //data component that points at a JukeboxSong registry entry. JukeboxSong is a datapack-JSON-driven registry
    //(like Enchantment, see ArcheologyEnchantment/PaleontologyEnchantment elsewhere in this port) - the actual
    //song data lives in data/fossil/jukebox_song/{bones,anu,scarab,discovery}.json, reusing this mod's existing
    //ModSounds sound events and the existing item.fossil.music_disc_*.desc lang keys for the tooltip text.
    public static final DeferredItem<Item> MUSIC_DISC_BONES = register("music_disc_bones",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("bones"))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> MUSIC_DISC_ANU = register("music_disc_anu",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("anu"))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> MUSIC_DISC_SCARAB = register("music_disc_scarab",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("scarab"))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> MUSIC_DISC_DISCOVERY = register("music_disc_discovery",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(jukeboxSong("discovery"))),
            ModTabs::addToOtherItemTab);

    //Source registers these with no creative tab at all - faithfully replicated, not an oversight.
    public static final DeferredItem<Item> BIO_FOSSIL = register("fossil_bio", () -> new FossilItem(TimePeriod.MESOZOIC));
    public static final DeferredItem<Item> PLANT_FOSSIL = register("fossil_plant", () -> new FossilItem(null));
    public static final DeferredItem<Item> SHALE_FOSSIL = register("fossil_shale", () -> new FossilItem(TimePeriod.PALEOZOIC));
    public static final DeferredItem<Item> TAR_FOSSIL = register("fossil_tar", () -> new FossilItem(TimePeriod.CENOZOIC));

    public static final DeferredItem<Item> RELIC_SCRAP = register("relic_scrap",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> STONE_TABLET = register("stone_tablet",
            () -> new StoneTabletItem(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SKULL_STICK = register("skull_stick",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BROKEN_SWORD = register("broken_sword",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BROKEN_HELMET = register("broken_helmet",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    //Source registers the sword/javelins with no creative tab at all - faithfully replicated, not an oversight.
    public static final DeferredItem<Item> ANCIENT_SWORD = register("ancient_sword",
            () -> new AncientSwordItem(Tiers.IRON, 3, -2.4f));
    public static final DeferredItem<Item> ANCIENT_HELMET = register("ancient_helmet",
            () -> new AncientHelmetItem(ModArmorMaterials.ANCIENT, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ModArmorMaterials.ANCIENT_DURABILITY_MULTIPLIER))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> FROZEN_MEAT = register("frozen_meat",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> TOOTH_DAGGER = register("tooth_dagger",
            () -> new SwordItem(ModToolTiers.TOOTH_DAGGER, new Item.Properties().attributes(SwordItem.createAttributes(ModToolTiers.TOOTH_DAGGER, 3, -2.4f))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<JavelinItem> WOODEN_JAVELIN = ITEMS.register("wooden_javelin", () -> new JavelinItem(Tiers.WOOD));
    public static final DeferredItem<JavelinItem> STONE_JAVELIN = ITEMS.register("stone_javelin", () -> new JavelinItem(Tiers.STONE));
    public static final DeferredItem<JavelinItem> GOLD_JAVELIN = ITEMS.register("gold_javelin", () -> new JavelinItem(Tiers.GOLD));
    public static final DeferredItem<JavelinItem> IRON_JAVELIN = ITEMS.register("iron_javelin", () -> new JavelinItem(Tiers.IRON));
    public static final DeferredItem<JavelinItem> DIAMOND_JAVELIN = ITEMS.register("diamond_javelin", () -> new JavelinItem(Tiers.DIAMOND));
    public static final DeferredItem<JavelinItem> ANCIENT_JAVELIN = ITEMS.register("ancient_javelin", () -> new JavelinItem(Tiers.WOOD, true));
    public static final DeferredItem<Item> SCARAB_SWORD = register("scarab_sword",
            () -> new SwordItem(ModToolTiers.SCARAB, new Item.Properties().attributes(SwordItem.createAttributes(ModToolTiers.SCARAB, 3, -2.4f))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SCARAB_PICKAXE = register("scarab_pickaxe",
            () -> new PickaxeItem(ModToolTiers.SCARAB, new Item.Properties().attributes(PickaxeItem.createAttributes(ModToolTiers.SCARAB, 1f, -2.8f))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SCARAB_AXE = register("scarab_axe",
            () -> new AxeItem(ModToolTiers.SCARAB, new Item.Properties().attributes(AxeItem.createAttributes(ModToolTiers.SCARAB, 4f, -3f))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SCARAB_SHOVEL = register("scarab_shovel",
            () -> new ShovelItem(ModToolTiers.SCARAB, new Item.Properties().attributes(ShovelItem.createAttributes(ModToolTiers.SCARAB, 1.5f, -3f))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SCARAB_HOE = register("scarab_hoe",
            () -> new HoeItem(ModToolTiers.SCARAB, new Item.Properties().attributes(HoeItem.createAttributes(ModToolTiers.SCARAB, -2f, -1f))),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> BONE_HELMET = register("bone_helmet",
            () -> new ArmorItem(ModArmorMaterials.BONE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(ModArmorMaterials.BONE_DURABILITY_MULTIPLIER))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BONE_CHESTPLATE = register("bone_chestplate",
            () -> new ArmorItem(ModArmorMaterials.BONE, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(ModArmorMaterials.BONE_DURABILITY_MULTIPLIER))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BONE_LEGGINGS = register("bone_leggings",
            () -> new ArmorItem(ModArmorMaterials.BONE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(ModArmorMaterials.BONE_DURABILITY_MULTIPLIER))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BONE_BOOTS = register("bone_boots",
            () -> new ArmorItem(ModArmorMaterials.BONE, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(ModArmorMaterials.BONE_DURABILITY_MULTIPLIER))),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> WHIP = register("whip",
            WhipItem::new,
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> CHICKEN_ESSENCE = register("essence_chicken",
            () -> new DescriptiveItem(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> STUNTED_ESSENCE = register("essence_stunted",
            () -> new DescriptiveItem(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> POTTERY_SHARD = register("pottery_shard",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> BIO_GOO = register("bio_goo",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> SCARAB_GEM = register("scarab_gem",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> AQUATIC_SCARAB_GEM = register("scarab_gem_aquatic",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> ANCIENT_KEY = register("ancient_key",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> ANCIENT_CLOCK = register("ancient_clock",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);
    public static final DeferredItem<Item> DINOPEDIA = register("dinopedia",
            () -> new Item(new Item.Properties()),
            ModTabs::addToOtherItemTab);

    public static final DeferredItem<Item> ELASMOTHERIUM_FUR = register("fur_elasmotherium",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> MAMMOTH_FUR = register("fur_mammoth",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    //Source registers this one with no creative tab at all - faithfully replicated, not an oversight.
    public static final DeferredItem<Item> THERIZINOSAURUS_DOWN = register("fur_therizinosaurus",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FAILURESAURUS_FLESH = register("failuresaurus_flesh",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> MAGIC_CONCH = register("magic_conch",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> RAW_CHICKEN_SOUP = register("chicken_soup_raw",
            () -> new FoodBucketItem(new Item.Properties().craftRemainder(Items.BUCKET)
                    .food(new FoodProperties.Builder().nutrition(4).saturationModifier(2f).build())),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> COOKED_CHICKEN_SOUP = register("chicken_soup_cooked",
            () -> new FoodBucketItem(new Item.Properties().craftRemainder(Items.BUCKET)
                    .food(new FoodProperties.Builder().nutrition(8).saturationModifier(2f).build())),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> COOKED_EGG = register("cooked_egg",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(2f).build())),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> ARTIFICIAL_HONEYCOMB = register("artificial_honeycomb",
            () -> new ArtificialHoneycombItem(new Item.Properties()),
            ModTabs::addToMobItemTab);

    public static final DeferredItem<Item> FERN_SEED_FOSSIL = register("fossil_seed_fern",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    //FERN_SEED needs ModBlocks.FERNS (FernsBlock) which isn't ported yet - genuinely Phase 1/Blocks catch-up
    //scope, not overlooked. Not registered until that block exists.
    public static final DeferredItem<Item> CALAMITES_FOSSIL_SAPLING = register("fossil_sapling_calamites",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> CORDAITES_FOSSIL_SAPLING = register("fossil_sapling_cordaites",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> PALM_FOSSIL_SAPLING = register("fossil_sapling_palm",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> SIGILLARIA_FOSSIL_SAPLING = register("fossil_sapling_sigillaria",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);
    public static final DeferredItem<Item> TEMPSKYA_FOSSIL_SAPLING = register("fossil_sapling_tempskya",
            () -> new Item(new Item.Properties()),
            ModTabs::addToMobItemTab);

    public static final DeferredItem<Item> TAR_SLIME_SPAWN_EGG = registerSpawnEgg("spawn_egg_tar_slime", ModEntities.TAR_SLIME, 0X222222, 0x0B0B0B);
    public static final DeferredItem<Item> ANU_BOSS_SPAWN_EGG = registerSpawnEgg("spawn_egg_anu_boss", ModEntities.ANU_BOSS, 0X0F0F0F, 0XF72D00);
    public static final DeferredItem<Item> SENTRY_PIGLIN_SPAWN_EGG = registerSpawnEgg("spawn_egg_sentry_piglin", ModEntities.SENTRY_PIGLIN, 15373203, 0XD0A750);
    public static final DeferredItem<Item> FAILURESAURUS_SPAWN_EGG = registerSpawnEgg("spawn_egg_failuresaurus", ModEntities.FAILURESAURUS, 0X46DA9b, 0X257B47);

    public static final Map<DyeColor, DeferredItem<ToyBallItem>> TOY_BALLS = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(Function.identity(), ModItems::registerBall));
    public static final Map<String, DeferredItem<ToyTetheredLogItem>> TOY_TETHERED_LOGS = WoodType.values().filter(ToyBase::isVanillaWood)
            .collect(Collectors.toMap(WoodType::name, ModItems::registerTetheredLog));
    public static final Map<String, DeferredItem<ToyScratchingPostItem>> TOY_SCRATCHING_POSTS = WoodType.values().filter(ToyBase::isVanillaWood)
            .collect(Collectors.toMap(WoodType::name, ModItems::registerScratchingPost));

    private static ResourceKey<JukeboxSong> jukeboxSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, FossilsAndArcheologyRevival.location(name));
    }

    private static DeferredItem<Item> registerSpawnEgg(String id, DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> type, int color1, int color2) {
        return register(id, () -> new DeferredSpawnEggItem(type, color1, color2, new Item.Properties()), ModTabs::addToMobItemTab);
    }

    private static DeferredItem<ToyBallItem> registerBall(DyeColor color) {
        DeferredItem<ToyBallItem> item = ITEMS.register("toy_ball_" + color.getName(), () -> new ToyBallItem(color, new Item.Properties()));
        ModTabs.addToBlockTab(item);
        return item;
    }

    private static DeferredItem<ToyTetheredLogItem> registerTetheredLog(WoodType woodType) {
        DeferredItem<ToyTetheredLogItem> item = ITEMS.register("toy_tethered_log_" + woodType.name(), () -> new ToyTetheredLogItem(woodType, new Item.Properties()));
        ModTabs.addToBlockTab(item);
        return item;
    }

    private static DeferredItem<ToyScratchingPostItem> registerScratchingPost(WoodType woodType) {
        DeferredItem<ToyScratchingPostItem> item = ITEMS.register("toy_scratching_post_" + woodType.name(), () -> new ToyScratchingPostItem(woodType, new Item.Properties()));
        ModTabs.addToBlockTab(item);
        return item;
    }

    private static DeferredItem<Item> register(String name, Supplier<Item> item, Consumer<DeferredItem<Item>> tab) {
        DeferredItem<Item> registered = ITEMS.register(name, item);
        tab.accept(registered);
        return registered;
    }

    private static DeferredItem<Item> register(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }

    public static void register(IEventBus modEventBus) {
        VanillaEntityInfo.register();
        PrehistoricEntityInfo.register();

        ITEMS.register(modEventBus);
    }
}
