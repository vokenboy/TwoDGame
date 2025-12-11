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
        chipWood(tile, 2, 2);
    }

    @Override
    public void visit(IT_DestructibleWall tile) {
        chipWood(tile, 1, 0);
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

    private void chipWood(InteractiveTile tile, int damage, int extraParticles) {
        if (damage <= 0) return;
        if (tile.destructible && tile.isCorrectItem(player) && !tile.invincible) {
            tile.playSE();
            tile.life -= Math.max(1, damage);
            tile.invincible = true;
            player.generateParticle(tile, tile);
            for (int i = 0; i < extraParticles; i++) {
                player.generateParticle(tile, tile);
            }
        }
    }
}
