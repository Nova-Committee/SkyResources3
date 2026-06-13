package committee.nova.mods.skyresources3.guide;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record GuideStructure(
        String id,
        String titleKey,
        List<BlockEntry> blocks
) {
    public GuideStructure {
        Objects.requireNonNull(id);
        Objects.requireNonNull(titleKey);
        blocks = List.copyOf(blocks);
    }

    public Component title() {
        return Component.translatable(this.titleKey);
    }

    public record BlockEntry(
            int x,
            int y,
            int z,
            Supplier<ItemStack> iconSupplier
    ) {
        public BlockEntry {
            Objects.requireNonNull(iconSupplier);
        }

        public ItemStack icon() {
            return this.iconSupplier.get().copy();
        }

        public Component position() {
            return Component.literal(this.x + ", " + this.y + ", " + this.z);
        }
    }
}
