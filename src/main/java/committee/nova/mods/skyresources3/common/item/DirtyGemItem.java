package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModDataComponents;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class DirtyGemItem extends Item {
    public DirtyGemItem(final Properties properties) {
        super(properties);
    }

    public static ItemStack forType(final ResourceKey<DirtyGemType> typeKey) {
        return forType(ModDataPackRegistries.dirtyGemTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.DIRTY_GEM.get());
        stack.set(ModDataComponents.DIRTY_GEM_TYPE.get(), typeId);
        return stack;
    }

    public static ResourceLocation dirtyGemTypeId(final ItemStack stack) {
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
            final List<Component> tooltipComponents,
            final TooltipFlag tooltipFlag
    ) {
        final HolderLookup.Provider registries = context.registries();
        if (registries == null) {
            return;
        }
        final ResourceLocation typeId = dirtyGemTypeId(stack);
        registries.lookup(ModDataPackRegistries.DIRTY_GEM_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.dirtyGemTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.dirty_gem.source_tag",
                            "#" + type.sourceTag().location()
                    ).withStyle(ChatFormatting.GRAY));
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.dirty_gem.rarity",
                            type.rarity()
                    ).withStyle(ChatFormatting.GRAY));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "item.skyresources.dirty_gem." + typeId.getPath();
        }
        return "item." + typeId.getNamespace() + ".dirty_gem." + typeId.getPath().replace('/', '.');
    }
}
