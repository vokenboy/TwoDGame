package org.example.entity.decorator;

import org.example.entity.Entity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ElementalDecorator extends EquipmentDecorator {
    
    public enum ElementType {
        FIRE("Fire", 3, new String[]{"ICE"}, new String[]{"LIGHTNING"}, 2),
        ICE("Ice", 2, new String[]{"FIRE"}, new String[]{"POISON"}, 2),
        LIGHTNING("Lightning", 4, new String[]{"POISON"}, new String[]{"FIRE"}, 2),
        POISON("Poison", 2, new String[]{"LIGHTNING"}, new String[]{"ICE"}, 1);
        
        public final String name;
        public final int bonusDamage;
        public final Set<String> conflicts;
        public final Set<String> synergies;
        public final int slotsRequired;
        
        ElementType(String name, int bonusDamage, String[] conflicts, String[] synergies, int slots) {
            this.name = name;
            this.bonusDamage = bonusDamage;
            this.conflicts = new HashSet<>(Arrays.asList(conflicts));
            this.synergies = new HashSet<>(Arrays.asList(synergies));
            this.slotsRequired = slots;
        }
    }
    
    private ElementType elementType;
    private boolean hasSynergy = false;
    private int statusChanceBonus;
    private int resistBonus;
    private int manaRegenBonus;
    private int speedBonus;
    
    public ElementalDecorator(Entity equipment, ElementType elementType) {
        this(equipment, elementType, 1);
    }
    
    public ElementalDecorator(Entity equipment, ElementType elementType, int level) {
        super(equipment, elementType.slotsRequired, level);
        this.elementType = elementType;
        
        // Check for conflicts with existing enchantments
        checkConflicts();
        
        // Check for synergies
        hasSynergy = checkSynergies();

        // Calculate damage with level and synergy bonus
        int baseDamage = elementType.bonusDamage + (enchantmentLevel - 1); // +1 per level
        if(hasSynergy) {
            baseDamage = (int)(baseDamage * 1.4); // +40% if synergy
        }

        this.attackValue = baseEquipment.attackValue + baseDamage;
        this.defenseValue = baseEquipment.defenseValue;

        this.lifeStealPercent = baseEquipment.lifeStealPercent;
        this.criticalChance = baseEquipment.criticalChance;

        applyElementalPackages(elementType);

        addEnchantmentTag("ELEMENT_" + elementType.name().toUpperCase());
        setDescription(
            "+" + elementType.name + " Dmg: +" + elementType.bonusDamage + " ATK" + (hasSynergy ? " [SYNERGY]" : ""),
            elementalLine(),
            statusLine(),
            sustainLine()
        );

        this.price = (int)(baseEquipment.price * (1.9 + (enchantmentLevel * 0.34)));
    }
    
    private void checkConflicts() {
        for(String enchant : appliedEnchantments) {
            if(enchant.startsWith("ELEMENT_")) {
                String existingElement = enchant.replace("ELEMENT_", "");
                if(elementType.conflicts.contains(existingElement)) {
                    throw new IllegalStateException(
                        "Cannot apply " + elementType.name + " element: " +
                        "Conflicts with " + existingElement + "!"
                    );
                }
            }
        }
    }
    
    private boolean checkSynergies() {
        for(String enchant : appliedEnchantments) {
            if(enchant.startsWith("ELEMENT_")) {
                String existingElement = enchant.replace("ELEMENT_", "");
                if(elementType.synergies.contains(existingElement)) {
                    return true;
                }
            }
            // Check for damage buff synergy
            if(enchant.equals("DAMAGE_BUFF")) {
                return true;
            }
        }
        return false;
    }
    
    private void applyElementalPackages(ElementType type) {
        switch (type) {
            case FIRE:
                statusChanceBonus = 12 + (enchantmentLevel * 4);
                resistBonus = 0;
                manaRegenBonus = 0;
                speedBonus = 0;
                this.bonusDamagePercent = baseEquipment.bonusDamagePercent + 12 + (enchantmentLevel * 3);
                break;
            case ICE:
                statusChanceBonus = 10 + (enchantmentLevel * 3);
                resistBonus = 8 + (enchantmentLevel * 2);
                manaRegenBonus = 0;
                speedBonus = 0;
                this.damageMitigationPercent = baseEquipment.damageMitigationPercent + 5 + (enchantmentLevel * 2);
                break;
            case LIGHTNING:
                statusChanceBonus = 14 + (enchantmentLevel * 3);
                resistBonus = 4 + enchantmentLevel;
                manaRegenBonus = 1 + (enchantmentLevel / 2);
                speedBonus = 6 + (enchantmentLevel * 2);
                this.speedPercent = baseEquipment.speedPercent + speedBonus;
                break;
            case POISON:
                statusChanceBonus = 18 + (enchantmentLevel * 4);
                resistBonus = 2 * enchantmentLevel;
                manaRegenBonus = 0;
                speedBonus = 0;
                this.lifeStealPercent = baseEquipment.lifeStealPercent + (enchantmentLevel * 2);
                break;
            default:
                statusChanceBonus = 5;
                resistBonus = 0;
                manaRegenBonus = 0;
                speedBonus = 0;
        }

        this.statusEffectChance = baseEquipment.statusEffectChance + statusChanceBonus;
        this.elementalResistPercent = baseEquipment.elementalResistPercent + resistBonus;
        this.manaRegenPerTick = baseEquipment.manaRegenPerTick + manaRegenBonus;
    }

    private String elementalLine() {
        if (resistBonus <= 0 && speedBonus <= 0) {
            return "Elemental Ward: Active";
        }
        StringBuilder sb = new StringBuilder("Elemental Ward: ");
        if (resistBonus > 0) {
            sb.append("+" + resistBonus + "% resist");
        }
        if (speedBonus > 0) {
            if (sb.length() > "Elemental Ward: ".length()) {
                sb.append(" / ");
            }
            sb.append("+" + speedBonus + "% celerity");
        }
        return sb.toString();
    }

    private String statusLine() {
        return "Status Affliction: +" + statusChanceBonus + "%";
    }

    private String sustainLine() {
        if (manaRegenBonus > 0 || lifeStealPercent > baseEquipment.lifeStealPercent) {
            StringBuilder sb = new StringBuilder("Sustain: ");
            if (manaRegenBonus > 0) {
                sb.append("Mana +" + manaRegenBonus + "/tick");
            }
            if (lifeStealPercent > baseEquipment.lifeStealPercent) {
                if (sb.length() > "Sustain: ".length()) {
                    sb.append(" / ");
                }
                sb.append("Leech +" + (lifeStealPercent - baseEquipment.lifeStealPercent) + "%");
            }
            return sb.toString();
        }
        return "Sustain: Stable";
    }
    
    @Override
    public boolean use(Entity user) {
        boolean result = super.use(user);
        
        if(result && getGamePanel() != null) {
            switch(elementType) {
                case FIRE:
                    getGamePanel().gameFacade.playSoundEffect(3);
                    break;
                case ICE:
                case LIGHTNING:
                case POISON:
                    getGamePanel().gameFacade.playSoundEffect(3);
                    break;
            }
        }
        
        return result;
    }
    
    public ElementType getElementType() {
        return elementType;
    }
    
    public boolean hasSynergy() {
        return hasSynergy;
    }
}