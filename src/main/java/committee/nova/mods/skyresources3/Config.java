package committee.nova.mods.skyresources3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Skyresources3.MODID)
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_MIGRATION_DEBUG_LOGGING = BUILDER
            .comment("Enable extra startup diagnostics while migrating SkyResources features.")
            .define("enableMigrationDebugLogging", false);
    private static final ModConfigSpec.BooleanValue ENABLE_VOID_ISLAND_FEATURES = BUILDER
            .comment("Enable built-in void island progression features as they are migrated.")
            .define("enableVoidIslandFeatures", true);
    private static final ModConfigSpec.BooleanValue ENABLE_MAGMA_ISLAND = BUILDER
            .comment("Enable the migrated magma island progression path when island generation is implemented.")
            .define("enableMagmaIsland", true);
    private static final ModConfigSpec.IntValue HEAVY_SNOWBALL_DAMAGE = BUILDER
            .comment("Damage dealt by a thrown heavy snowball.")
            .defineInRange("heavySnowballDamage", 8, 0, 1024);
    private static final ModConfigSpec.IntValue EXPLOSIVE_HEAVY_SNOWBALL_DAMAGE = BUILDER
            .comment("Damage dealt by a thrown explosive heavy snowball before its tiny impact explosion.")
            .defineInRange("explosiveHeavySnowballDamage", 12, 0, 1024);
    private static final ModConfigSpec.BooleanValue PLANT_MATTER_BONEMEAL_CAPABILITY = BUILDER
            .comment("Allow plant matter to act as instant bone meal.")
            .define("plantMatterBonemealCapability", true);
    private static final ModConfigSpec.IntValue HEALTH_GEM_MAX_HEALTH = BUILDER
            .comment("Maximum health points a health gem can store.")
            .defineInRange("healthGemMaxHealth", 100, 0, 1024);
    private static final ModConfigSpec.DoubleValue HEALTH_GEM_PERCENTAGE = BUILDER
            .comment("Fraction of stored health that will later count as max-health boost.")
            .defineInRange("healthGemPercentage", 0.02D, 0.0D, 100.0D);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableMigrationDebugLogging;
    public static boolean enableVoidIslandFeatures;
    public static boolean enableMagmaIsland;
    public static int heavySnowballDamage;
    public static int explosiveHeavySnowballDamage;
    public static boolean plantMatterBonemealCapability;
    public static int healthGemMaxHealth;
    public static double healthGemPercentage;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableMigrationDebugLogging = ENABLE_MIGRATION_DEBUG_LOGGING.get();
        enableVoidIslandFeatures = ENABLE_VOID_ISLAND_FEATURES.get();
        enableMagmaIsland = ENABLE_MAGMA_ISLAND.get();
        heavySnowballDamage = HEAVY_SNOWBALL_DAMAGE.get();
        explosiveHeavySnowballDamage = EXPLOSIVE_HEAVY_SNOWBALL_DAMAGE.get();
        plantMatterBonemealCapability = PLANT_MATTER_BONEMEAL_CAPABILITY.get();
        healthGemMaxHealth = HEALTH_GEM_MAX_HEALTH.get();
        healthGemPercentage = HEALTH_GEM_PERCENTAGE.get();
    }

    private Config() {
    }
}
