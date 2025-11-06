package org.example.entity.equipment;

import org.example.entity.Entity;

import java.awt.Rectangle;

/**
 * Immutable snapshot of equipment statistics supplied by a {@link StatProvider} implementation.
 */
public final class EquipmentStats {

    private final int attackValue;
    private final int defenseValue;
    private final Rectangle attackArea;
    private final int motion1Duration;
    private final int motion2Duration;
    private final int knockBackPower;
    private final int lifeStealPercent;
    private final int criticalChance;
    private final int bonusDamagePercent;
    private final int bonusCritDamagePercent;
    private final int damageMitigationPercent;
    private final int elementalResistPercent;
    private final int manaRegenPerTick;
    private final int healthRegenPerTick;
    private final int speedPercent;
    private final int statusEffectChance;
    private final int guardStrength;
    private final Entity visualEntity;

    private EquipmentStats(Builder builder) {
        this.attackValue = builder.attackValue;
        this.defenseValue = builder.defenseValue;
        this.attackArea = builder.attackArea != null ? builder.attackArea : new Rectangle();
        this.motion1Duration = builder.motion1Duration;
        this.motion2Duration = builder.motion2Duration;
        this.knockBackPower = builder.knockBackPower;
        this.lifeStealPercent = builder.lifeStealPercent;
        this.criticalChance = builder.criticalChance;
        this.bonusDamagePercent = builder.bonusDamagePercent;
        this.bonusCritDamagePercent = builder.bonusCritDamagePercent;
        this.damageMitigationPercent = builder.damageMitigationPercent;
        this.elementalResistPercent = builder.elementalResistPercent;
        this.manaRegenPerTick = builder.manaRegenPerTick;
        this.healthRegenPerTick = builder.healthRegenPerTick;
        this.speedPercent = builder.speedPercent;
        this.statusEffectChance = builder.statusEffectChance;
        this.guardStrength = builder.guardStrength;
        this.visualEntity = builder.visualEntity;
    }

    public static EquipmentStats fromEntity(Entity entity) {
        if (entity == null) {
            return builder().build();
        }
        return builder()
            .attackValue(entity.attackValue)
            .defenseValue(entity.defenseValue)
            .attackArea(entity.attackArea)
            .motion1Duration(entity.motion1_duration)
            .motion2Duration(entity.motion2_duration)
            .knockBackPower(entity.knockBackPower)
            .lifeStealPercent(entity.lifeStealPercent)
            .criticalChance(entity.criticalChance)
            .bonusDamagePercent(entity.bonusDamagePercent)
            .bonusCritDamagePercent(entity.bonusCritDamagePercent)
            .damageMitigationPercent(entity.damageMitigationPercent)
            .elementalResistPercent(entity.elementalResistPercent)
            .manaRegenPerTick(entity.manaRegenPerTick)
            .healthRegenPerTick(entity.healthRegenPerTick)
            .speedPercent(entity.speedPercent)
            .statusEffectChance(entity.statusEffectChance)
            .guardStrength(entity.guardStrength)
            .visualEntity(entity)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getAttackValue() {
        return attackValue;
    }

    public int getDefenseValue() {
        return defenseValue;
    }

    public Rectangle getAttackArea() {
        return attackArea;
    }

    public int getMotion1Duration() {
        return motion1Duration;
    }

    public int getMotion2Duration() {
        return motion2Duration;
    }

    public int getKnockBackPower() {
        return knockBackPower;
    }

    public int getLifeStealPercent() {
        return lifeStealPercent;
    }

    public int getCriticalChance() {
        return criticalChance;
    }

    public int getBonusDamagePercent() {
        return bonusDamagePercent;
    }

    public int getBonusCritDamagePercent() {
        return bonusCritDamagePercent;
    }

    public int getDamageMitigationPercent() {
        return damageMitigationPercent;
    }

    public int getElementalResistPercent() {
        return elementalResistPercent;
    }

    public int getManaRegenPerTick() {
        return manaRegenPerTick;
    }

    public int getHealthRegenPerTick() {
        return healthRegenPerTick;
    }

    public int getSpeedPercent() {
        return speedPercent;
    }

    public int getStatusEffectChance() {
        return statusEffectChance;
    }

    public int getGuardStrength() {
        return guardStrength;
    }

    public Entity getVisualEntity() {
        return visualEntity;
    }

    public static final class Builder {
        private int attackValue;
        private int defenseValue;
        private Rectangle attackArea;
        private int motion1Duration;
        private int motion2Duration;
        private int knockBackPower;
        private int lifeStealPercent;
        private int criticalChance;
        private int bonusDamagePercent;
        private int bonusCritDamagePercent;
        private int damageMitigationPercent;
        private int elementalResistPercent;
        private int manaRegenPerTick;
        private int healthRegenPerTick;
        private int speedPercent;
        private int statusEffectChance;
        private int guardStrength;
        private Entity visualEntity;

        public Builder attackValue(int attackValue) {
            this.attackValue = attackValue;
            return this;
        }

        public Builder defenseValue(int defenseValue) {
            this.defenseValue = defenseValue;
            return this;
        }

        public Builder attackArea(Rectangle attackArea) {
            this.attackArea = attackArea;
            return this;
        }

        public Builder motion1Duration(int motion1Duration) {
            this.motion1Duration = motion1Duration;
            return this;
        }

        public Builder motion2Duration(int motion2Duration) {
            this.motion2Duration = motion2Duration;
            return this;
        }

        public Builder knockBackPower(int knockBackPower) {
            this.knockBackPower = knockBackPower;
            return this;
        }

        public Builder lifeStealPercent(int lifeStealPercent) {
            this.lifeStealPercent = lifeStealPercent;
            return this;
        }

        public Builder criticalChance(int criticalChance) {
            this.criticalChance = criticalChance;
            return this;
        }

        public Builder bonusDamagePercent(int bonusDamagePercent) {
            this.bonusDamagePercent = bonusDamagePercent;
            return this;
        }

        public Builder bonusCritDamagePercent(int bonusCritDamagePercent) {
            this.bonusCritDamagePercent = bonusCritDamagePercent;
            return this;
        }

        public Builder damageMitigationPercent(int damageMitigationPercent) {
            this.damageMitigationPercent = damageMitigationPercent;
            return this;
        }

        public Builder elementalResistPercent(int elementalResistPercent) {
            this.elementalResistPercent = elementalResistPercent;
            return this;
        }

        public Builder manaRegenPerTick(int manaRegenPerTick) {
            this.manaRegenPerTick = manaRegenPerTick;
            return this;
        }

        public Builder healthRegenPerTick(int healthRegenPerTick) {
            this.healthRegenPerTick = healthRegenPerTick;
            return this;
        }

        public Builder speedPercent(int speedPercent) {
            this.speedPercent = speedPercent;
            return this;
        }

        public Builder statusEffectChance(int statusEffectChance) {
            this.statusEffectChance = statusEffectChance;
            return this;
        }

        public Builder guardStrength(int guardStrength) {
            this.guardStrength = guardStrength;
            return this;
        }

        public Builder visualEntity(Entity visualEntity) {
            this.visualEntity = visualEntity;
            return this;
        }

        public EquipmentStats build() {
            return new EquipmentStats(this);
        }
    }
}
