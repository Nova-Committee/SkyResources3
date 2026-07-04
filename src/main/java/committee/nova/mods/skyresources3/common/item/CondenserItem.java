package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
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

public final class CondenserItem extends BlockItem {
    public CondenserItem(final Block block, final Properties properties) {
        super(block, properties.stacksTo(1));
    }

    public static ItemStack forType(final ResourceKey<CondenserType> typeKey) {
        return forType(ModDataPackRegistries.condenserTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.CONDENSER.get());
        ModDataComponents.setResource(stack, ModDataComponents.CONDENSER_TYPE, typeId);
        ModDataComponents.setBlockEntityResource(stack, ModDataComponents.MACHINE_TYPE, typeId);
        VariantModelData.apply(stack, typeId);
        return stack;
    }

    public static ResourceLocation condenserTypeId(final ItemStack stack) {
        return ModDataComponents.getResource(
                stack,
                ModDataComponents.CONDENSER_TYPE,
                ModDataPackRegistries.condenserTypeId(ModDataPackRegistries.IRON_CONDENSER)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(condenserTypeId(stack)));
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
        final ResourceLocation typeId = condenserTypeId(stack);
        registries.lookup(ModDataPackRegistries.CONDENSER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.condenserTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.condenser.speed",
                            Math.round(type.speed() * 100.0F)
                    ).withStyle(ChatFormatting.BLUE));
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.condenser.efficiency",
                            Math.round(type.efficiency() * 100.0F)
                    ).withStyle(ChatFormatting.GREEN));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources.condenser." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".condenser." + typeId.getPath().replace('/', '.');
    }
}
