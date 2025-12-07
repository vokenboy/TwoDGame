package org.example.multiplayer;

import org.example.entity.Entity;
import org.example.entity.PlayerDummy;
import org.example.main.GamePanel;
import org.example.multiplayer.model.WorldEntitySnapshot;

import java.lang.reflect.Constructor;

/**
 * Visual-only representation of NPCs/monsters driven by the host.
 */
public class RemoteEntityMirror extends Entity {

    private String remoteId;
    private String className;
    private long lastUpdated;
    private int currentMap;
    private String category;

    public RemoteEntityMirror(GamePanel gp) {
        super(gp);
        sleep = true;
    }

    public void apply(WorldEntitySnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        remoteId = snapshot.id();
        if (className == null || !className.equals(snapshot.className())) {
            loadAppearance(snapshot.className());
            className = snapshot.className();
        }

        name = snapshot.name();
        worldX = snapshot.worldX();
        worldY = snapshot.worldY();
        direction = snapshot.direction() == null ? "down" : snapshot.direction();
        spriteNum = snapshot.spriteNum();
        currentMap = snapshot.currentMap();
        category = snapshot.category();
        alive = snapshot.alive();
        life = snapshot.life();
        maxLife = snapshot.maxLife();
        lastUpdated = snapshot.timestamp();
    }

    private void loadAppearance(String fqcn) {
        Entity template = instantiate(fqcn);
        if (template == null) {
            template = new PlayerDummy(getGp());
        }
        copySprites(template);
    }

    private Entity instantiate(String fqcn) {
        if (fqcn == null || fqcn.isBlank()) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(fqcn);
            Constructor<?> ctor = clazz.getConstructor(GamePanel.class);
            return (Entity) ctor.newInstance(getGp());
        } catch (Exception ex) {
            return null;
        }
    }

    private void copySprites(Entity source) {
        this.up1 = source.up1;
        this.up2 = source.up2;
        this.down1 = source.down1;
        this.down2 = source.down2;
        this.left1 = source.left1;
        this.left2 = source.left2;
        this.right1 = source.right1;
        this.right2 = source.right2;
        this.image = source.image;
        this.image2 = source.image2;
        this.image3 = source.image3;
    }

    @Override
    public void update() {
        // Remote mirrors are animated purely via incoming snapshots.
    }

    public boolean isStale(long now, long thresholdMillis) {
        return now - lastUpdated > thresholdMillis;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public String getCategory() {
        return category;
    }
}
