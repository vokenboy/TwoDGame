package org.example.multiplayer.model;

/**
 * Normalized state that the server rebroadcasts to every connected client.
 */
public record PlayerSnapshot(
        String id,
        String name,
        int worldX,
        int worldY,
        String direction,
        int spriteNum,
        boolean moving,
        boolean attacking,
        int currentMap,
        long timestamp
) {
}
