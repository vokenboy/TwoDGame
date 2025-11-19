package org.example.tile_interactive;

import org.example.entity.Entity;
import org.example.main.GamePanel;

import java.awt.*;

public abstract class InteractiveTile extends Entity {

    protected GamePanel gp;
    public boolean destructible = false;

    public InteractiveTile(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        initTemplate(col, row);
    }

    private final void initTemplate(int col, int row) {
        setDefaultState(col, row);
        loadSprite();
        setupProperties();
        setupParticles();
    }

    private void setDefaultState(int col, int row) {
        this.worldX = gp.tileSize * col;
        this.worldY = gp.tileSize * row;

        this.solidArea.x = 0;
        this.solidArea.y = 0;
        this.solidArea.width = gp.tileSize;
        this.solidArea.height = gp.tileSize;

        this.destructible = false;
        this.life = 1;
    }

    protected abstract void loadSprite();

    protected void setupProperties() {}

    protected void setupParticles() {}

    public boolean isCorrectItem(Entity entity) { return false; }

    public void playSE() {}

    public InteractiveTile getDestroyedForm() { return null; }


    @Override
    public void update() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 20) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            g2.drawImage(down1, screenX, screenY, null);
        }
    }
}
