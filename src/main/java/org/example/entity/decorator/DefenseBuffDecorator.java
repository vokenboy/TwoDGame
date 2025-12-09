package org.example.entity.decorator;

import org.example.entity.Entity;

public class DefenseBuffDecorator extends EquipmentDecorator {

    private final int flatDefenseBonus;
    private final int mitigationBonus;
    private final int guardBonus;
    private final int elementalResistBonus;
    private final int regenBonus;

    public DefenseBuffDecorator(Entity equipment, int bonusDefense) {
        this(equipment, bonusDefense, 1);
    }

    public DefenseBuffDecorator(Entity equipment, int bonusDefense, int level) {
        super(equipment, 1, level); // Uses 1 slot
        int levelScaling = Math.max(0, enchantmentLevel - 1);
        this.flatDefenseBonus = bonusDefense + levelScaling;
        this.mitigationBonus = 6 + (enchantmentLevel * 3);
        this.guardBonus = 8 + (enchantmentLevel * 4);
        this.elementalResistBonus = 4 + (enchantmentLevel * 2);
        this.regenBonus = Math.max(1, enchantmentLevel / 2);

        this.defenseValue = baseEquipment.defenseValue + flatDefenseBonus;
        this.attackValue = Math.max(0, baseEquipment.attackValue - levelScaling); // heavier armor trades a little offense
        this.damageMitigationPercent = baseEquipment.damageMitigationPercent + mitigationBonus;
        this.guardStrength = baseEquipment.guardStrength + guardBonus;
        this.elementalResistPercent = baseEquipment.elementalResistPercent + elementalResistBonus;
        this.healthRegenPerTick = baseEquipment.healthRegenPerTick + regenBonus;

        if (hasEnchantment("HEALTH_BUFF")) {
            this.healthRegenPerTick += enchantmentLevel;
        }

        addEnchantmentTag("DEFENSE_BUFF");
        setDescription(
            "+Defense: +" + flatDefenseBonus + " DEF",
            "Damage Soak: " + mitigationBonus + "% less incoming",
            "Guard Stability: +" + guardBonus,
            "Elemental Ward: +" + elementalResistBonus + "%",
            "Regeneration: +" + regenBonus + " HP/tick"
        );

        this.price = (int) (baseEquipment.price * (1.45 + (enchantmentLevel * 0.28)));
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result && getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
        }

        return result;
    }

    public int getFlatDefenseBonus() {
        return flatDefenseBonus;
    }
}