package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            Skyresources3.MODID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.skyresources3.main"))
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
                        output.accept(ModItems.DARK_MATTER_BLOCK.get());
                        output.accept(ModItems.LIGHT_MATTER_BLOCK.get());
                        output.accept(ModItems.ALCHEMICAL_GLASS.get());
                        output.accept(ModItems.CACTUS_FRUIT.get());
                        output.accept(ModItems.FLESHY_SNOW_NUGGET.get());
                        output.accept(ModItems.CACTUS_NEEDLE.get());
                        output.accept(ModItems.CRYSTAL_SHARD.get());
                        output.accept(ModItems.PLANT_MATTER.get());
                        output.accept(ModItems.DARK_MATTER.get());
                        output.accept(ModItems.LIGHT_MATTER.get());
                        output.accept(ModItems.SAWDUST.get());
                        output.accept(ModItems.CRUSHED_STONE.get());
                        output.accept(ModItems.CRUSHED_NETHERRACK.get());
                    })
                    .build()
    );

    public static void register(final IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    private ModCreativeTabs() {
    }
}
