package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.machine.MachineVariant;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public final class HeatProviderItem extends Item {
    private final MachineVariant variant;

    public HeatProviderItem(final Properties properties, final MachineVariant variant) {
        super(properties.stacksTo(1));
        this.variant = variant;
    }

    public MachineVariant variant() {
        return this.variant;
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
        tooltipAdder.accept(Component.translatable(
                "item.skyresources3.heat_provider.heat",
                Math.round(this.variant.heatPerTick())
        ).withStyle(ChatFormatting.RED));
        tooltipAdder.accept(Component.translatable(
                "item.skyresources3.heat_provider.efficiency",
                Math.round(this.variant.efficiency() * 100.0F)
        ).withStyle(ChatFormatting.GREEN));
    }
}
