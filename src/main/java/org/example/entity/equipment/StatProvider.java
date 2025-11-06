package org.example.entity.equipment;

/**
 * Implemented by any component capable of supplying equipment statistics to an {@link EquipmentSlot} abstraction.
 */
public interface StatProvider {

    EquipmentStats getStats();
}
