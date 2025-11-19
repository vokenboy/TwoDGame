package org.example.tile_interactive;

import org.example.entity.Entity;
import org.example.main.GamePanel;

import java.awt.*;

public final class IT_DestructibleWall extends InteractiveTile {

    public IT_DestructibleWall(GamePanel gp, int col, int row) {
        super(gp, col, row);
    }

    @Override
    protected void loadSprite() {
        down1 = setup("/tiles_interactive/destructiblewall", gp.tileSize, gp.tileSize);
    }

    @Override
    protected void setupProperties() {
        destructible = true;
        life = 3;
    }

    @Override
    public boolean isCorrectItem(Entity entity) {
        return entity.currentWeapon.type == type_pickaxe;
    }

    @Override
    public void playSE() {
        gp.gameFacade.playSoundEffect(20);
    }

    @Override
    public InteractiveTile getDestroyedForm() {
        return null;
    }

    @Override
    public Color getParticleColor() {
        return new Color(65, 65, 65);
    }

    @Override
    public int getParticleSize() {
        return 6;
    }

    @Override
    public int getParticleSpeed() {
        return 1;
    }

    @Override
    public int getParticleMaxLife() {
        return 20;
    }
}
