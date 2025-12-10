package org.example.main.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.example.main.input.PlayerInput;

/**
 * Simple serializable message types used between host and clients.
 */
public final class NetworkMessages {

    private NetworkMessages() {}

    public static class Handshake implements Serializable {

        public int playerId;
        public String assignedName;
    }

    public static class InputMessage implements Serializable {

        public int playerId;
        public PlayerInput.SimpleInputState input;
    }

    public static class ChatMessage implements Serializable {

        public int senderId;
        public String senderName;
        public Object event; // ChatEvent (kept as Object to avoid tight coupling)
    }

    public static class WorldState implements Serializable {

        public long tick;
        public List<PlayerState> players = new ArrayList<>();
        public List<EntityState> npcs = new ArrayList<>();
        public List<EntityState> monsters = new ArrayList<>();
        public List<EntityState> projectiles = new ArrayList<>();
        public List<EntityState> objects = new ArrayList<>();
    }

    public static class PlayerState implements Serializable {

        public int playerId;
        public String name;
        public int worldX;
        public int worldY;
        public String direction;
        public int life;
        public int maxLife;
        public boolean attacking;
        public boolean guarding;
        public int spriteNum;
        public boolean alive;
    }

    /**
     * Shared snapshot for monsters, projectiles, and objects.
     */
    public static class EntityState implements Serializable {

        public int mapIndex;
        public int slotIndex;
        public String name;
        public int worldX;
        public int worldY;
        public String direction;
        public int life;
        public int maxLife;
        public boolean alive;
        public boolean dying;
        public int spriteNum;
        public boolean hpBarOn;
        public int hpBarCounter;
    }
}
