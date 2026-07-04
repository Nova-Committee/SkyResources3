package committee.nova.mods.skyresources3.common.compat.transfer.item;

import java.util.Objects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemResource {
    public static final ItemResource EMPTY = new ItemResource(ItemStack.EMPTY);

    private final ItemStack stack;

    private ItemResource(final ItemStack stack) {
        this.stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }

    public static ItemResource of(final ItemStack stack) {
        return stack.isEmpty() ? EMPTY : new ItemResource(stack);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public Item getItem() {
        return this.stack.getItem();
    }

    public ItemStack toStack() {
        return this.stack.copy();
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof ItemResource other
                && ItemStack.isSameItemSameTags(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stack.getItem(), this.stack.getTag());
    }
}
