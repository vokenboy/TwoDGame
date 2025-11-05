package org.example.main;

import org.example.entity.Entity;

/**
 * COMPREHENSIVE FACADE PATTERN
 *
 * This facade provides a unified interface to major game subsystems:
 * - Audio Management (music & sound effects)
 * - Collision Detection (tiles, objects, entities, events)
 */
public class GameFacade {

    // Subsystems
    private GamePanel gp;
    private Sound music;
    private Sound se;
    private CollisionChecker cChecker;
    private EventHandler eHandler;

    public GameFacade(GamePanel gp, Sound music, Sound se) {
        this.gp = gp;
        this.music = music;
        this.se = se;
        this.cChecker = gp.cChecker;
        this.eHandler = gp.eHandler;
    }

    // ============================================
    // AUDIO MANAGEMENT METHODS
    // ============================================

    public void playBackgroundMusic(int index) {
        music.setFile(index);
        music.play();
        music.loop();
    }

    public void stopBackgroundMusic() {
        music.stop();
    }

    public void playSoundEffect(int index) {
        se.setFile(index);
        se.play();
    }

    public void setMusicVolume(int scale) {
        music.volumeScale = scale;
        if(music.clip != null) {
            music.checkVolume();
        }
    }

    public void setSoundEffectVolume(int scale) {
        se.volumeScale = scale;
        if(se.clip != null) {
            se.checkVolume();
        }
    }

    public int getMusicVolume() {
        return music.volumeScale;
    }

    public int getSoundEffectVolume() {
        return se.volumeScale;
    }

    public void playAreaMusic(int area, int outside, int indoor, int dungeon) {
        stopBackgroundMusic();
        if(area == outside) {
            playBackgroundMusic(0);
        } else if(area == indoor) {
            playBackgroundMusic(18);
        } else if(area == dungeon) {
            playBackgroundMusic(19);
        }
    }

    // ============================================
    // COLLISION DETECTION METHODS
    // ============================================

    /**
     * Check ALL collision types for an entity
     * Coordinates: CollisionChecker + multiple entity arrays
     * Returns: true if ANY collision occurred
     */
    public boolean checkAllCollisions(Entity entity, boolean isPlayer) {
        entity.collisionOn = false;

        // Check tile collisions
        cChecker.checkTile(entity);

        // Check object collisions
        cChecker.checkObject(entity, isPlayer);

        // Check NPC collisions
        cChecker.checkEntity(entity, gp.npc);

        // Check monster collisions
        cChecker.checkEntity(entity, gp.monster);

        // Check interactive tile collisions (only for player)
        if(isPlayer) {
            cChecker.checkEntity(entity, gp.iTile);
        }

        return entity.collisionOn;
    }

    /**
     * Check collision and get the index of what was hit
     * Useful for interaction logic
     */
    public CollisionResult checkCollisionWithIndex(Entity entity, boolean isPlayer) {
        entity.collisionOn = false;

        CollisionResult result = new CollisionResult();

        // Check tile collision
        cChecker.checkTile(entity);
        result.tileCollision = entity.collisionOn;

        // Check object collision
        result.objectIndex = cChecker.checkObject(entity, isPlayer);

        // Check NPC collision
        result.npcIndex = cChecker.checkEntity(entity, gp.npc);

        // Check monster collision
        result.monsterIndex = cChecker.checkEntity(entity, gp.monster);

        // Check if player is touching interactive tile
        if(isPlayer) {
            result.interactiveTileIndex = cChecker.checkEntity(entity, gp.iTile);
        }

        return result;
    }

    /**
     * Check if entity can move in its current direction
     * Simple true/false check
     */
    public boolean canEntityMove(Entity entity) {
        entity.collisionOn = false;

        cChecker.checkTile(entity);
        cChecker.checkObject(entity, false);
        cChecker.checkEntity(entity, gp.npc);
        cChecker.checkEntity(entity, gp.monster);

        return !entity.collisionOn;
    }

    /**
     * Check player-specific collisions including events
     * Coordinates: CollisionChecker + EventHandler
     */
    public void checkPlayerCollisions() {
        // Standard collision checks
        checkAllCollisions(gp.player, true);

        // Check for world events (healing pools, teleports, etc.)
        eHandler.checkEvent();

        // Check if player contacted any NPCs or monsters
        int npcIndex = cChecker.checkEntity(gp.player, gp.npc);
        gp.player.interactNPC(npcIndex);

        int monsterIndex = cChecker.checkEntity(gp.player, gp.monster);
        gp.player.contactMonster(monsterIndex);

        int iTileIndex = cChecker.checkEntity(gp.player, gp.iTile);
        gp.player.damageInteractiveTile(iTileIndex);

        // Check object pickup
        int objIndex = cChecker.checkObject(gp.player, true);
        gp.player.pickUpObject(objIndex);
    }

    /**
     * Check monster collision with player
     * Returns: true if monster touched player
     */
    public boolean monsterContactedPlayer(Entity monster) {
        return cChecker.checkPlayer(monster);
    }

    /**
     * Simplified object interaction check
     * Returns: index of object player is touching, or 999 if none
     */
    public int checkObjectInteraction(Entity entity) {
        return cChecker.checkObject(entity, true);
    }

    /**
     * Check if specific entity types can see the player
     * Used for monster AI detection
     */
    public boolean canSeePlayer(Entity entity, int distance) {
        int xDistance = Math.abs(entity.worldX - gp.player.worldX);
        int yDistance = Math.abs(entity.worldY - gp.player.worldY);
        int totalDistance = Math.max(xDistance, yDistance);

        return totalDistance < distance * gp.tileSize;
    }

    // ============================================
    // HELPER METHODS (Combining Systems)
    // ============================================

    /**
     * Play collision sound effect
     * Convenient method combining collision detection with audio
     */
    public void playCollisionSound(Entity entity) {
        if(entity.collisionOn) {
            playSoundEffect(15); // blocked sound
        }
    }

    // ============================================
    // INNER CLASS - Collision Result
    // ============================================

    /**
     * Data structure to hold collision detection results
     */
    public static class CollisionResult {
        public boolean tileCollision = false;
        public int objectIndex = 999;
        public int npcIndex = 999;
        public int monsterIndex = 999;
        public int interactiveTileIndex = 999;

        public boolean hasAnyCollision() {
            return tileCollision || objectIndex != 999 || npcIndex != 999
                    || monsterIndex != 999 || interactiveTileIndex != 999;
        }

        public String getCollisionType() {
            if(tileCollision) return "tile";
            if(objectIndex != 999) return "object";
            if(npcIndex != 999) return "npc";
            if(monsterIndex != 999) return "monster";
            if(interactiveTileIndex != 999) return "interactive tile";
            return "none";
        }
    }
}