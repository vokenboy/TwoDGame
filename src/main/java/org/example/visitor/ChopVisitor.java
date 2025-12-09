package org.example.visitor;

import org.example.entity.Player;
import org.example.tile_interactive.IT_Default;
import org.example.tile_interactive.IT_DestructibleWall;
import org.example.tile_interactive.IT_DryTree;
import org.example.tile_interactive.IT_MetalPlate;
import org.example.tile_interactive.IT_Trunk;
import org.example.tile_interactive.InteractiveTile;

public class ChopVisitor implements TileVisitor {
    private final Player player;

    public ChopVisitor(Player player) {
        this.player = player;
    }

    @Override
    public void visit(IT_DryTree tile) {
        damage(tile);
    }

    @Override
    public void visit(IT_DestructibleWall tile) {
    }

    @Override
    public void visit(IT_MetalPlate tile) {
    }

    @Override
    public void visit(IT_Trunk tile) {
    }

    @Override
    public void visit(IT_Default tile) {
    }

    private void damage(InteractiveTile tile) {
        if (tile.destructible && tile.isCorrectItem(player) && !tile.invincible) {
            tile.playSE();
            tile.life--;
            tile.invincible = true;
            player.generateParticle(tile, tile);
        }
    }
}
