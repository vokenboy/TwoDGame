package org.example.multiplayer;

import org.example.entity.PlayerDummy;
import org.example.main.GamePanel;
import org.example.multiplayer.model.PlayerSnapshot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Lightweight entity used to render remote clients.
 */
public class RemotePlayer extends PlayerDummy {

    private String playerId;
    private String playerName = "Player";
    private boolean moving;
    private boolean attacking;
    private long lastUpdated;
    private int currentMap;

    public RemotePlayer(GamePanel gp) {
        super(gp);
        type = type_player;
    }

    public void apply(PlayerSnapshot snapshot) {
        this.playerId = snapshot.id();
        this.playerName = snapshot.name() == null ? "Player" : snapshot.name();
        this.worldX = snapshot.worldX();
        this.worldY = snapshot.worldY();
        this.direction = snapshot.direction() == null ? "down" : snapshot.direction();
        this.spriteNum = snapshot.spriteNum();
        this.moving = snapshot.moving();
        this.attacking = snapshot.attacking();
        this.currentMap = snapshot.currentMap();
        this.lastUpdated = snapshot.timestamp();
    }

    public boolean isStale(long now, long thresholdMillis) {
        return now - lastUpdated > thresholdMillis;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        if (playerName == null) {
            return;
        }
        int screenX = getScreenX();
        int screenY = getScreenY();
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.BLACK);
        g2.drawString(playerName, screenX - 19, screenY - 18);
        g2.setColor(Color.WHITE);
        g2.drawString(playerName, screenX - 20, screenY - 19);
    }
}
