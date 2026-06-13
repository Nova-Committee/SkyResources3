package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.machine.MachineVariant;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public final class CondenserItem extends Item {
    private final MachineVariant variant;

    public CondenserItem(final Properties properties, final MachineVariant variant) {
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
                "item.skyresources3.condenser.speed",
                Math.round(this.variant.speed() * 100.0F)
        ).withStyle(ChatFormatting.BLUE));
        tooltipAdder.accept(Component.translatable(
                "item.skyresources3.condenser.efficiency",
                Math.round(this.variant.efficiency() * 100.0F)
        ).withStyle(ChatFormatting.GREEN));
    }
}
