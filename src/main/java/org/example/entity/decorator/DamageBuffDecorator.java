package org.example.entity.decorator;

import org.example.entity.Entity;

public class DamageBuffDecorator extends EquipmentDecorator {

    private final int flatAttackBonus;
    private final double damageMultiplier;
    private final int percentDamageBonus;
    private final int critChanceBonus;
    private final int critDamageBonus;
    private final int knockBackBonus;
    private final int staggerChanceBonus;

    public DamageBuffDecorator(Entity equipment, int bonusAttack) {
        this(equipment, bonusAttack, 1); // Default level 1
    }

    public DamageBuffDecorator(Entity equipment, int bonusAttack, int level) {
        super(equipment, 1, level); // Uses 1 slot
        int levelScaling = Math.max(0, enchantmentLevel - 1);
        this.flatAttackBonus = bonusAttack + levelScaling;
        this.damageMultiplier = 1.0 + (enchantmentLevel * 0.08); // +8% per level for meaningful scaling
        this.percentDamageBonus = 10 + (enchantmentLevel * 5);

        // Apply core stat changes
        this.attackValue = (int) (baseEquipment.attackValue * damageMultiplier) + flatAttackBonus;
        this.defenseValue = baseEquipment.defenseValue;
        this.bonusDamagePercent = baseEquipment.bonusDamagePercent + percentDamageBonus;

        // Critical bonuses scale if crit already present
        boolean hasCritFoundation = baseEquipment.criticalChance > 0 || hasEnchantment("CRITICAL");
        this.critChanceBonus = hasCritFoundation ? 5 + (enchantmentLevel * 2) : 3 * enchantmentLevel;
        this.criticalChance = Math.min(100, baseEquipment.criticalChance + critChanceBonus);

        this.critDamageBonus = 15 + (enchantmentLevel * 5);
        this.bonusCritDamagePercent = baseEquipment.bonusCritDamagePercent + critDamageBonus;

        this.knockBackBonus = Math.max(1, enchantmentLevel);
        this.knockBackPower = baseEquipment.knockBackPower + knockBackBonus;

        this.staggerChanceBonus = 5 + (enchantmentLevel * 3) + (hasEnchantment("ELEMENT_FIRE") ? 5 : 0);
        this.statusEffectChance = baseEquipment.statusEffectChance + staggerChanceBonus;

        // Faster recovery between swings
        this.motion1_duration = Math.max(4, baseEquipment.motion1_duration - enchantmentLevel);

        addEnchantmentTag("DAMAGE_BUFF");
        setDescription(
            "+Damage: +" + flatAttackBonus + " ATK (" + percentDamageBonus + "% amp)",
            "+Critical Boost: +" + critChanceBonus + "% chance / +" + critDamageBonus + "% dmg",
            "+Knockback: +" + knockBackBonus,
            "+Stagger Chance: +" + staggerChanceBonus + "%"
        );

        this.price = (int) (baseEquipment.price * (1.6 + (enchantmentLevel * 0.35)));
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result && getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
        }

        return result;
    }

    public int getFlatAttackBonus() {
        return flatAttackBonus;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }
}