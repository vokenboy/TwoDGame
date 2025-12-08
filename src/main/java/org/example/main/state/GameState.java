package org.example.main.state;

import org.example.main.GamePanel;

import java.awt.Graphics2D;

public interface GameState {
    default void enter(GamePanel gp) {}

    void update(GamePanel gp);

    void draw(GamePanel gp, Graphics2D g2);
}
