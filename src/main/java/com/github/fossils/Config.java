package com.github.fossils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FossilsAndArcheologyRevival.MODID)
public class Config {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue GENERATE_HELL_SHIPS;
    private static final ModConfigSpec.BooleanValue GENERATE_MOAI;
    private static final ModConfigSpec.BooleanValue GENERATE_FOSSILS;
    private static final ModConfigSpec.BooleanValue GENERATE_PERMAFROST;
    private static final ModConfigSpec.BooleanValue GENERATE_VOLCANIC_ROCK;
    private static final ModConfigSpec.BooleanValue GENERATE_VOLCANO_BIOME;
    private static final ModConfigSpec.IntValue VOLCANO_BIOME_RARITY;

    private static final ModConfigSpec.BooleanValue FISH_ARE_PERSISTENT;
    private static final ModConfigSpec.BooleanValue SPAWN_TAR_SLIMES;
    private static final ModConfigSpec.IntValue TAR_SLIMES_SPAWN_RATE;

    private static final ModConfigSpec.BooleanValue FEATHERED_DILO;
    private static final ModConfigSpec.BooleanValue FEATHERED_CERATO;
    private static final ModConfigSpec.BooleanValue FEATHERED_DRYO;
    private static final ModConfigSpec.BooleanValue HEALING_DINOS;
    private static final ModConfigSpec.BooleanValue ENABLE_HUNGER;
    private static final ModConfigSpec.BooleanValue ENABLE_STARVATION;
    private static final ModConfigSpec.BooleanValue BREEDING_DINOS;
    private static final ModConfigSpec.BooleanValue EGGS_LIKE_CHICKENS;
    private static final ModConfigSpec.BooleanValue WHIP_TO_TAME_DINO;
    private static final ModConfigSpec.IntValue PREGNANCY_DURATION;
    private static final ModConfigSpec.BooleanValue DINOS_EAT_BLOCKS;
    private static final ModConfigSpec.BooleanValue DINOS_BREAK_BLOCKS;
    private static final ModConfigSpec.DoubleValue BLOCK_BREAK_HARDNESS;
    private static final ModConfigSpec.BooleanValue DINOS_EAT_MODDED_MOBS;
    private static final ModConfigSpec.BooleanValue ANIMALS_FEAR_DINOS;
    private static final ModConfigSpec.IntValue FLYING_TARGET_MAX_HEIGHT;
    private static final ModConfigSpec.BooleanValue ANU_BLOCK_PLACING;
    private static final ModConfigSpec.BooleanValue ANUBITE_HAS_COOLDOWN;
    private static final ModConfigSpec.IntValue ANUBITE_COOLDOWN;

    private static final ModConfigSpec.IntValue CULTURE_VAT_FAIL_CHANCE;
    private static final ModConfigSpec.BooleanValue MACHINES_REQUIRE_ENERGY;
    private static final ModConfigSpec.IntValue MACHINE_MAX_ENERGY;
    private static final ModConfigSpec.IntValue MACHINE_TRANSFER_RATE;
    private static final ModConfigSpec.IntValue MACHINE_ENERGY_USAGE;
    private static final ModConfigSpec.IntValue FERN_TICK_RATE;

    public static final ModConfigSpec COMMON_SPEC;

