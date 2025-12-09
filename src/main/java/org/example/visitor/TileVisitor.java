package org.example.visitor;

import org.example.tile_interactive.IT_Default;
import org.example.tile_interactive.IT_DestructibleWall;
import org.example.tile_interactive.IT_DryTree;
import org.example.tile_interactive.IT_MetalPlate;
import org.example.tile_interactive.IT_Trunk;

public interface TileVisitor {
    void visit(IT_DryTree tile);
    void visit(IT_DestructibleWall tile);
    void visit(IT_MetalPlate tile);
    void visit(IT_Trunk tile);
    void visit(IT_Default tile);
}
