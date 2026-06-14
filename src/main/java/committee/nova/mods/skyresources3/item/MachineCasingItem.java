package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.machine.CasingType;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public final class MachineCasingItem extends BlockItem {
    public MachineCasingItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    public static ItemStack forType(final ResourceKey<CasingType> typeKey) {
        return forType(ModDataPackRegistries.casingTypeId(typeKey));
    }

    public static ItemStack forType(final Identifier typeId) {
        final ItemStack stack = new ItemStack(ModItems.MACHINE_CASING.get());
        stack.set(ModDataComponents.CASING_TYPE.get(), typeId);
        return stack;
    }

    public static Identifier casingTypeId(final ItemStack stack) {
        return stack.getOrDefault(
                ModDataComponents.CASING_TYPE.get(),
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
            final Item.TooltipContext context,
            final TooltipDisplay tooltipDisplay,
            final Consumer<Component> tooltipAdder,
            final TooltipFlag tooltipFlag
    ) {
        final HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        final Identifier typeId = casingTypeId(stack);
        registries.lookup(ModDataPackRegistries.CASING_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.casingTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipAdder.accept(Component.translatable("item.skyresources3.machine_casing.max_heat", type.maxHeat())
                            .withStyle(ChatFormatting.GRAY));
                    tooltipAdder.accept(Component.translatable(
                                    "item.skyresources3.machine_casing.efficiency",
                                    Math.round(type.efficiency() * 100.0F)
                            )
                            .withStyle(ChatFormatting.GRAY));
                });
    }

    public static String translationKey(final Identifier typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "block.skyresources3.machine_casing." + typeId.getPath();
        }
        return "block." + typeId.getNamespace() + ".machine_casing." + typeId.getPath().replace('/', '.');
    }
}
