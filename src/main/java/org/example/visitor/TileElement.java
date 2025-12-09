package org.example.visitor;

public interface TileElement {
    void accept(TileVisitor visitor);
}
