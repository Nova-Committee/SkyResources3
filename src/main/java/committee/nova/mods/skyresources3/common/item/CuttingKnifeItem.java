package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.init.event.CuttingKnifeEvents;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public final class CuttingKnifeItem extends Item {
    private static final float ATTACK_SPEED = -2.8F;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final int enchantmentValue;
    private final float miningSpeed;

    public CuttingKnifeItem(
            final Properties properties,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        super(properties.durability(durability));
        this.defaultModifiers = attributes(attackDamage);
        this.enchantmentValue = enchantmentValue;
        this.miningSpeed = miningSpeed;
    }

    public float miningSpeed() {
        return this.miningSpeed;
    }

    @Override
    public float getDestroySpeed(final ItemStack stack, final BlockState state) {
        return CuttingKnifeEvents.getDestroySpeed(this, state);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(final EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_FORTUNE || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public int getEnchantmentValue(final ItemStack stack) {
        return this.enchantmentValue;
    }

    private static Multimap<Attribute, AttributeModifier> attributes(final float attackDamage) {
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                BASE_ATTACK_DAMAGE_UUID,
                                "Tool modifier",
                                attackDamage,
                                AttributeModifier.Operation.ADDITION
                        )
                )
                .put(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                BASE_ATTACK_SPEED_UUID,
                                "Tool modifier",
                                ATTACK_SPEED,
                                AttributeModifier.Operation.ADDITION
                        )
                )
                .build();
    }
}
