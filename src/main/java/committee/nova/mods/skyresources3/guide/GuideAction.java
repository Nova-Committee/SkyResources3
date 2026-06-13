package committee.nova.mods.skyresources3.guide;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record GuideAction(
        Type type,
        String target,
        String labelKey,
        Supplier<ItemStack> iconSupplier
) {
    public GuideAction {
        Objects.requireNonNull(type);
        Objects.requireNonNull(target);
        Objects.requireNonNull(labelKey);
        Objects.requireNonNull(iconSupplier);
    }

    public static GuideAction link(final String pageId, final Supplier<ItemStack> iconSupplier) {
        return new GuideAction(Type.LINK, pageId, "", iconSupplier);
    }

    public static GuideAction recipe(final Supplier<ItemStack> iconSupplier) {
        return new GuideAction(Type.RECIPE, "", "", iconSupplier);
    }

    public static GuideAction recipe(final String target, final Supplier<ItemStack> iconSupplier) {
        return new GuideAction(Type.RECIPE, target, "", iconSupplier);
    }

    public static GuideAction image(
            final String structureId,
            final String labelKey,
            final Supplier<ItemStack> iconSupplier
    ) {
        return new GuideAction(Type.IMAGE, structureId, labelKey, iconSupplier);
    }

    public Component label() {
        return switch (this.type) {
            case LINK -> GuidePages.find(this.target)
                    .map(GuidePage::title)
                    .orElseGet(() -> Component.literal(this.target));
            case RECIPE -> this.icon().getHoverName();
            case IMAGE -> Component.translatable(this.labelKey);
        };
    }

    public ItemStack icon() {
        return this.iconSupplier.get().copy();
    }

    public enum Type {
        LINK,
        RECIPE,
        IMAGE
    }
}
