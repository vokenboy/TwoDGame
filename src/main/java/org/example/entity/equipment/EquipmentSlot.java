package org.example.entity.equipment;

import org.example.entity.Entity;

/**
 * Bridge abstraction representing a character equipment slot that can draw statistics from multiple sources.
 */
public abstract class EquipmentSlot {

    private StatProvider statProvider;

    protected EquipmentSlot(StatProvider statProvider) {
        setStatProvider(statProvider);
    }

    public void equip(Entity entity) {
        if (entity == null) {
            setStatProvider(EmptyStatProvider.getInstance());
        } else {
            setStatProvider(new EntityStatProvider(entity));
        }
    }

    public void setStatProvider(StatProvider statProvider) {
        this.statProvider = statProvider != null ? statProvider : EmptyStatProvider.getInstance();
        onProviderChanged();
    }

    protected void onProviderChanged() {
        // Hooks let concrete slots respond to provider updates.
    }

    protected EquipmentStats getStatsInternal() {
        return statProvider.getStats();
    }

    public EquipmentStats getStats() {
        return getStatsInternal();
    }

    public Entity getEquippedEntity() {
        return getStatsInternal().getVisualEntity();
    }

    public boolean isEmpty() {
        return getEquippedEntity() == null;
    }
}
