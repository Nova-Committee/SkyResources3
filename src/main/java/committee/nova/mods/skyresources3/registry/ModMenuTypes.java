package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.CrucibleInserterMenu;
import committee.nova.mods.skyresources3.menu.DirtFurnaceMenu;
import committee.nova.mods.skyresources3.menu.FreezerMenu;
import committee.nova.mods.skyresources3.menu.FusionTableMenu;
import committee.nova.mods.skyresources3.menu.QuickDropperMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Skyresources3.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FusionTableMenu>> FUSION_TABLE =
            MENU_TYPES.register("fusion_table", () -> IMenuTypeExtension.create(FusionTableMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<DirtFurnaceMenu>> DIRT_FURNACE =
            MENU_TYPES.register("dirt_furnace", () -> IMenuTypeExtension.create(DirtFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER =
            MENU_TYPES.register("freezer", () -> IMenuTypeExtension.create(FreezerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<QuickDropperMenu>> QUICK_DROPPER =
            MENU_TYPES.register("quick_dropper", () -> IMenuTypeExtension.create(QuickDropperMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CrucibleInserterMenu>> CRUCIBLE_INSERTER =
            MENU_TYPES.register("crucible_inserter", () -> IMenuTypeExtension.create(CrucibleInserterMenu::new));

    public static void register(final IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

    private ModMenuTypes() {
    }
}
