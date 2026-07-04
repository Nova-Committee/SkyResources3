package committee.nova.mods.skyresources3;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;

@Mod.EventBusSubscriber(modid = Skyresources3.MODID)
public final class Config {
    private static final String DEFAULT_VOID_ISLAND_SPAWN_PLATFORM_BLOCK = "minecraft:bedrock";
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_MIGRATION_DEBUG_LOGGING = BUILDER
            .comment("Enable extra startup diagnostics while migrating SkyResources features.")
            .define("enableMigrationDebugLogging", false);
    private static final ForgeConfigSpec.BooleanValue ENABLE_VOID_ISLAND_FEATURES = BUILDER
            .comment("Enable built-in void island progression features as they are migrated.")
            .define("enableVoidIslandFeatures", true);
    private static final ForgeConfigSpec.IntValue ISLAND_PROTECTION_RADIUS = BUILDER
            .comment("Horizontal radius around an island center protected from unrelated player interaction.")
            .defineInRange("islandProtectionRadius", 128, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue VOID_ISLAND_SPAWN_PLATFORM_RADIUS = BUILDER
            .comment("Horizontal radius of the generated shared void island spawn platform.")
            .defineInRange("voidIslandSpawnPlatformRadius", 2, 0, 64);
    private static final ForgeConfigSpec.ConfigValue<String> VOID_ISLAND_SPAWN_PLATFORM_BLOCK = BUILDER
            .comment("Block id used to generate the shared void island spawn platform.")
            .define(
                    "voidIslandSpawnPlatformBlock",
                    DEFAULT_VOID_ISLAND_SPAWN_PLATFORM_BLOCK,
                    Config::validateBlockName
            );
    private static final ForgeConfigSpec.BooleanValue ENABLE_MAGMA_ISLAND = BUILDER
            .comment("Enable the migrated magma island progression path when island generation is implemented.")
            .define("enableMagmaIsland", true);
    private static final ForgeConfigSpec.IntValue HEAVY_SNOWBALL_DAMAGE = BUILDER
            .comment("Damage dealt by a thrown heavy snowball.")
            .defineInRange("heavySnowballDamage", 8, 0, 1024);
    private static final ForgeConfigSpec.IntValue EXPLOSIVE_HEAVY_SNOWBALL_DAMAGE = BUILDER
            .comment("Damage dealt by a thrown explosive heavy snowball before its tiny impact explosion.")
            .defineInRange("explosiveHeavySnowballDamage", 12, 0, 1024);
    private static final ForgeConfigSpec.BooleanValue PLANT_MATTER_BONEMEAL_CAPABILITY = BUILDER
            .comment("Allow plant matter to act as instant bone meal.")
            .define("plantMatterBonemealCapability", true);
    private static final ForgeConfigSpec.BooleanValue INFUSION_STONE_BONEMEAL_CAPABILITY = BUILDER
            .comment("Allow infusion stones to act as instant bone meal when no infusion recipe matches.")
            .define("infusionStoneBonemealCapability", true);
    private static final ForgeConfigSpec.IntValue HEALTH_GEM_MAX_HEALTH = BUILDER
            .comment("Maximum health points a health gem can store.")
            .defineInRange("healthGemMaxHealth", 100, 0, 1024);
    private static final ForgeConfigSpec.DoubleValue HEALTH_GEM_PERCENTAGE = BUILDER
            .comment("Fraction of stored health that will later count as max-health boost.")
            .defineInRange("healthGemPercentage", 0.02D, 0.0D, 100.0D);
    private static final ForgeConfigSpec.IntValue WATER_EXTRACTOR_CAPACITY = BUILDER
            .comment("Water capacity of the water extractor, in millibuckets.")
            .defineInRange("waterExtractorCapacity", 4000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue FLUID_DROPPER_CAPACITY = BUILDER
            .comment("Fluid capacity of the fluid dropper, in millibuckets.")
            .defineInRange("fluidDropperCapacity", 1000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue CRUCIBLE_CAPACITY = BUILDER
            .comment("Fluid and pending solid capacity of the crucible, in millibuckets.")
            .defineInRange("crucibleCapacity", 4000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue CRUCIBLE_SPEED = BUILDER
            .comment("Base crucible speed. A torch melts 1 mB/tick at the default value of 8.")
            .defineInRange("crucibleSpeed", 8, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue ROCK_CRUSHER_POWER_USAGE = BUILDER
            .comment("Energy consumed by the rock crusher per processing tick.")
            .defineInRange("rockCrusherPowerUsage", 100, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue ROCK_CRUSHER_SPEED = BUILDER
            .comment("Rock crusher progress added per tick.")
            .defineInRange("rockCrusherSpeed", 2, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue ROCK_CLEANER_POWER_USAGE = BUILDER
            .comment("Energy consumed by the rock cleaner per processing tick.")
            .defineInRange("rockCleanerPowerUsage", 80, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue ROCK_CLEANER_SPEED = BUILDER
            .comment("Rock cleaner progress added per tick.")
            .defineInRange("rockCleanerSpeed", 5, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue AQUEOUS_CONCENTRATOR_POWER_USAGE = BUILDER
            .comment("Energy consumed by the aqueous concentrator per processing tick.")
            .defineInRange("aqueousConcentratorPowerUsage", 80, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue AQUEOUS_CONCENTRATOR_SPEED = BUILDER
            .comment("Aqueous concentrator progress added per tick.")
            .defineInRange("aqueousConcentratorSpeed", 5, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue AQUEOUS_DECONCENTRATOR_POWER_USAGE = BUILDER
            .comment("Energy consumed by the aqueous deconcentrator per processing tick.")
            .defineInRange("aqueousDeconcentratorPowerUsage", 80, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue AQUEOUS_DECONCENTRATOR_SPEED = BUILDER
            .comment("Aqueous deconcentrator progress added per tick.")
            .defineInRange("aqueousDeconcentratorSpeed", 10, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue WILDLIFE_ATTRACTOR_POWER_USAGE = BUILDER
            .comment("Energy consumed by the wildlife attractor per active tick.")
            .defineInRange("wildlifeAttractorPowerUsage", 40, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue WILDLIFE_ATTRACTOR_WATER_USAGE = BUILDER
            .comment("Water consumed by the wildlife attractor per active tick, in millibuckets.")
            .defineInRange("wildlifeAttractorWaterUsage", 20, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue WILDLIFE_ATTRACTOR_MATTER_TIME = BUILDER
            .comment("Active ticks provided by one plant matter in the wildlife attractor.")
            .defineInRange("wildlifeAttractorMatterTime", 320, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue WILDLIFE_ATTRACTOR_WATER_CAPACITY = BUILDER
            .comment("Water capacity of the wildlife attractor, in millibuckets.")
            .defineInRange("wildlifeAttractorWaterCapacity", 4000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.ConfigValue<String> WILDLIFE_ATTRACTOR_ANIMAL_IDS = BUILDER
            .comment("Comma-separated entity ids the wildlife attractor may spawn.")
            .define(
                    "wildlifeAttractorAnimalIds",
                    "minecraft:sheep,minecraft:cow,minecraft:chicken,minecraft:pig,"
                            + "minecraft:rabbit,minecraft:squid,minecraft:horse,minecraft:parrot"
            );
    private static final ForgeConfigSpec.IntValue COMBUSTION_CONTROLLER_TICKS = BUILDER
            .comment("Cooldown in ticks between smart combustion controller crafts.")
            .defineInRange("combustionControllerTicks", 20, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue DARK_MATTER_WARPER_FUEL_TIME = BUILDER
            .comment("Ticks of fueled operation provided by one dark matter in the dark matter warper.")
            .defineInRange("darkMatterWarperFuelTime", 3600, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue DARK_MATTER_WARPER_EFFECT_PLAYERS = BUILDER
            .comment("Allow the fueled dark matter warper to apply negative effects to nearby non-creative players.")
            .define("darkMatterWarperEffectPlayers", true);
    private static final ForgeConfigSpec.BooleanValue DARK_MATTER_WARPER_EFFECT_NO_FUEL = BUILDER
            .comment("Allow the unfueled dark matter warper to apply short negative effects to nearby non-creative players.")
            .define("darkMatterWarperEffectNoFuel", true);
    private static final ForgeConfigSpec.EnumValue<EndPortalDifficulty> END_PORTAL_MODE = BUILDER
            .comment("End portal difficulty: NORMAL spawns armed silverfish, EASY spawns unarmed silverfish, WUSS spawns none.")
            .defineEnum("endPortalMode", EndPortalDifficulty.NORMAL);
    private static final ForgeConfigSpec.BooleanValue ADD_BEETROOT_SEED_DROP = BUILDER
            .comment("Add beetroot seeds to grass drops.")
            .define("addBeetrootSeedDrop", true);
    private static final ForgeConfigSpec.BooleanValue ADD_MELON_SEED_DROP = BUILDER
            .comment("Add melon seeds to grass drops.")
            .define("addMelonSeedDrop", true);
    private static final ForgeConfigSpec.BooleanValue ADD_PUMPKIN_SEED_DROP = BUILDER
            .comment("Add pumpkin seeds to grass drops.")
            .define("addPumpkinSeedDrop", true);
    private static final ForgeConfigSpec.BooleanValue ADD_COCOA_BEAN_DROP = BUILDER
            .comment("Add cocoa beans to grass drops.")
            .define("addCocoaBeanDrop", true);
    private static final ForgeConfigSpec.BooleanValue ADD_CARROT_DROP = BUILDER
            .comment("Add carrots to grass drops.")
            .define("addCarrotDrop", true);
    private static final ForgeConfigSpec.BooleanValue ADD_POTATO_DROP = BUILDER
            .comment("Add potatoes to grass drops.")
            .define("addPotatoDrop", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enableMigrationDebugLogging;
    public static boolean enableVoidIslandFeatures;
    public static int islandProtectionRadius;
    public static int voidIslandSpawnPlatformRadius = 2;
    public static Block voidIslandSpawnPlatformBlock = Blocks.BEDROCK;
    public static boolean enableMagmaIsland;
    public static int heavySnowballDamage;
    public static int explosiveHeavySnowballDamage;
    public static boolean plantMatterBonemealCapability;
    public static boolean infusionStoneBonemealCapability;
    public static int healthGemMaxHealth;
    public static double healthGemPercentage;
    public static int waterExtractorCapacity;
    public static int fluidDropperCapacity;
    public static int crucibleCapacity;
    public static int crucibleSpeed;
    public static int rockCrusherPowerUsage;
    public static int rockCrusherSpeed;
    public static int rockCleanerPowerUsage;
    public static int rockCleanerSpeed;
    public static int aqueousConcentratorPowerUsage;
    public static int aqueousConcentratorSpeed;
    public static int aqueousDeconcentratorPowerUsage;
    public static int aqueousDeconcentratorSpeed;
    public static int wildlifeAttractorPowerUsage;
    public static int wildlifeAttractorWaterUsage;
    public static int wildlifeAttractorMatterTime;
    public static int wildlifeAttractorWaterCapacity;
    public static List<String> wildlifeAttractorAnimalIds = List.of();
    public static int combustionControllerTicks;
    public static int darkMatterWarperFuelTime;
    public static boolean darkMatterWarperEffectPlayers;
    public static boolean darkMatterWarperEffectNoFuel;
    public static EndPortalDifficulty endPortalMode;
    public static boolean addBeetrootSeedDrop;
    public static boolean addMelonSeedDrop;
    public static boolean addPumpkinSeedDrop;
    public static boolean addCocoaBeanDrop;
    public static boolean addCarrotDrop;
    public static boolean addPotatoDrop;

    static {
        applyConfigValues(ForgeConfigSpec.ConfigValue::getDefault);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        applyConfigValues(ForgeConfigSpec.ConfigValue::get);
    }

    private static void applyConfigValues(final ConfigReader reader) {
        enableMigrationDebugLogging = reader.read(ENABLE_MIGRATION_DEBUG_LOGGING);
        enableVoidIslandFeatures = reader.read(ENABLE_VOID_ISLAND_FEATURES);
        islandProtectionRadius = reader.read(ISLAND_PROTECTION_RADIUS);
        voidIslandSpawnPlatformRadius = reader.read(VOID_ISLAND_SPAWN_PLATFORM_RADIUS);
        voidIslandSpawnPlatformBlock = resolveBlock(reader.read(VOID_ISLAND_SPAWN_PLATFORM_BLOCK));
        enableMagmaIsland = reader.read(ENABLE_MAGMA_ISLAND);
        heavySnowballDamage = reader.read(HEAVY_SNOWBALL_DAMAGE);
        explosiveHeavySnowballDamage = reader.read(EXPLOSIVE_HEAVY_SNOWBALL_DAMAGE);
        plantMatterBonemealCapability = reader.read(PLANT_MATTER_BONEMEAL_CAPABILITY);
        infusionStoneBonemealCapability = reader.read(INFUSION_STONE_BONEMEAL_CAPABILITY);
        healthGemMaxHealth = reader.read(HEALTH_GEM_MAX_HEALTH);
        healthGemPercentage = reader.read(HEALTH_GEM_PERCENTAGE);
        waterExtractorCapacity = reader.read(WATER_EXTRACTOR_CAPACITY);
        fluidDropperCapacity = reader.read(FLUID_DROPPER_CAPACITY);
        crucibleCapacity = reader.read(CRUCIBLE_CAPACITY);
        crucibleSpeed = reader.read(CRUCIBLE_SPEED);
        rockCrusherPowerUsage = reader.read(ROCK_CRUSHER_POWER_USAGE);
        rockCrusherSpeed = reader.read(ROCK_CRUSHER_SPEED);
        rockCleanerPowerUsage = reader.read(ROCK_CLEANER_POWER_USAGE);
        rockCleanerSpeed = reader.read(ROCK_CLEANER_SPEED);
        aqueousConcentratorPowerUsage = reader.read(AQUEOUS_CONCENTRATOR_POWER_USAGE);
        aqueousConcentratorSpeed = reader.read(AQUEOUS_CONCENTRATOR_SPEED);
        aqueousDeconcentratorPowerUsage = reader.read(AQUEOUS_DECONCENTRATOR_POWER_USAGE);
        aqueousDeconcentratorSpeed = reader.read(AQUEOUS_DECONCENTRATOR_SPEED);
        wildlifeAttractorPowerUsage = reader.read(WILDLIFE_ATTRACTOR_POWER_USAGE);
        wildlifeAttractorWaterUsage = reader.read(WILDLIFE_ATTRACTOR_WATER_USAGE);
        wildlifeAttractorMatterTime = reader.read(WILDLIFE_ATTRACTOR_MATTER_TIME);
        wildlifeAttractorWaterCapacity = reader.read(WILDLIFE_ATTRACTOR_WATER_CAPACITY);
        wildlifeAttractorAnimalIds = parseEntityIdList(reader.read(WILDLIFE_ATTRACTOR_ANIMAL_IDS));
        combustionControllerTicks = reader.read(COMBUSTION_CONTROLLER_TICKS);
        darkMatterWarperFuelTime = reader.read(DARK_MATTER_WARPER_FUEL_TIME);
        darkMatterWarperEffectPlayers = reader.read(DARK_MATTER_WARPER_EFFECT_PLAYERS);
        darkMatterWarperEffectNoFuel = reader.read(DARK_MATTER_WARPER_EFFECT_NO_FUEL);
        endPortalMode = reader.read(END_PORTAL_MODE);
        addBeetrootSeedDrop = reader.read(ADD_BEETROOT_SEED_DROP);
        addMelonSeedDrop = reader.read(ADD_MELON_SEED_DROP);
        addPumpkinSeedDrop = reader.read(ADD_PUMPKIN_SEED_DROP);
        addCocoaBeanDrop = reader.read(ADD_COCOA_BEAN_DROP);
        addCarrotDrop = reader.read(ADD_CARROT_DROP);
        addPotatoDrop = reader.read(ADD_POTATO_DROP);
    }

    @FunctionalInterface
    private interface ConfigReader {
        <T> T read(ForgeConfigSpec.ConfigValue<T> value);
    }

    private static List<String> parseEntityIdList(final String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .toList();
    }

    private static boolean validateBlockName(final Object obj) {
        if (!(obj instanceof String blockName)) {
            return false;
        }
        try {
            return BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(blockName));
        } catch (final RuntimeException exception) {
            return false;
        }
    }

    private static Block resolveBlock(final String blockName) {
        try {
            final ResourceLocation id = new ResourceLocation(blockName);
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                return BuiltInRegistries.BLOCK.get(id);
            }
        } catch (final RuntimeException ignored) {
            // Invalid values should be rejected by the config spec; keep runtime fallback defensive.
        }
        return Blocks.GRASS_BLOCK;
    }

    private Config() {
    }

    public enum EndPortalDifficulty {
        NORMAL,
        EASY,
        WUSS
    }
}
