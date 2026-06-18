package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
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
import net.minecraft.world.level.block.Block;

public final class HeatProviderItem extends BlockItem {
    public HeatProviderItem(final Block block, final Properties properties) {
        super(block, properties.stacksTo(1));
    }

    public static ItemStack forType(final ResourceKey<HeatProviderType> typeKey) {
        return forType(ModDataPackRegistries.heatProviderTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.HEAT_PROVIDER.get());
        stack.set(ModDataComponents.HEAT_PROVIDER_TYPE.get(), typeId);
        return stack;
    }

    public static ResourceLocation heatProviderTypeId(final ItemStack stack) {
        return stack.getOrDefault(
                ModDataComponents.HEAT_PROVIDER_TYPE.get(),
                ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(heatProviderTypeId(stack)));
    }

    @Override
    @Deprecated
    public void appendHoverText(
            final ItemStack stack,
            final TooltipContext context,
            final List<Component> tooltipComponents,
            final TooltipFlag tooltipFlag
    ) {
        final HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        final ResourceLocation typeId = heatProviderTypeId(stack);
        registries.lookup(ModDataPackRegistries.HEAT_PROVIDER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.heatProviderTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.heat_provider.heat",
                            Math.round(type.heatPerTick())
                    ).withStyle(ChatFormatting.RED));
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.heat_provider.efficiency",
                            Math.round(type.efficiency() * 100.0F)
                    ).withStyle(ChatFormatting.GREEN));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources.heat_provider." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".heat_provider." + typeId.getPath().replace('/', '.');
    }
}
