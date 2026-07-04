package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.menu.AqueousMachineMenu;
import committee.nova.mods.skyresources3.common.menu.CombustionCollectorMenu;
import committee.nova.mods.skyresources3.common.menu.CombustionControllerMenu;
import committee.nova.mods.skyresources3.common.menu.CrucibleInserterMenu;
import committee.nova.mods.skyresources3.common.menu.DarkMatterWarperMenu;
import committee.nova.mods.skyresources3.common.menu.DirtFurnaceMenu;
import committee.nova.mods.skyresources3.common.menu.EndPortalCoreMenu;
import committee.nova.mods.skyresources3.common.menu.FreezerMenu;
import committee.nova.mods.skyresources3.common.menu.FusionTableMenu;
import committee.nova.mods.skyresources3.common.menu.LifeInfuserMenu;
import committee.nova.mods.skyresources3.common.menu.LifeInjectorMenu;
import committee.nova.mods.skyresources3.common.menu.MachineCasingMenu;
import committee.nova.mods.skyresources3.common.menu.QuickDropperMenu;
import committee.nova.mods.skyresources3.common.menu.RockCleanerMenu;
import committee.nova.mods.skyresources3.common.menu.RockCrusherMenu;
import committee.nova.mods.skyresources3.common.menu.WildlifeAttractorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Skyresources3.MODID);

    public static final RegistryObject<MenuType<FusionTableMenu>> FUSION_TABLE =
            MENU_TYPES.register("fusion_table", () -> IForgeMenuType.create(FusionTableMenu::new));
    public static final RegistryObject<MenuType<DirtFurnaceMenu>> DIRT_FURNACE =
            MENU_TYPES.register("dirt_furnace", () -> IForgeMenuType.create(DirtFurnaceMenu::new));
    public static final RegistryObject<MenuType<FreezerMenu>> FREEZER =
            MENU_TYPES.register("freezer", () -> IForgeMenuType.create(FreezerMenu::new));
    public static final RegistryObject<MenuType<QuickDropperMenu>> QUICK_DROPPER =
            MENU_TYPES.register("quick_dropper", () -> IForgeMenuType.create(QuickDropperMenu::new));
    public static final RegistryObject<MenuType<DarkMatterWarperMenu>> DARK_MATTER_WARPER =
            MENU_TYPES.register("dark_matter_warper", () -> IForgeMenuType.create(DarkMatterWarperMenu::new));
    public static final RegistryObject<MenuType<EndPortalCoreMenu>> END_PORTAL_CORE =
            MENU_TYPES.register("end_portal_core", () -> IForgeMenuType.create(EndPortalCoreMenu::new));
    public static final RegistryObject<MenuType<CrucibleInserterMenu>> CRUCIBLE_INSERTER =
            MENU_TYPES.register("crucible_inserter", () -> IForgeMenuType.create(CrucibleInserterMenu::new));
    public static final RegistryObject<MenuType<RockCrusherMenu>> ROCK_CRUSHER =
            MENU_TYPES.register("rock_crusher", () -> IForgeMenuType.create(RockCrusherMenu::new));
    public static final RegistryObject<MenuType<RockCleanerMenu>> ROCK_CLEANER =
            MENU_TYPES.register("rock_cleaner", () -> IForgeMenuType.create(RockCleanerMenu::new));
    public static final RegistryObject<MenuType<AqueousMachineMenu>> AQUEOUS_MACHINE =
            MENU_TYPES.register("aqueous_machine", () -> IForgeMenuType.create(AqueousMachineMenu::new));
    public static final RegistryObject<MenuType<WildlifeAttractorMenu>> WILDLIFE_ATTRACTOR =
            MENU_TYPES.register("wildlife_attractor", () -> IForgeMenuType.create(WildlifeAttractorMenu::new));
    public static final RegistryObject<MenuType<MachineCasingMenu>> MACHINE_CASING =
            MENU_TYPES.register("machine_casing", () -> IForgeMenuType.create(MachineCasingMenu::new));
    public static final RegistryObject<MenuType<LifeInfuserMenu>> LIFE_INFUSER =
            MENU_TYPES.register("life_infuser", () -> IForgeMenuType.create(LifeInfuserMenu::new));
    public static final RegistryObject<MenuType<LifeInjectorMenu>> LIFE_INJECTOR =
            MENU_TYPES.register("life_injector", () -> IForgeMenuType.create(LifeInjectorMenu::new));
    public static final RegistryObject<MenuType<CombustionCollectorMenu>> COMBUSTION_COLLECTOR =
            MENU_TYPES.register("combustion_collector", () -> IForgeMenuType.create(CombustionCollectorMenu::new));
    public static final RegistryObject<MenuType<CombustionControllerMenu>> COMBUSTION_CONTROLLER =
            MENU_TYPES.register(
                    "combustion_controller",
                    () -> IForgeMenuType.create(CombustionControllerMenu::new)
            );

    public static void register(final IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

    private ModMenuTypes() {
    }
}