    static {
        COMMON_BUILDER.push("Generation Config");
        GENERATE_HELL_SHIPS = COMMON_BUILDER.comment("True if Hell Ships are to generate naturally").define("generateHellShips", true);
        GENERATE_FOSSILS = COMMON_BUILDER.comment("True if Fossil Ores are to generate naturally").define("generateFossils", true);
        GENERATE_PERMAFROST = COMMON_BUILDER.comment("True if Permafrost Ore is to generate naturally").define("generatePermafrost", true);
        GENERATE_VOLCANIC_ROCK = COMMON_BUILDER.comment("True if Volcanic Rock is to generate naturally").define("generateVolcanicRock", true);
        GENERATE_MOAI = COMMON_BUILDER.comment("True if Moai Statues are to generate naturally").define("generateMoai", true);
        GENERATE_VOLCANO_BIOME = COMMON_BUILDER.comment("True if Volcano Biomes are to generate naturally").define("generateVolcanoBiome", true);
        VOLCANO_BIOME_RARITY = COMMON_BUILDER.comment("Volcano Biome Weight. Higher number = more common").defineInRange("volcanoBiomeRarity", 7, 1, 10000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Spawn Config");
        SPAWN_TAR_SLIMES = COMMON_BUILDER.comment("True if Tar Slimes are to spawn naturally in tar pits").define("spawnTarSlimes", true);
        TAR_SLIMES_SPAWN_RATE = COMMON_BUILDER.comment("Tar Slime Spawn Rarity. Higher number = more rare").defineInRange("tarSlimesSpawnRate", 75, 1, 100000000);
        FISH_ARE_PERSISTENT = COMMON_BUILDER.comment("True if fish spawned via fish egg item should never despawn").define("fishArePersistent", true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Mob Config");
        FEATHERED_DILO = COMMON_BUILDER.comment("True if Dilophosaurus should be feathered").define("featheredDilo", false);
        FEATHERED_CERATO = COMMON_BUILDER.comment("True if Ceratosaurus should be feathered").define("featheredCerato", false);
        FEATHERED_DRYO = COMMON_BUILDER.comment("True if Dryosaurus should be feathered").define("featheredDryo", false);
        HEALING_DINOS = COMMON_BUILDER.comment("True if Dinosaurs can be healed by hand feeding it food").define("healingDinos", true);
        ENABLE_HUNGER = COMMON_BUILDER.comment("True if Dinosaurs have hunger").define("enableHunger", true);
        ENABLE_STARVATION = COMMON_BUILDER.comment("True if Dinosaurs can die of hunger").define("enableStarvation", false);
        BREEDING_DINOS = COMMON_BUILDER.comment("True if Dinosaurs should breed").define("breedingDinos", true);
        EGGS_LIKE_CHICKENS = COMMON_BUILDER.comment("True if Dinosaurs should create item eggs instead of entities").define("eggsLikeChickens", false);
        WHIP_TO_TAME_DINO = COMMON_BUILDER.comment("True if Whips can be used to tame some dinosaurs").define("whipToTameDino", true);
        FLYING_TARGET_MAX_HEIGHT = COMMON_BUILDER.comment("Maximum height that flying creatures should be able to fly to").defineInRange("flyingTargetMaxHeight", 128, 1, 512);
        PREGNANCY_DURATION = COMMON_BUILDER.comment("How long mammal pregnancies last, in ticks").defineInRange("pregnancyDuration", 10000, 1, 1000000000);
        DINOS_BREAK_BLOCKS = COMMON_BUILDER.comment("True if certain Dinosaurs can break blocks weaker than the set hardness").define("dinosBreakBlocks", true);
        BLOCK_BREAK_HARDNESS = COMMON_BUILDER.comment("Minimum hardness that a block needs in order not to break. The default is iron(5). A value of 0.4 would be enough to break leaves and glass but not dirt").defineInRange("blockBreakHardness", 5.0, 0.0, 100.0);
        DINOS_EAT_BLOCKS = COMMON_BUILDER.comment("True if herbivores can eat plant blocks").define("dinosEatBlocks", true);
        DINOS_EAT_MODDED_MOBS = COMMON_BUILDER.comment("True if Dinosaurs can eat non-vanilla mobs").define("dinosEatModdedMobs", true);
        ANIMALS_FEAR_DINOS = COMMON_BUILDER.comment("True if vanilla animals should run away from dangerous Dinosaurs").define("animalsFearDinos", true);
        ANU_BLOCK_PLACING = COMMON_BUILDER.comment("True if Anu should be able to place blocks").define("anuBlockPlacing", true);
        ANUBITE_HAS_COOLDOWN = COMMON_BUILDER.comment("True if the Anubite should be able to spawn again after some time").define("anubiteHasCooldown", false);
        ANUBITE_COOLDOWN = COMMON_BUILDER.comment("How long it takes for the Anubite to respawn, in ticks").defineInRange("anubiteCooldown", 72000, 1200, 1000000000);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Machine Config");
        CULTURE_VAT_FAIL_CHANCE = COMMON_BUILDER.comment("Percentage chance that the Culture Vat will break").defineInRange("cultureVatFailChance", 10, 0, 100);
        MACHINES_REQUIRE_ENERGY = COMMON_BUILDER.comment("True if machines require Energy to operate").define("machinesRequireEnergy", false);
        MACHINE_MAX_ENERGY = COMMON_BUILDER.comment("Max stored Energy machines can have").defineInRange("machineMaxEnergy", 1000, 1, 1000000);
        MACHINE_TRANSFER_RATE = COMMON_BUILDER.comment("Max Energy machines can transfer per tick").defineInRange("machineTransferRate", 10, 1, 1000000);
        MACHINE_ENERGY_USAGE = COMMON_BUILDER.comment("How much Energy machines consume per tick").defineInRange("machineEnergyUsage", 1, 1, 1000000);
        COMMON_BUILDER.pop();

        FERN_TICK_RATE = COMMON_BUILDER.comment("How often ferns try to grow. Higher number = less growth").defineInRange("fernTickRate", 2, 1, 1000000);

        COMMON_SPEC = COMMON_BUILDER.build();
    }

    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue CUSTOM_MAIN_MENU = CLIENT_BUILDER.comment("True if the Custom Main Menu should be enabled").define("customMainMenu", true);
    private static final ModConfigSpec.BooleanValue HELMET_OVERLAYS = CLIENT_BUILDER.comment("True if skull helmet and ancient helmet should render overlays like vanilla pumpkin").define("helmetOverlays", true);
    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static boolean generateHellShips;
    public static boolean generateMoai;
    public static boolean generateFossils;
    public static boolean generatePermafrost;
    public static boolean generateVolcanicRock;
    public static boolean generateVolcanoBiome;
    public static int volcanoBiomeRarity;

    public static boolean fishArePersistent;
    public static boolean spawnTarSlimes;
    public static int tarSlimesSpawnRate;

    public static boolean featheredDilo;
    public static boolean featheredCerato;
    public static boolean featheredDryo;
    public static boolean healingDinos;
    public static boolean enableHunger;
    public static boolean enableStarvation;
    public static boolean breedingDinos;
    public static boolean eggsLikeChickens;
    public static boolean whipToTameDino;
    public static int pregnancyDuration;
    public static boolean dinosEatBlocks;
    public static boolean dinosBreakBlocks;
    public static double blockBreakHardness;
    public static boolean dinosEatModdedMobs;
    public static boolean animalsFearDinos;
    public static int flyingTargetMaxHeight;
    public static boolean anuBlockPlacing;
    public static boolean anubiteHasCooldown;
    public static int anubiteCooldown;

    public static int cultureVatFailChance;
    public static boolean machinesRequireEnergy;
    public static int machineMaxEnergy;
    public static int machineTransferRate;
    public static int machineEnergyUsage;
    public static int fernTickRate;

    public static boolean customMainMenu;
    public static boolean helmetOverlays;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            generateHellShips = GENERATE_HELL_SHIPS.get();
            generateMoai = GENERATE_MOAI.get();
            generateFossils = GENERATE_FOSSILS.get();
            generatePermafrost = GENERATE_PERMAFROST.get();
            generateVolcanicRock = GENERATE_VOLCANIC_ROCK.get();
            generateVolcanoBiome = GENERATE_VOLCANO_BIOME.get();
            volcanoBiomeRarity = VOLCANO_BIOME_RARITY.get();

            fishArePersistent = FISH_ARE_PERSISTENT.get();
            spawnTarSlimes = SPAWN_TAR_SLIMES.get();
            tarSlimesSpawnRate = TAR_SLIMES_SPAWN_RATE.get();

            featheredDilo = FEATHERED_DILO.get();
            featheredCerato = FEATHERED_CERATO.get();
            featheredDryo = FEATHERED_DRYO.get();
            healingDinos = HEALING_DINOS.get();
            enableHunger = ENABLE_HUNGER.get();
            enableStarvation = ENABLE_STARVATION.get();
            breedingDinos = BREEDING_DINOS.get();
            eggsLikeChickens = EGGS_LIKE_CHICKENS.get();
            whipToTameDino = WHIP_TO_TAME_DINO.get();
            pregnancyDuration = PREGNANCY_DURATION.get();
            dinosEatBlocks = DINOS_EAT_BLOCKS.get();
            dinosBreakBlocks = DINOS_BREAK_BLOCKS.get();
            blockBreakHardness = BLOCK_BREAK_HARDNESS.get();
            dinosEatModdedMobs = DINOS_EAT_MODDED_MOBS.get();
            animalsFearDinos = ANIMALS_FEAR_DINOS.get();
            flyingTargetMaxHeight = FLYING_TARGET_MAX_HEIGHT.get();
            anuBlockPlacing = ANU_BLOCK_PLACING.get();
            anubiteHasCooldown = ANUBITE_HAS_COOLDOWN.get();
            anubiteCooldown = ANUBITE_COOLDOWN.get();

            cultureVatFailChance = CULTURE_VAT_FAIL_CHANCE.get();
            machinesRequireEnergy = MACHINES_REQUIRE_ENERGY.get();
            machineMaxEnergy = MACHINE_MAX_ENERGY.get();
            machineTransferRate = MACHINE_TRANSFER_RATE.get();
            machineEnergyUsage = MACHINE_ENERGY_USAGE.get();
            fernTickRate = FERN_TICK_RATE.get();
        } else if (event.getConfig().getSpec() == CLIENT_SPEC) {
            customMainMenu = CUSTOM_MAIN_MENU.get();
            helmetOverlays = HELMET_OVERLAYS.get();
        }
    }

