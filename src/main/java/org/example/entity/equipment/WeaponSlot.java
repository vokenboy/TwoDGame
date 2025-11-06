package org.example.entity.equipment;

import java.awt.Rectangle;

/**
 * Refined equipment slot abstraction for weapon statistics.
 */
public class WeaponSlot extends EquipmentSlot {

    public WeaponSlot(StatProvider statProvider) {
        super(statProvider);
    }

    public int getAttackValue() {
        return getStatsInternal().getAttackValue();
    }

    public Rectangle getAttackArea() {
        return getStatsInternal().getAttackArea();
    }

    public int getMotion1Duration() {
        return getStatsInternal().getMotion1Duration();
    }

    public int getMotion2Duration() {
        return getStatsInternal().getMotion2Duration();
    }

    public int getKnockBackPower() {
        return getStatsInternal().getKnockBackPower();
    }

    public int getLifeStealPercent() {
        return getStatsInternal().getLifeStealPercent();
    }

    public int getCriticalChance() {
        return getStatsInternal().getCriticalChance();
    }

    public int getBonusDamagePercent() {
        return getStatsInternal().getBonusDamagePercent();
    }

    public int getBonusCritDamagePercent() {
        return getStatsInternal().getBonusCritDamagePercent();
    }

    public int getStatusEffectChance() {
        return getStatsInternal().getStatusEffectChance();
    }

    public int getSpeedPercent() {
        return getStatsInternal().getSpeedPercent();
    }

    public int getManaRegenPerTick() {
        return getStatsInternal().getManaRegenPerTick();
    }

    public int getHealthRegenPerTick() {
        return getStatsInternal().getHealthRegenPerTick();
    }

    public int getDamageMitigationPercent() {
        return getStatsInternal().getDamageMitigationPercent();
    }

    public int getElementalResistPercent() {
        return getStatsInternal().getElementalResistPercent();
    }

    public int getGuardStrength() {
        return getStatsInternal().getGuardStrength();
    }
}
