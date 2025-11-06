package org.example.entity.decorator;

import org.example.entity.Entity;

public class LifeStealDecorator extends EquipmentDecorator {

    private final int siphonPercent;
    private final int regenBonus;
    private final int mitigationBonus;
    private final int damageAmpBonus;

    public LifeStealDecorator(Entity equipment, int lifeStealPercent) {
        this(equipment, lifeStealPercent, 1);
    }

    public LifeStealDecorator(Entity equipment, int lifeStealPercent, int level) {
        super(equipment, 2, level); // Uses 2 slots (powerful enchantment)
        this.siphonPercent = lifeStealPercent + (enchantmentLevel - 1) * 3; // +3% per level for meaningful scaling
        this.regenBonus = 2 + enchantmentLevel;
        this.mitigationBonus = 3 + (enchantmentLevel * 2);

        int vampiricSynergy = hasEnchantment("HEALTH_BUFF") ? 5 : 0;
        vampiricSynergy += hasEnchantment("ELEMENT_POISON") ? 7 : 0;
        this.damageAmpBonus = 5 + (enchantmentLevel * 3) + vampiricSynergy;

        this.lifeStealPercent = siphonPercent;
        this.criticalChance = baseEquipment.criticalChance + (hasEnchantment("CRITICAL") ? 2 * enchantmentLevel : enchantmentLevel);
        this.bonusDamagePercent = baseEquipment.bonusDamagePercent + damageAmpBonus;
        this.damageMitigationPercent = baseEquipment.damageMitigationPercent + mitigationBonus;
        this.healthRegenPerTick = baseEquipment.healthRegenPerTick + regenBonus;
        this.statusEffectChance = baseEquipment.statusEffectChance + (3 * enchantmentLevel);

        addEnchantmentTag("LIFESTEAL");
        setDescription(
            "Life Siphon: " + siphonPercent + "% of damage as healing",
            "Regeneration: +" + regenBonus + " HP/tick",
            "Blood Ward: " + mitigationBonus + "% dmg shield",
            "Predatory Instinct: +" + damageAmpBonus + "% dmg"
        );

        this.price = (int) (baseEquipment.price * (2.15 + (enchantmentLevel * 0.45)));
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result && getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
            if (user != null) {
                user.healthRegenPerTick += regenBonus;
            }
        }

        return result;
    }

    public int getSiphonPercent() {
        return siphonPercent;
    }
}