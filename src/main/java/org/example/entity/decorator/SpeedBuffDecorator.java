package org.example.entity.decorator;

import org.example.entity.Entity;

public class SpeedBuffDecorator extends EquipmentDecorator {

    private final int speedBonus;
    private final int speedPercentBonus;
    private final int dodgeBonus;
    private final int tempoReduction;
    private boolean buffApplied = false;

    public SpeedBuffDecorator(Entity equipment, int speedBonus) {
        this(equipment, speedBonus, 1);
    }

    public SpeedBuffDecorator(Entity equipment, int speedBonus, int level) {
        super(equipment, 1, level); // Uses 1 slot
        this.speedBonus = speedBonus + Math.max(0, enchantmentLevel - 1); // +1 speed per level after the first
        this.speedPercentBonus = 8 + (enchantmentLevel * 4);
        this.dodgeBonus = 5 + (enchantmentLevel * 2);
        this.tempoReduction = 1 + (enchantmentLevel / 2);

        this.speedPercent = baseEquipment.speedPercent + speedPercentBonus;
        this.statusEffectChance = baseEquipment.statusEffectChance + dodgeBonus;
        this.motion1_duration = Math.max(3, baseEquipment.motion1_duration - tempoReduction);
        this.motion2_duration = Math.max(3, baseEquipment.motion2_duration - tempoReduction);

        if (hasEnchantment("CRITICAL")) {
            this.criticalChance = Math.min(100, baseEquipment.criticalChance + enchantmentLevel * 2);
        }

        addEnchantmentTag("SPEED_BUFF");
        setDescription(
            "+Speed: +" + speedBonus,
            "Momentum: +" + speedPercentBonus + "% move/attack",
            "Evasion Window: +" + dodgeBonus + "%",
            "Tempo Recovery: -" + tempoReduction + " frames"
        );

        this.price = (int) (baseEquipment.price * (1.68 + (enchantmentLevel * 0.33)));
    }

    public void applyBuff(Entity user) {
        if (user == null || buffApplied) {
            return;
        }
        user.speed += speedBonus;
        user.speedPercent += speedPercentBonus;
        user.statusEffectChance += dodgeBonus;
        buffApplied = true;

        if (getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
            if (getGamePanel().ui != null) {
                getGamePanel().ui.addMessage("Agility increased by " + speedBonus + "!");
            }
        }
    }

    public void removeBuff(Entity user) {
        if (user == null || !buffApplied) {
            return;
        }
        user.speed -= speedBonus;
        user.speedPercent -= speedPercentBonus;
        user.statusEffectChance -= dodgeBonus;
        buffApplied = false;
    }

    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);

        if (result) {
            applyBuff(user);
        }

        return result;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }
}