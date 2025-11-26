package org.example.main.state;
import org.example.main.GamePanel;

import java.awt.Graphics2D;
public class GameOverState implements GameState {

    @Override
    public void enter(GamePanel gp) {
        gp.gameFacade.stopBackgroundMusic();
        gp.gameFacade.playBackgroundMusic(14);
    }

    @Override
    public void update(GamePanel gp) {
        gp.keyH.update();
    }

    @Override
    public void draw(GamePanel gp, Graphics2D g2) {
        gp.ui.draw(g2);
    }
}
