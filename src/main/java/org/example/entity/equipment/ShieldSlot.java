package org.example.entity.equipment;

/**
 * Refined equipment slot abstraction for shield and armor statistics.
 */
public class ShieldSlot extends EquipmentSlot {

    public ShieldSlot(StatProvider statProvider) {
        super(statProvider);
    }

    public int getDefenseValue() {
        return getStatsInternal().getDefenseValue();
    }

    public int getDamageMitigationPercent() {
        return getStatsInternal().getDamageMitigationPercent();
    }

    public int getElementalResistPercent() {
        return getStatsInternal().getElementalResistPercent();
    }

    public int getManaRegenPerTick() {
        return getStatsInternal().getManaRegenPerTick();
    }

    public int getHealthRegenPerTick() {
        return getStatsInternal().getHealthRegenPerTick();
    }

    public int getSpeedPercent() {
        return getStatsInternal().getSpeedPercent();
    }

    public int getStatusEffectChance() {
        return getStatsInternal().getStatusEffectChance();
    }

    public int getGuardStrength() {
        return getStatsInternal().getGuardStrength();
    }
}
