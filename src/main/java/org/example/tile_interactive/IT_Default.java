package org.example.tile_interactive;
import org.example.main.GamePanel;
public class IT_Default extends InteractiveTile {

    public IT_Default(GamePanel gp, int col, int row) {
        super(gp, col, row);
    }

    @Override
    protected void loadSprite() {
        down1 = setup("/tiles_interactive/default", gp.tileSize, gp.tileSize);
    }
}