package org.example.tile_interactive;

import org.example.main.GamePanel;

public final class IT_MetalPlate extends InteractiveTile {

    public static final String itName = "Metal Plate";

    public IT_MetalPlate(GamePanel gp, int col, int row) {
        super(gp, col, row);
    }

    @Override
    protected void loadSprite() {
        name = itName;
        down1 = setup("/tiles_interactive/metalplate", gp.tileSize, gp.tileSize);

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 0;
        solidArea.height = 0;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    protected void setupProperties() {
        destructible = false;
        life = 1;
    }

    @Override
    public boolean isCorrectItem(org.example.entity.Entity entity) {
        return false;
    }

    @Override
    public void playSE() {
    }

    @Override
    public InteractiveTile getDestroyedForm() {
        return null;
    }

    @Override
    public java.awt.Color getParticleColor() {
        return null;
    }

    @Override
    public int getParticleSize() {
        return 0;
    }

    @Override
    public int getParticleSpeed() {
        return 0;
    }

    @Override
    public int getParticleMaxLife() {
        return 0;
    }
}
