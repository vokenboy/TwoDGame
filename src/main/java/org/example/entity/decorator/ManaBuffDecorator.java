package org.example.entity.decorator;

import org.example.entity.Entity;

public class ManaBuffDecorator extends EquipmentDecorator {

    private final int manaBonus;
    private final int manaRegenBonus;
    private final int spellPowerBonus;
    private final int focusGenerationBonus;

    public ManaBuffDecorator(Entity equipment, int manaBonus) {
        this(equipment, manaBonus, 1);
    }

    public ManaBuffDecorator(Entity equipment, int manaBonus, int level) {
        super(equipment, 1, level); // Uses 1 slot
        this.manaBonus = manaBonus + (enchantmentLevel - 1) * 3; // +3 mana per level
        this.manaRegenBonus = 1 + (enchantmentLevel / 2);
        this.spellPowerBonus = 6 + (enchantmentLevel * 3);
        this.focusGenerationBonus = 2 + enchantmentLevel;

        this.manaRegenPerTick = baseEquipment.manaRegenPerTick + manaRegenBonus;
        this.bonusDamagePercent = baseEquipment.bonusDamagePercent + (hasEnchantment("ELEMENT_LIGHTNING") ? spellPowerBonus + 5 : spellPowerBonus);
        this.statusEffectChance = baseEquipment.statusEffectChance + focusGenerationBonus;

        addEnchantmentTag("MANA_BUFF");
        setDescription(
            "+Max Mana: +" + this.manaBonus,
            "Mana Regen: +" + manaRegenBonus + "/tick",
            "Spell Amplify: +" + spellPowerBonus + "%",
            "Focus Gain: +" + focusGenerationBonus + "% crit scaling"
        );

        this.price = (int) (baseEquipment.price * (1.38 + (enchantmentLevel * 0.24)));
    }

    public void applyBuff(Entity user) {
        if (user == null) {
            return;
        }
        user.maxMana += manaBonus;
        user.mana += manaBonus;
        user.manaRegenPerTick += manaRegenBonus;

        if (getGamePanel() != null) {
            getGamePanel().gameFacade.playSoundEffect(3);
            if (getGamePanel().ui != null) {
                getGamePanel().ui.addMessage("Arcane reserves expanded by " + manaBonus + "!");
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

    public int getManaBonus() {
        return manaBonus;
    }
}