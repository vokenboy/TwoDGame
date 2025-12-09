package org.example.entity.decorator;

import org.example.entity.Entity;

public class HealthBuffDecorator extends EquipmentDecorator {

    private final int healthBonus;
    private final int regenBonus;
    private final int mitigationBonus;
    private final int guardBonus;

    public HealthBuffDecorator(Entity equipment, int healthBonus) {
        this(equipment, healthBonus, 1);
    }

    public HealthBuffDecorator(Entity equipment, int healthBonus, int level) {
        super(equipment, 1, level); // Uses 1 slot
        this.healthBonus = healthBonus + (enchantmentLevel - 1) * 3; // +3 HP per level
        this.regenBonus = 2 + (enchantmentLevel / 2);
        this.mitigationBonus = 4 + (enchantmentLevel * 2);
        this.guardBonus = 6 + (enchantmentLevel * 3);

        this.lifeStealPercent = baseEquipment.lifeStealPercent;
        this.criticalChance = baseEquipment.criticalChance;
        this.healthRegenPerTick = baseEquipment.healthRegenPerTick + regenBonus;
        this.damageMitigationPercent = baseEquipment.damageMitigationPercent + mitigationBonus;
        this.guardStrength = baseEquipment.guardStrength + guardBonus;

        if (hasEnchantment("DEFENSE_BUFF")) {
            this.damageMitigationPercent += enchantmentLevel * 2;
        }

        addEnchantmentTag("HEALTH_BUFF");
        setDescription(
            "+Max HP: +" + healthBonus,
            "Regeneration: +" + regenBonus + " HP/tick",
            "Damage Buffer: " + mitigationBonus + "%",
            "Fortitude: +" + guardBonus + " guard"
        );

        this.price = (int) (baseEquipment.price * (1.48 + (enchantmentLevel * 0.26)));
    }

    // This method can be called manually to apply the buff
    public void applyBuff(Entity user) {
        if (user == null) {
            return;
        }
        user.maxLife += healthBonus;
        user.life += healthBonus;
        user.healthRegenPerTick += regenBonus;
        user.damageMitigationPercent += mitigationBonus;

        if (getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
            if (getGamePanel().ui != null) {
                getGamePanel().ui.addMessage("Vitality increased by " + healthBonus + "!");
            }
        }
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result) {
            applyBuff(user);
        }

        return result;
    }

    public int getHealthBonus() {
        return healthBonus;
    }
}