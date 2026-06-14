package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.registry.ModDataComponents;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public final class DirtyGemItem extends Item {
    public DirtyGemItem(final Properties properties) {
        super(properties);
    }

    public static ItemStack forType(final ResourceKey<DirtyGemType> typeKey) {
        return forType(ModDataPackRegistries.dirtyGemTypeId(typeKey));
    }

    public static ItemStack forType(final Identifier typeId) {
        final ItemStack stack = new ItemStack(ModItems.DIRTY_GEM.get());
        stack.set(ModDataComponents.DIRTY_GEM_TYPE.get(), typeId);
        return stack;
    }

    public static Identifier dirtyGemTypeId(final ItemStack stack) {
        return stack.getOrDefault(
                ModDataComponents.DIRTY_GEM_TYPE.get(),
                ModDataPackRegistries.dirtyGemTypeId(ModDataPackRegistries.EMERALD_DIRTY_GEM)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(dirtyGemTypeId(stack)));
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
        final Identifier typeId = dirtyGemTypeId(stack);
        registries.lookup(ModDataPackRegistries.DIRTY_GEM_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.dirtyGemTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipAdder.accept(Component.translatable(
                            "item.skyresources3.dirty_gem.source_tag",
                            "#" + type.sourceTag().location()
                    ).withStyle(ChatFormatting.GRAY));
                    tooltipAdder.accept(Component.translatable(
                            "item.skyresources3.dirty_gem.rarity",
                            type.rarity()
                    ).withStyle(ChatFormatting.GRAY));
                });
    }

    public static String translationKey(final Identifier typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "item.skyresources3.dirty_gem." + typeId.getPath();
        }
        return "item." + typeId.getNamespace() + ".dirty_gem." + typeId.getPath().replace('/', '.');
    }
}
