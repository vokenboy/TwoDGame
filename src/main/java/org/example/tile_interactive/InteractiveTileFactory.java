package org.example.tile_interactive;


import org.example.main.GamePanel;

public class InteractiveTileFactory {

    private final GamePanel gp;

    public InteractiveTileFactory(GamePanel gp) {
        this.gp = gp;
    }

    public InteractiveTile createTile(String type, int col, int row) {
        switch(type) {
            case "Trunk": return new IT_Trunk(gp, col, row);
            case "MetalPlate": return new IT_MetalPlate(gp, col, row);
            case "DryTree": return new IT_DryTree(gp, col, row);
            case "DestructibleWall": return new IT_DestructibleWall(gp, col, row);
            default: return new IT_Default(gp, col, row);
        }
    }
}

