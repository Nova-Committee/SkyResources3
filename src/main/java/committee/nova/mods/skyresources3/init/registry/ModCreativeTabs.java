package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.DirtyGemItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            Skyresources3.MODID
    );

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.skyresources.main"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.CACTUS_FRUIT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.COMPRESSED_COAL_BLOCK.get());
                        output.accept(ModItems.COAL_INFUSED_BLOCK.get());
                        output.accept(ModItems.SANDY_NETHERRACK.get());
                        output.accept(ModItems.PETRIFIED_WOOD.get());
                        output.accept(ModItems.PETRIFIED_PLANKS.get());
                        output.accept(ModItems.MAGMAFIED_STONE.get());
                        output.accept(ModItems.HEAVY_SNOW.get());
                        output.accept(ModItems.BLAZE_POWDER_BLOCK.get());
                        output.accept(ModItems.DARK_MATTER_BLOCK.get());
                        output.accept(ModItems.LIGHT_MATTER_BLOCK.get());
                        output.accept(ModItems.ALCHEMICAL_GLASS.get());
                        output.accept(ModItems.FUSION_TABLE.get());
                        output.accept(ModItems.DIRT_FURNACE.get());
                        output.accept(ModItems.QUICK_DROPPER.get());
                        output.accept(ModItems.DARK_MATTER_WARPER.get());
                        output.accept(ModItems.END_PORTAL_CORE.get());
                        output.accept(ModItems.SILVERFISH_DISRUPTOR.get());
                        output.accept(ModItems.FLUID_DROPPER.get());
                        output.accept(ModItems.CRUCIBLE.get());
                        output.accept(ModItems.CRUCIBLE_INSERTER.get());
                        output.accept(ModItems.ROCK_CRUSHER.get());
                        output.accept(ModItems.ROCK_CLEANER.get());
                        output.accept(ModItems.AQUEOUS_CONCENTRATOR.get());
                        output.accept(ModItems.AQUEOUS_DECONCENTRATOR.get());
                        output.accept(ModItems.WILDLIFE_ATTRACTOR.get());
                        ModDataPackRegistries.BUILTIN_CASING_TYPES.forEach(
                                typeKey -> output.accept(MachineCasingItem.forType(typeKey))
                        );
                        output.accept(ModItems.COMBUSTION_COLLECTOR.get());
                        output.accept(ModItems.COMBUSTION_CONTROLLER.get());
                        output.accept(ModItems.MINI_FREEZER.get());
                        output.accept(ModItems.IRON_FREEZER.get());
                        output.accept(ModItems.LIGHT_FREEZER.get());
                        output.accept(ModItems.LIFE_INFUSER.get());
                        output.accept(ModItems.LIFE_INJECTOR.get());
                        output.accept(ModItems.CACTUS_FRUIT_NEEDLE.get());
                        output.accept(ModItems.DRY_CACTUS.get());
                        output.accept(ModItems.CACTUS_FRUIT.get());
                        output.accept(ModItems.FLESHY_SNOW_NUGGET.get());
                        output.accept(ModItems.CACTUS_NEEDLE.get());
                        output.accept(ModItems.CRYSTAL_SHARD.get());
                        output.accept(ModItems.PRIMUS_ALCHEMICAL_DUST.get());
                        output.accept(ModItems.SECUNDUS_ALCHEMICAL_DUST.get());
                        output.accept(ModItems.TERTIUS_ALCHEMICAL_DUST.get());
                        output.accept(ModItems.QUARTUS_ALCHEMICAL_DUST.get());
                        parameters.holders().lookup(ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES)
                                .ifPresent(registry -> registry.listElements()
                                        .filter(reference -> reference.value().isAvailable(parameters.holders()))
                                        .forEach(reference -> output.accept(OreAlchemyDustItem.forType(reference.key()))));
                        parameters.holders().lookup(ModDataPackRegistries.DIRTY_GEM_TYPES)
                                .ifPresent(registry -> registry.listElements()
                                        .filter(reference -> reference.value().isAvailable(parameters.holders()))
                                        .forEach(reference -> output.accept(DirtyGemItem.forType(reference.key()))));
                        output.accept(ModItems.ALCHEMICAL_COAL.get());
                        output.accept(ModItems.WOODEN_HEAT_COMPONENT.get());
                        output.accept(ModItems.STONE_ALCHEMY_COMPONENT.get());
                        output.accept(ModItems.ALCHEMICAL_GOLD_INGOT.get());
                        output.accept(ModItems.ALCHEMICAL_IRON_INGOT.get());
                        output.accept(ModItems.ALCHEMICAL_GOLD_NEEDLE.get());
                        output.accept(ModItems.ALCHEMICAL_DIAMOND.get());
                        output.accept(ModItems.PLANT_MATTER.get());
                        output.accept(ModItems.ADVANCED_POWER_COMPONENT.get());
                        output.accept(ModItems.FROZEN_IRON_COOLING_COMPONENT.get());
                        output.accept(ModItems.DARK_MATTER.get());
                        output.accept(ModItems.ENRICHED_BONEMEAL.get());
                        output.accept(ModItems.LIGHT_MATTER.get());
                        output.accept(ModItems.SAWDUST.get());
                        output.accept(ModItems.QUARTZ_AMPLIFICATION_COMPONENT.get());
                        output.accept(ModItems.CRUSHED_STONE.get());
                        output.accept(ModItems.RADIOACTIVE_MIX.get());
                        output.accept(ModItems.FROZEN_IRON_INGOT.get());
                        output.accept(ModItems.CRUSHED_NETHERRACK.get());
                        output.accept(ModItems.WATER_EXTRACTOR.get());
                        output.accept(ModItems.SURVIVALIST_FISHING_ROD.get());
                        output.accept(ModItems.HEAVY_SNOWBALL.get());
                        output.accept(ModItems.HEAVY_EXPLOSIVE_SNOWBALL.get());
                        output.accept(ModItems.CACTUS_CUTTING_KNIFE.get());
                        output.accept(ModItems.STONE_CUTTING_KNIFE.get());
                        output.accept(ModItems.IRON_CUTTING_KNIFE.get());
                        output.accept(ModItems.DIAMOND_CUTTING_KNIFE.get());
                        output.accept(ModItems.STONE_GRINDER.get());
                        output.accept(ModItems.IRON_GRINDER.get());
                        output.accept(ModItems.DIAMOND_GRINDER.get());
                        output.accept(ModItems.SANDSTONE_INFUSION_STONE.get());
                        output.accept(ModItems.RED_SANDSTONE_INFUSION_STONE.get());
                        output.accept(ModItems.ALCHEMICAL_INFUSION_STONE.get());
                        output.accept(ModItems.HEALTH_GEM.get());
                        output.accept(ModItems.CRYSTAL_FLUID_BUCKET.get());
                        for (final var heaterType : ModDataPackRegistries.BUILTIN_COMBUSTION_HEATER_TYPES) {
                            output.accept(CombustionHeaterItem.forType(heaterType));
                        }
                        for (final var providerType : ModDataPackRegistries.BUILTIN_HEAT_PROVIDER_TYPES) {
                            output.accept(HeatProviderItem.forType(providerType));
                        }
                        for (final var condenserType : ModDataPackRegistries.BUILTIN_CONDENSER_TYPES) {
                            output.accept(CondenserItem.forType(condenserType));
                        }
                    })
                    .build()
    );

    public static void register(final IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    private ModCreativeTabs() {
    }
}
