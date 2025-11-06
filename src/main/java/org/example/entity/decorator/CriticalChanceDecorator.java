package org.example.entity.decorator;

import org.example.entity.Entity;

public class CriticalChanceDecorator extends EquipmentDecorator {

    private final int critChance;
    private final int critDamageBonus;
    private final int tempoBonus;
    private final int bleedChanceBonus;

    public CriticalChanceDecorator(Entity equipment, int critChance) {
        this(equipment, critChance, 1);
    }

    public CriticalChanceDecorator(Entity equipment, int critChance, int level) {
        super(equipment, 1, level); // Uses 1 slot
        int baseChance = critChance + (enchantmentLevel - 1) * 6;
        this.critChance = Math.min(100, baseChance);
        this.critDamageBonus = 20 + (enchantmentLevel * 8);
        this.tempoBonus = 5 + (enchantmentLevel * 3);
        this.bleedChanceBonus = 4 + (enchantmentLevel * 2);

        this.criticalChance = critChanceClamp(baseEquipment.criticalChance + critChance);
        this.criticalChance = critChanceClamp(this.criticalChance + (enchantmentLevel * 4));

        this.bonusCritDamagePercent = baseEquipment.bonusCritDamagePercent + critDamageBonus;
        this.speedPercent = baseEquipment.speedPercent + tempoBonus;
        this.statusEffectChance = baseEquipment.statusEffectChance + bleedChanceBonus;

        if (hasEnchantment("DAMAGE_BUFF")) {
            this.bonusDamagePercent = baseEquipment.bonusDamagePercent + (enchantmentLevel * 4);
        }

        addEnchantmentTag("CRITICAL");
        setDescription(
            "Critical Chance: " + this.criticalChance + "%",
            "Crit Damage: +" + critDamageBonus + "%",
            "Tempo Boost: +" + tempoBonus + "% swing speed",
            "Hemorrhage Chance: +" + bleedChanceBonus + "%"
        );

        this.price = (int) (baseEquipment.price * (1.75 + (enchantmentLevel * 0.32)));
    }

    private int critChanceClamp(int value) {
        return Math.min(100, Math.max(0, value));
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result && getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
        }

        return result;
    }

    public int getCritChance() {
        return critChance;
    }
}