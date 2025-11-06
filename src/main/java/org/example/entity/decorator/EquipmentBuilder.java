package org.example.entity.decorator;

import org.example.entity.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Advanced Equipment Builder with smart optimization, budgeting, and slot validation.
 * This consolidates the previous builder variants into one extensible implementation.
 */
public class EquipmentBuilder {

    private Entity equipment;
    private String presetName = "";
    private int totalSlots;
    private int usedSlots;
    private int totalCost;
    private int maxBudget = Integer.MAX_VALUE;
    private boolean autoOptimize;

    private final List<EnchantmentRecord> appliedEnchantments = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final Map<String, Integer> synergyBonuses = new HashMap<>();
    private final List<String> suggestions = new ArrayList<>();

    private enum Priority {
        LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);
        final int value;

        Priority(int value) {
            this.value = value;
        }
    }

    private static final class EnchantmentRecord {
        final String type;
        final int level;
        final int slotsUsed;
        final int cost;
        final Priority priority;

        EnchantmentRecord(String type, int level, int slotsUsed, int cost, Priority priority) {
            this.type = type;
            this.level = level;
            this.slotsUsed = slotsUsed;
            this.cost = cost;
            this.priority = priority;
        }
    }

    @FunctionalInterface
    private interface EnchantmentApplier {
        boolean apply();
    }

    private EquipmentBuilder(Entity baseEquipment) {
        this.equipment = Objects.requireNonNull(baseEquipment, "Base equipment cannot be null");
        this.totalSlots = baseEquipment.enchantmentSlots;
        this.usedSlots = baseEquipment.usedEnchantmentSlots;

        if (baseEquipment.appliedEnchantments != null) {
            for (String tag : baseEquipment.appliedEnchantments) {
                appliedEnchantments.add(new EnchantmentRecord(tag, 1, 0, 0, Priority.LOW));
            }
        }
    }

    public static EquipmentBuilder create(Entity baseEquipment) {
        return new EquipmentBuilder(baseEquipment);
    }

    // -------------------------------
    // Configuration API
    // -------------------------------

    public EquipmentBuilder withBudget(int maxCoins) {
        this.maxBudget = Math.max(0, maxCoins);
        return this;
    }

    public EquipmentBuilder enableAutoOptimize() {
        this.autoOptimize = true;
        return this;
    }

    public EquipmentBuilder withSlots(int slots) {
        int sanitized = Math.max(0, slots);
        this.equipment.enchantmentSlots = sanitized;
        this.totalSlots = sanitized;
        refreshSlotUsage();
        return this;
    }

    // -------------------------------
    // Builder operations
    // -------------------------------

    public EquipmentBuilder withDamageBuff(int bonus) {
        return withDamageBuff(bonus, 1);
    }

    public EquipmentBuilder withDamageBuff(int bonus, int level) {
        return applyEnchantment("Damage Buff", () -> {
            int cost = calculateCost(equipment.price, 1.5, level);
            if (!ensureBudget("Damage Buff", cost) || !ensureSlots("Damage Buff", 1)) {
                return false;
            }
            equipment = new DamageBuffDecorator(equipment, bonus, level);
            commitCost(cost);
            recordEnchantment("DAMAGE_BUFF", level, 1, cost, Priority.HIGH);
            checkDamageSynergies();
            return true;
        });
    }

    public EquipmentBuilder withDefenseBuff(int bonus) {
        return withDefenseBuff(bonus, 1);
    }

    public EquipmentBuilder withDefenseBuff(int bonus, int level) {
        return applyEnchantment("Defense Buff", () -> {
            int cost = calculateCost(equipment.price, 1.4, level);
            if (!ensureBudget("Defense Buff", cost) || !ensureSlots("Defense Buff", 1)) {
                return false;
            }
            equipment = new DefenseBuffDecorator(equipment, bonus, level);
            commitCost(cost);
            recordEnchantment("DEFENSE_BUFF", level, 1, cost, Priority.MEDIUM);
            return true;
        });
    }

    public EquipmentBuilder withLifeSteal(int percent) {
        return withLifeSteal(percent, 1);
    }

    public EquipmentBuilder withLifeSteal(int percent, int level) {
        return applyEnchantment("Lifesteal", () -> {
            int cost = calculateCost(equipment.price, 2.0, level);
            if (!ensureBudget("Lifesteal", cost) || !ensureSlots("Lifesteal", 2)) {
                return false;
            }
            equipment = new LifeStealDecorator(equipment, percent, level);
            commitCost(cost);
            recordEnchantment("LIFESTEAL", level, 2, cost, Priority.HIGH);
            checkVampiricSynergy();
            return true;
        });
    }

    public EquipmentBuilder withCriticalChance(int percent) {
        return withCriticalChance(percent, 1);
    }

    public EquipmentBuilder withCriticalChance(int percent, int level) {
        return applyEnchantment("Critical Chance", () -> {
            int cost = calculateCost(equipment.price, 1.7, level);
            if (!ensureBudget("Critical Chance", cost) || !ensureSlots("Critical Chance", 1)) {
                return false;
            }
            equipment = new CriticalChanceDecorator(equipment, percent, level);
            commitCost(cost);
            recordEnchantment("CRITICAL", level, 1, cost, Priority.HIGH);
            checkDamageSynergies();
            return true;
        });
    }

    public EquipmentBuilder withElement(ElementalDecorator.ElementType elementType) {
        return withElement(elementType, 1);
    }

    public EquipmentBuilder withElement(ElementalDecorator.ElementType elementType, int level) {
        return applyEnchantment("Element " + elementType.name, () -> {
            int cost = calculateCost(equipment.price, 1.8, level);
            int requiredSlots = elementType.slotsRequired;

            if (!ensureBudget("Element " + elementType.name, cost) ||
                !ensureSlots("Element " + elementType.name, requiredSlots)) {
                return false;
            }

            if (hasElementConflict(elementType)) {
                warnings.add("Element " + elementType.name + " skipped: conflicts with existing element");
                return false;
            }

            equipment = new ElementalDecorator(equipment, elementType, level);
            commitCost(cost);
            recordEnchantment("ELEMENT_" + elementType.name(), level, requiredSlots, cost, Priority.CRITICAL);
            checkElementalSynergies(elementType);
            return true;
        });
    }

    public EquipmentBuilder withMana(int manaBonus) {
        return withMana(manaBonus, 1);
    }

    public EquipmentBuilder withMana(int manaBonus, int level) {
        return applyEnchantment("Mana Buff", () -> {
            int cost = calculateCost(equipment.price, 1.3, level);
            if (!ensureBudget("Mana Buff", cost) || !ensureSlots("Mana Buff", 1)) {
                return false;
            }
            equipment = new ManaBuffDecorator(equipment, manaBonus, level);
            commitCost(cost);
            recordEnchantment("MANA_BUFF", level, 1, cost, Priority.MEDIUM);
            return true;
        });
    }

    public EquipmentBuilder withHealth(int healthBonus) {
        return withHealth(healthBonus, 1);
    }

    public EquipmentBuilder withHealth(int healthBonus, int level) {
        return applyEnchantment("Health Buff", () -> {
            int cost = calculateCost(equipment.price, 1.4, level);
            if (!ensureBudget("Health Buff", cost) || !ensureSlots("Health Buff", 1)) {
                return false;
            }
            equipment = new HealthBuffDecorator(equipment, healthBonus, level);
            commitCost(cost);
            recordEnchantment("HEALTH_BUFF", level, 1, cost, Priority.MEDIUM);
            return true;
        });
    }

    public EquipmentBuilder withSpeed(int speedBonus) {
        return withSpeed(speedBonus, 1);
    }

    public EquipmentBuilder withSpeed(int speedBonus, int level) {
        return applyEnchantment("Speed Buff", () -> {
            int cost = calculateCost(equipment.price, 1.6, level);
            if (!ensureBudget("Speed Buff", cost) || !ensureSlots("Speed Buff", 1)) {
                return false;
            }
            equipment = new SpeedBuffDecorator(equipment, speedBonus, level);
            commitCost(cost);
            recordEnchantment("SPEED_BUFF", level, 1, cost, Priority.MEDIUM);
            return true;
        });
    }

    // -------------------------------
    // Preset builds
    // -------------------------------

    public EquipmentBuilder asCommon() {
        presetName = "Common";
        return withDamageBuff(1);
    }

    public EquipmentBuilder asUncommon() {
        presetName = "Uncommon";
        return withDamageBuff(2).withDefenseBuff(1);
    }

    public EquipmentBuilder asRare() {
        presetName = "Rare";
        return withDamageBuff(3).withCriticalChance(10);
    }

    public EquipmentBuilder asEpic() {
        presetName = "Epic";
        return withDamageBuff(5).withCriticalChance(15).withHealth(2);
    }

    public EquipmentBuilder asLegendary() {
        presetName = "Legendary";
        return withDamageBuff(7).withCriticalChance(25).withLifeSteal(12);
    }

    public EquipmentBuilder asVampiric() {
        presetName = "Vampiric";
        return withDamageBuff(3)
            .withLifeSteal(20)
            .withElement(ElementalDecorator.ElementType.POISON);
    }

    public EquipmentBuilder asBerserker() {
        presetName = "Berserker";
        return withDamageBuff(6).withCriticalChance(30).withSpeed(2);
    }

    public EquipmentBuilder asTank() {
        presetName = "Tank";
        return withDefenseBuff(5).withHealth(6);
    }

    public EquipmentBuilder asMage() {
        presetName = "Mage";
        return withDamageBuff(2)
            .withMana(6)
            .withElement(ElementalDecorator.ElementType.LIGHTNING);
    }

    public EquipmentBuilder asFlaming() {
        presetName = "Flaming";
        return withElement(ElementalDecorator.ElementType.FIRE)
            .withDamageBuff(5)
            .withCriticalChance(15);
    }

    public EquipmentBuilder asFrozen() {
        presetName = "Frozen";
        return withElement(ElementalDecorator.ElementType.ICE)
            .withDamageBuff(3)
            .withDefenseBuff(3)
            .withHealth(3);
    }

    // -------------------------------
    // Analytics
    // -------------------------------

    public int getTotalCost() {
        return totalCost;
    }

    public int getUsedSlots() {
        return usedSlots;
    }

    public int getAvailableSlots() {
        return Math.max(0, totalSlots - usedSlots);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public Map<String, Integer> getSynergyBonuses() {
        return Collections.unmodifiableMap(synergyBonuses);
    }

    public List<String> getSuggestions() {
        regenerateSuggestions();
        return Collections.unmodifiableList(suggestions);
    }

    // -------------------------------
    // Build
    // -------------------------------

    public Entity build() {
        if (autoOptimize) {
            runAutoOptimization();
        }

        if (!presetName.isEmpty()) {
            String originalName = getOriginalName(equipment.name);
            equipment.name = presetName + " " + originalName;
        }
        return equipment;
    }

    // -------------------------------
    // Internal helpers
    // -------------------------------

    private EquipmentBuilder applyEnchantment(String label, EnchantmentApplier applier) {
        boolean success = false;
        try {
            success = applier.apply();
        } catch (IllegalStateException ex) {
            warnings.add(label + " failed: " + ex.getMessage());
        } catch (Exception ex) {
            warnings.add(label + " failed: " + ex.getMessage());
        }

        if (success) {
            refreshSlotUsage();
            regenerateSuggestions();
        }

        return this;
    }

    private void refreshSlotUsage() {
        usedSlots = equipment.usedEnchantmentSlots;
        totalSlots = equipment.enchantmentSlots;
    }

    private boolean ensureBudget(String label, int cost) {
        if (totalCost + cost > maxBudget) {
            warnings.add(label + " skipped: cost limit exceeded (" + (totalCost + cost) + "/" + maxBudget + ")");
            return false;
        }
        return true;
    }

    private boolean ensureSlots(String label, int requiredSlots) {
        int available = totalSlots - usedSlots;
        if (requiredSlots > available) {
            warnings.add(label + " skipped: requires " + requiredSlots + " slot(s), only " + available + " free");
            return false;
        }
        return true;
    }

    private void commitCost(int cost) {
        totalCost += Math.max(0, cost);
    }

    private int calculateCost(int basePrice, double multiplier, int level) {
        int clampedLevel = Math.max(1, Math.min(level, 5));
        double scaling = 1 + (clampedLevel - 1) * 0.3;
        return (int) Math.round(basePrice * multiplier * scaling);
    }

    private void recordEnchantment(String type, int level, int slots, int cost, Priority priority) {
        appliedEnchantments.add(new EnchantmentRecord(type, level, slots, cost, priority));
        synergyBonuses.putIfAbsent(type, level * 5);
    }

    private void checkDamageSynergies() {
        if (hasExactEnchantment("DAMAGE_BUFF") && hasExactEnchantment("CRITICAL")) {
            synergyBonuses.put("DAMAGE_CRITICAL", 15);
        }
    }

    private void checkVampiricSynergy() {
        boolean lifesteal = hasExactEnchantment("LIFESTEAL");
        boolean poison = hasExactEnchantment("ELEMENT_POISON");
        boolean health = hasExactEnchantment("HEALTH_BUFF");
        if (lifesteal && (poison || health)) {
            synergyBonuses.put("VAMPIRIC", 20);
        }
    }

    private void checkElementalSynergies(ElementalDecorator.ElementType elementType) {
        for (EnchantmentRecord record : appliedEnchantments) {
            if (!record.type.startsWith("ELEMENT_")) {
                continue;
            }
            String existing = record.type.replace("ELEMENT_", "");
            if (elementType.synergies.contains(existing)) {
                synergyBonuses.put("ELEMENT_SYNERGY", 25);
            }
        }
    }

    private boolean hasElementConflict(ElementalDecorator.ElementType newElement) {
        for (EnchantmentRecord record : appliedEnchantments) {
            if (!record.type.startsWith("ELEMENT_")) {
                continue;
            }
            String existing = record.type.replace("ELEMENT_", "");
            if (newElement.conflicts.contains(existing)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasExactEnchantment(String type) {
        for (EnchantmentRecord record : appliedEnchantments) {
            if (record.type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private void runAutoOptimization() {
        if (appliedEnchantments.isEmpty()) {
            return;
        }

        appliedEnchantments.sort(Comparator.comparingInt(record -> -record.priority.value));

        int projectedSlotUsage = 0;
        int projectedBudget = 0;
        int priorityScore = 0;
        for (EnchantmentRecord record : appliedEnchantments) {
            projectedSlotUsage += Math.max(0, record.slotsUsed);
            projectedBudget += Math.max(0, record.cost);
            priorityScore += Math.max(1, record.level) * record.priority.value;
        }

        if (projectedBudget > maxBudget && !warnings.contains("Budget projection exceeds limit")) {
            warnings.add("Budget projection exceeds limit");
        }

        if (projectedSlotUsage < usedSlots) {
            projectedSlotUsage = usedSlots;
        }

        if (priorityScore > projectedSlotUsage * 4 && getAvailableSlots() == 0) {
            if (!suggestions.contains("Revisit low priority enchantments to free slots")) {
                suggestions.add(0, "Revisit low priority enchantments to free slots");
            }
        }

        if (getAvailableSlots() > 0 && !hasExactEnchantment("CRITICAL") && hasExactEnchantment("DAMAGE_BUFF")) {
            if (!suggestions.contains("Consider adding Critical Chance for bonus damage synergy")) {
                suggestions.add(0, "Consider adding Critical Chance for bonus damage synergy");
            }
        }
    }

    private void regenerateSuggestions() {
        suggestions.clear();
        int available = getAvailableSlots();
        if (available <= 0) {
            return;
        }
        if (!hasExactEnchantment("DAMAGE_BUFF")) {
            suggestions.add("Spare slots detected: add Damage Buff to boost attack");
        }
        if (!hasExactEnchantment("CRITICAL") && hasExactEnchantment("DAMAGE_BUFF")) {
            suggestions.add("Critical Chance pairs well with existing damage bonuses");
        }
        if (!hasExactEnchantment("LIFESTEAL") && hasExactEnchantment("ELEMENT_POISON")) {
            suggestions.add("Poison builds benefit from Lifesteal for sustain");
        }

        int slotInvestment = 0;
        boolean hasHighLevel = false;
        for (EnchantmentRecord record : appliedEnchantments) {
            slotInvestment += Math.max(0, record.slotsUsed);
            if (record.level >= 4) {
                hasHighLevel = true;
            }
        }

        if (!hasHighLevel) {
            suggestions.add("No high level enchantments detected: consider investing in higher tiers");
        }
        if (slotInvestment < usedSlots) {
            slotInvestment = usedSlots;
        }
        if (slotInvestment < totalSlots / 2 && available > 1) {
            suggestions.add("Plenty of slots remain: stack defensive buffs for balance");
        }
    }

    private String getOriginalName(String currentName) {
        String name = currentName == null ? "" : currentName.trim();
        if (name.isEmpty()) {
            return name;
        }

        String[] prefixes = {
            "Common ", "Uncommon ", "Rare ", "Epic ", "Legendary ",
            "Vampiric ", "Berserker ", "Tank ", "Mage ",
            "Flaming ", "Frozen ", "Thundering ", "Poisonous "
        };

        boolean removed;
        do {
            removed = false;
            for (String prefix : prefixes) {
                if (name.startsWith(prefix)) {
                    name = name.substring(prefix.length()).trim();
                    removed = true;
                    break;
                }
            }
        } while (removed);

        return name;
    }
}