    /**
     * Looks up a boolean config toggle by its config key (as used in {@code data/.../variants/} config-condition JSON)
     */
    public static boolean isEnabled(String key) {
        return switch (key) {
            case "generateHellShips" -> generateHellShips;
            case "generateMoai" -> generateMoai;
            case "generateFossils" -> generateFossils;
            case "generatePermafrost" -> generatePermafrost;
            case "generateVolcanicRock" -> generateVolcanicRock;
            case "generateVolcanoBiome" -> generateVolcanoBiome;
            case "fishArePersistent" -> fishArePersistent;
            case "spawnTarSlimes" -> spawnTarSlimes;
            case "featheredDilo" -> featheredDilo;
            case "featheredCerato" -> featheredCerato;
            case "featheredDryo" -> featheredDryo;
            case "healingDinos" -> healingDinos;
            case "enableHunger" -> enableHunger;
            case "enableStarvation" -> enableStarvation;
            case "breedingDinos" -> breedingDinos;
            case "eggsLikeChickens" -> eggsLikeChickens;
            case "whipToTameDino" -> whipToTameDino;
            case "dinosEatBlocks" -> dinosEatBlocks;
            case "dinosBreakBlocks" -> dinosBreakBlocks;
            case "dinosEatModdedMobs" -> dinosEatModdedMobs;
            case "animalsFearDinos" -> animalsFearDinos;
            case "anuBlockPlacing" -> anuBlockPlacing;
            case "anubiteHasCooldown" -> anubiteHasCooldown;
            case "machinesRequireEnergy" -> machinesRequireEnergy;
            case "customMainMenu" -> customMainMenu;
            case "helmetOverlays" -> helmetOverlays;
            default -> false;
        };
    }
}
