package org.example.multiplayer.model;

/**
 * Local player state published to the multiplayer server.
 */
public record PlayerState(
        String id,
        String name,
        int worldX,
        int worldY,
        String direction,
        int spriteNum,
        boolean moving,
        boolean attacking,
        int currentMap
) {
}
