package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.registry.ModDataComponents;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public final class CombustionHeaterItem extends BlockItem {
    public CombustionHeaterItem(final Block block, final Properties properties) {
        super(block, properties.stacksTo(1));
    }

    public static ItemStack forType(final ResourceKey<CombustionHeaterType> typeKey) {
        return forType(ModDataPackRegistries.combustionHeaterTypeId(typeKey));
    }

    public static ItemStack forType(final Identifier typeId) {
        final ItemStack stack = new ItemStack(ModItems.COMBUSTION_HEATER.get());
        stack.set(ModDataComponents.COMBUSTION_HEATER_TYPE.get(), typeId);
        return stack;
    }

    public static Identifier combustionHeaterTypeId(final ItemStack stack) {
        return stack.getOrDefault(
                ModDataComponents.COMBUSTION_HEATER_TYPE.get(),
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
            final TooltipContext context,
            final TooltipDisplay tooltipDisplay,
            final Consumer<Component> tooltipAdder,
            final TooltipFlag tooltipFlag
    ) {
        final HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        final Identifier typeId = combustionHeaterTypeId(stack);
        registries.lookup(ModDataPackRegistries.COMBUSTION_HEATER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.combustionHeaterTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipAdder.accept(Component.translatable(
                            "item.skyresources.combustion_heater.speed",
                            Math.round(type.speed() * 100.0F)
                    ).withStyle(ChatFormatting.BLUE));
                    tooltipAdder.accept(Component.translatable(
                            "item.skyresources.combustion_heater.efficiency",
                            Math.round(type.efficiency() * 100.0F)
                    ).withStyle(ChatFormatting.GREEN));
                });
    }

    public static String translationKey(final Identifier typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources.combustion_heater." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".combustion_heater." + typeId.getPath().replace('/', '.');
    }
}
