package org.example.multiplayer.model;

public record WorldEntitySnapshot(
        String id,
        String className,
        String name,
        int worldX,
        int worldY,
        String direction,
        int spriteNum,
        int currentMap,
        boolean alive,
        int life,
        int maxLife,
        String category,
        long timestamp
) {
}
