package committee.nova.mods.skyresources3.guide;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record GuidePage(
        String id,
        String categoryKey,
        String titleKey,
        String textKey,
        Supplier<ItemStack> iconSupplier,
        List<GuideAction> actions
) {
    public GuidePage(
            final String id,
            final String categoryKey,
            final String titleKey,
            final String textKey,
            final Supplier<ItemStack> iconSupplier
    ) {
        this(id, categoryKey, titleKey, textKey, iconSupplier, List.of());
    }

    public GuidePage {
        Objects.requireNonNull(id);
        Objects.requireNonNull(categoryKey);
        Objects.requireNonNull(titleKey);
        Objects.requireNonNull(textKey);
        Objects.requireNonNull(iconSupplier);
        actions = List.copyOf(actions);
    }

    public Component category() {
        return Component.translatable(this.categoryKey);
    }

    public Component title() {
        return Component.translatable(this.titleKey);
    }

    public Component text() {
        return Component.translatable(this.textKey);
    }

    public ItemStack icon() {
        return this.iconSupplier.get().copy();
    }
}
