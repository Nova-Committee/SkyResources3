package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.util.HeatSources;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

record HeatSourceJeiRecipe(ItemStack source, int heat) {
    HeatSourceJeiRecipe {
        source = source.copy();
        if (heat < 0) {
            throw new IllegalArgumentException("Heat source value must not be negative");
        }
    }

    @Override
    public ItemStack source() {
        return this.source.copy();
    }

    static List<HeatSourceJeiRecipe> recipes() {
        final List<HeatSourceJeiRecipe> recipes = new ArrayList<>();
        add(recipes, new ItemStack(Items.LAVA_BUCKET), HeatSources.sourceLavaValue());
        for (final HeatSources.BlockHeatSource source : HeatSources.blockHeatSources()) {
            add(recipes, new ItemStack(source.block()), source.heat());
        }
        heatProviderTypes().ifPresent(registry -> ModDataPackRegistries.BUILTIN_HEAT_PROVIDER_TYPES.forEach(key -> add(
                recipes,
                HeatProviderItem.forType(key),
                Math.round(registry.getValueOrThrow(key).heatPerTick())
        )));
        return List.copyOf(recipes);
    }

    private static Optional<Registry<HeatProviderType>> heatProviderTypes() {
        final Minecraft minecraft = Minecraft.getInstance();
        final Level level = minecraft.level;
        final RegistryAccess registryAccess;
        if (level != null) {
            registryAccess = level.registryAccess();
        } else {
            final ClientPacketListener connection = minecraft.getConnection();
            if (connection == null) {
                return Optional.empty();
            }
            registryAccess = connection.registryAccess();
        }
        return registryAccess.lookup(ModDataPackRegistries.HEAT_PROVIDER_TYPES);
    }

    private static void add(final List<HeatSourceJeiRecipe> recipes, final ItemStack stack, final int heat) {
        if (!stack.isEmpty()) {
            recipes.add(new HeatSourceJeiRecipe(stack, heat));
        }
    }
}
