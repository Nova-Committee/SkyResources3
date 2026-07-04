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
import net.minecraft.world.level.Level;

public final class OreAlchemyDustItem extends Item {
    public OreAlchemyDustItem(final Properties properties) {
        super(properties);
    }

    public static ItemStack forType(final ResourceKey<OreAlchemyDustType> typeKey) {
        return forType(ModDataPackRegistries.oreAlchemyDustTypeId(typeKey));
    }

    public static ItemStack forType(final ResourceLocation typeId) {
        final ItemStack stack = new ItemStack(ModItems.ORE_ALCHEMICAL_DUST.get());
        ModDataComponents.setResource(stack, ModDataComponents.ORE_ALCHEMY_DUST_TYPE, typeId);
        return stack;
    }

    public static ResourceLocation oreAlchemyDustTypeId(final ItemStack stack) {
        return ModDataComponents.getResource(
                stack,
                ModDataComponents.ORE_ALCHEMY_DUST_TYPE,
                ModDataPackRegistries.oreAlchemyDustTypeId(ModDataPackRegistries.IRON_ORE_ALCHEMY_DUST)
        );
    }

    @Override
    public Component getName(final ItemStack stack) {
        return Component.translatable(translationKey(oreAlchemyDustTypeId(stack)));
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
        final ResourceLocation typeId = oreAlchemyDustTypeId(stack);
        registries.lookup(ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.oreAlchemyDustTypeKey(typeId)))
                .map(reference -> reference.value())
                .ifPresent(type -> {
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.ore_alchemical_dust.source_tag",
                            "#" + type.sourceTag().location()
                    ).withStyle(ChatFormatting.GRAY));
                    tooltipComponents.add(Component.translatable(
                            "item.skyresources.ore_alchemical_dust.rarity",
                            type.rarity()
                    ).withStyle(ChatFormatting.GRAY));
                });
    }

    public static String translationKey(final ResourceLocation typeId) {
        if (Skyresources3.MODID.equals(typeId.getNamespace())) {
            return "item.skyresources.ore_alchemical_dust." + typeId.getPath();
        }
        return "item." + typeId.getNamespace() + ".ore_alchemical_dust." + typeId.getPath().replace('/', '.');
    }
}
