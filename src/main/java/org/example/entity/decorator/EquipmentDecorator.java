package org.example.entity.decorator;

import org.example.entity.Entity;
import org.example.main.GamePanel;
import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

public abstract class EquipmentDecorator extends Entity {
    
    protected Entity baseEquipment;
    protected int slotsRequired = 1; // Default 1 slot per enchantment
    protected int enchantmentLevel = 1; // 1-5 star rating
    
    public EquipmentDecorator(Entity equipment) {
        this(equipment, 1, 1); // Default: 1 slot, level 1
    }
    
    public EquipmentDecorator(Entity equipment, int slotsRequired, int enchantmentLevel) {
        super(equipment.getGp());
    this.baseEquipment = requireNonNull(equipment, "Base equipment cannot be null");
        this.slotsRequired = slotsRequired;
        this.enchantmentLevel = Math.min(Math.max(enchantmentLevel, 1), 5); // Clamp 1-5
        
        // Copy original item reference
        if(equipment.originalItem != null) {
            this.originalItem = equipment.originalItem;
        } else {
            this.originalItem = equipment; // This IS the original
        }
        
        // Check if we have enough slots
        int newUsedSlots = equipment.usedEnchantmentSlots + slotsRequired;
        if(newUsedSlots > equipment.enchantmentSlots) {
            throw new IllegalStateException(
                "Cannot apply enchantment: Not enough slots! " +
                "Required: " + slotsRequired + ", Available: " + 
                (equipment.enchantmentSlots - equipment.usedEnchantmentSlots) + "/" +
                equipment.enchantmentSlots
            );
        }
        
        // Update slot usage
        this.usedEnchantmentSlots = newUsedSlots;
        this.enchantmentSlots = equipment.enchantmentSlots;
        
        // Copy applied enchantments list
        this.appliedEnchantments = new ArrayList<>(equipment.appliedEnchantments);
        
        // Copy ALL base properties
        this.type = equipment.type;
        this.name = equipment.name;
        this.description = equipment.description;
        this.price = equipment.price;
        
        // Copy equipment stats
        this.attackValue = equipment.attackValue;
        this.defenseValue = equipment.defenseValue;
        this.attackArea = equipment.attackArea;
        this.motion1_duration = equipment.motion1_duration;
        this.motion2_duration = equipment.motion2_duration;
        this.knockBackPower = equipment.knockBackPower;
        
        // Copy special effects
    this.lifeStealPercent = equipment.lifeStealPercent;
    this.criticalChance = equipment.criticalChance;
    this.bonusDamagePercent = equipment.bonusDamagePercent;
    this.bonusCritDamagePercent = equipment.bonusCritDamagePercent;
    this.damageMitigationPercent = equipment.damageMitigationPercent;
    this.elementalResistPercent = equipment.elementalResistPercent;
    this.manaRegenPerTick = equipment.manaRegenPerTick;
    this.healthRegenPerTick = equipment.healthRegenPerTick;
    this.speedPercent = equipment.speedPercent;
    this.statusEffectChance = equipment.statusEffectChance;
    this.guardStrength = equipment.guardStrength;
        
        // Copy collision/visual properties
        this.solidArea = equipment.solidArea;
        this.solidAreaDefaultX = equipment.solidAreaDefaultX;
        this.solidAreaDefaultY = equipment.solidAreaDefaultY;
        this.collision = equipment.collision;
        this.down1 = equipment.down1;
    }
    
    protected GamePanel getGamePanel() {
        return super.getGp();
    }
    
    protected void addEnchantmentTag(String enchantmentName) {
        appliedEnchantments.add(enchantmentName);
    }
    
    protected boolean hasEnchantment(String enchantmentName) {
        return appliedEnchantments.contains(enchantmentName);
    }
    
    protected boolean hasEnchantmentType(String type) {
        for(String enchant : appliedEnchantments) {
            if(enchant.startsWith(type + "_")) {
                return true;
            }
        }
        return false;
    }
    
    protected String getStarRating() {
        return "★".repeat(enchantmentLevel) + "☆".repeat(5 - enchantmentLevel);
    }
    
    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }
    
    public int getSlotsRequired() {
        return slotsRequired;
    }
    
    @Override
    public boolean use(Entity user) {
        return baseEquipment.use(user);
    }

    protected String stripBaseDescription(String rawDescription) {
        String baseDesc = rawDescription == null ? "" : rawDescription;
        if (!baseDesc.startsWith("[")) {
            return baseDesc;
        }
        int endBracket = baseDesc.indexOf(']');
        if (endBracket == -1) {
            return baseDesc;
        }
        return baseDesc.substring(endBracket + 1);
    }

    protected void setDescription(String... lines) {
        String baseDesc = stripBaseDescription(baseEquipment.description);
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(this.name).append(']').append(baseDesc)
               .append('\n').append(getStarRating());
        if (lines != null) {
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                builder.append('\n').append(line);
            }
        }
        builder.append('\n')
               .append("Slots: ")
               .append(usedEnchantmentSlots)
               .append('/')
               .append(enchantmentSlots);
        this.description = builder.toString();
    }
}