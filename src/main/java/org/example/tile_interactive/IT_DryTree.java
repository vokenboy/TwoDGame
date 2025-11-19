package org.example.tile_interactive;

import org.example.entity.Entity;
import org.example.main.GamePanel;

import java.awt.*;

public final class IT_DryTree extends InteractiveTile {

    public IT_DryTree(GamePanel gp, int col, int row) {
        super(gp, col, row);
    }

    @Override
    protected void loadSprite() {
        down1 = setup("/tiles_interactive/drytree", gp.tileSize, gp.tileSize);
    }

    @Override
    protected void setupProperties() {
        destructible = true;
        life = 2;
    }

    @Override
    public boolean isCorrectItem(Entity entity) {
        return entity.currentWeapon.type == type_axe;
    }

    @Override
    public void playSE() {
        gp.gameFacade.playSoundEffect(11);
    }

    @Override
    public InteractiveTile getDestroyedForm() {
        return new IT_Trunk(gp, worldX / gp.tileSize, worldY / gp.tileSize);
    }

    @Override
    public Color getParticleColor() {
        return new Color(65, 50, 30);
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
