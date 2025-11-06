package org.example.entity.equipment;

import org.example.entity.Entity;

import java.util.Objects;

/**
 * Supplies {@link EquipmentStats} by delegating to an {@link Entity}'s current values.
 */
public final class EntityStatProvider implements StatProvider {

    private final Entity entity;

    public EntityStatProvider(Entity entity) {
        this.entity = Objects.requireNonNull(entity, "entity");
    }

    @Override
    public EquipmentStats getStats() {
        return EquipmentStats.fromEntity(entity);
    }

    public Entity getEntity() {
        return entity;
    }
}
