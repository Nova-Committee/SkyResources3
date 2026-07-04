package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.machine.CasingType;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class MachineCasingItem extends BlockItem {
    public MachineCasingItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    public static ItemStack forType(final ResourceKey<CasingType> typeKey) {
        return forType(ModDataPackRegistries.casingTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.MACHINE_CASING.get());
        ModDataComponents.setResource(stack, ModDataComponents.CASING_TYPE, typeId);
        ModDataComponents.setBlockEntityResource(stack, ModDataComponents.CASING_TYPE, typeId);
        VariantModelData.apply(stack, typeId);
        return stack;
    }

    public static ResourceLocation casingTypeId(final ItemStack stack) {
        return ModDataComponents.getResource(
                stack,
                ModDataComponents.CASING_TYPE,
                ModDataPackRegistries.casingTypeId(ModDataPackRegistries.IRON)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(casingTypeId(stack)));
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
        final ResourceLocation typeId = casingTypeId(stack);
        registries.lookup(ModDataPackRegistries.CASING_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.casingTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable("item.skyresources.machine_casing.max_heat", type.maxHeat())
                            .withStyle(ChatFormatting.GRAY));
                    tooltipComponents.add(Component.translatable(
                                    "item.skyresources.machine_casing.efficiency",
                                    Math.round(type.efficiency() * 100.0F)
                            )
                            .withStyle(ChatFormatting.GRAY));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources.machine_casing." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".machine_casing." + typeId.getPath().replace('/', '.');
    }
}
