package org.example.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardAdapter implements Controls, KeyListener {

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean pausePressed, characterPressed, mapPressed, escapePressed;

    private boolean enterPressed, shotPressed, altShotPressed, spacePressed;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_ENTER -> enterPressed = true;
            case KeyEvent.VK_F -> shotPressed = true;
            case KeyEvent.VK_G -> altShotPressed = true;
            case KeyEvent.VK_SPACE -> spacePressed = true;
            case KeyEvent.VK_P -> pausePressed = true;
            case KeyEvent.VK_C -> characterPressed = true;
            case KeyEvent.VK_M -> mapPressed = true;
            case KeyEvent.VK_ESCAPE -> escapePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_ENTER -> enterPressed = false;
            case KeyEvent.VK_F -> shotPressed = false;
            case KeyEvent.VK_G -> altShotPressed = false;
            case KeyEvent.VK_SPACE -> spacePressed = false;
            case KeyEvent.VK_P -> pausePressed = false;
            case KeyEvent.VK_C -> characterPressed = false;
            case KeyEvent.VK_M -> mapPressed = false;
            case KeyEvent.VK_ESCAPE -> escapePressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public boolean isUpPressed() { return upPressed; }
    @Override
    public boolean isDownPressed() { return downPressed; }
    @Override
    public boolean isLeftPressed() { return leftPressed; }
    @Override
    public boolean isRightPressed() { return rightPressed; }
    @Override
    public boolean isEnterPressed() { return enterPressed; }
    @Override
    public boolean isShotPressed() { return shotPressed; }

    @Override
    public boolean isAltShotPressed() {
        return altShotPressed;
    }

    @Override
    public boolean isSpacePressed() { return spacePressed; }
    @Override
    public boolean isPausePressed() { return pausePressed; }
    @Override
    public boolean isCharacterPressed() { return characterPressed; }
    @Override
    public boolean isMapPressed() { return mapPressed; }
    @Override
    public boolean isEscapePressed() { return escapePressed; }


    @Override
    public void update() {}

    public void resetKeys() {
        upPressed = downPressed = leftPressed = rightPressed = false;
        enterPressed = shotPressed = altShotPressed = spacePressed = false; // ✅ reset all
    }
}

