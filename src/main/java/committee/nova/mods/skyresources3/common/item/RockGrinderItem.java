package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.init.event.RockGrinderEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public final class RockGrinderItem extends Item {
    private static final float ATTACK_SPEED = -2.4F;
    private static final float DEFAULT_MINING_SPEED = 0.5F;

    private final float miningSpeed;

    public RockGrinderItem(
            final Properties properties,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        super(properties
                .durability(durability)
                .enchantable(enchantmentValue)
                .attributes(attributes(attackDamage)));
        this.miningSpeed = miningSpeed;
    }

    public float miningSpeed() {
        return this.miningSpeed;
    }

    @Override
    public float getDestroySpeed(final ItemStack stack, final BlockState state) {
        return RockGrinderEvents.hasResult(state) ? this.miningSpeed : DEFAULT_MINING_SPEED;
    }

    @Override
    public boolean supportsEnchantment(final ItemStack stack, final Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.FORTUNE) || super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(final ItemStack stack, final Holder<Enchantment> enchantment) {
        return this.supportsEnchantment(stack, enchantment);
    }

    private static ItemAttributeModifiers attributes(final float attackDamage) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                attackDamage,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                ATTACK_SPEED,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
