package org.example.entity.equipment;

import java.awt.Rectangle;

/**
 * Provides sensible defaults when an {@link EquipmentSlot} has no backing equipment.
 */
public final class EmptyStatProvider implements StatProvider {

    private static final EmptyStatProvider INSTANCE = new EmptyStatProvider();
    private final EquipmentStats emptyStats;

    private EmptyStatProvider() {
        emptyStats = EquipmentStats.builder()
            .attackValue(1)
            .defenseValue(1)
            .attackArea(new Rectangle())
            .build();
    }

    public static EmptyStatProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public EquipmentStats getStats() {
        return emptyStats;
    }
}
