package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.init.registry.ModDataComponents;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class CombustionHeaterItem extends BlockItem {
    public CombustionHeaterItem(final Block block, final Properties properties) {
        super(block, properties.stacksTo(1));
    }

    public static ItemStack forType(final ResourceKey<CombustionHeaterType> typeKey) {
        return forType(ModDataPackRegistries.combustionHeaterTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.COMBUSTION_HEATER.get());
        ModDataComponents.setResource(stack, ModDataComponents.COMBUSTION_HEATER_TYPE, typeId);
        ModDataComponents.setBlockEntityResource(stack, ModDataComponents.MACHINE_TYPE, typeId);
        VariantModelData.apply(stack, typeId);
        return stack;
    }

    public static ResourceLocation combustionHeaterTypeId(final ItemStack stack) {
        return ModDataComponents.getResource(
                stack,
                ModDataComponents.COMBUSTION_HEATER_TYPE,
                ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(combustionHeaterTypeId(stack)));
    }

    @Override
    @Deprecated
    public void appendHoverText(
            final ItemStack stack,
            final Level level,
            final List<Component> tooltipComponents,
            final TooltipFlag tooltipFlag
    ) {
        if (level == null) {
            return;
        }
        final HolderLookup.Provider registries = level.registryAccess();
        final ResourceLocation typeId = combustionHeaterTypeId(stack);
        registries.lookup(ModDataPackRegistries.COMBUSTION_HEATER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.combustionHeaterTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.combustion_heater.speed",
                            Math.round(type.speed() * 100.0F)
                    ).withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.combustion_heater.efficiency",
                            Math.round(type.efficiency() * 100.0F)
                    ).withStyle(ChatFormatting.GREEN));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources.combustion_heater." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".combustion_heater." + typeId.getPath().replace('/', '.');
    }
}
