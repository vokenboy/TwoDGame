package org.example.main.input;

import org.example.main.KeyHandler;

/**
 * Bridges the existing KeyHandler to the PlayerInput abstraction.
 */
public class KeyHandlerPlayerInput implements PlayerInput {
    private final KeyHandler keyHandler;
    private final SimpleInputState state = new SimpleInputState();

    public KeyHandlerPlayerInput(KeyHandler keyHandler) {
        this.keyHandler = keyHandler;
    }

    @Override
    public void tick() {
        // KeyHandler.update() is driven by GamePanel, so here we only cache results.
        state.up = keyHandler.upPressed;
        state.down = keyHandler.downPressed;
        state.left = keyHandler.leftPressed;
        state.right = keyHandler.rightPressed;
        state.attack = keyHandler.enterPressed;
        state.interact = keyHandler.interactPressed || keyHandler.interactOnce;
        state.cast = keyHandler.shotKeyPressed;
        state.altCast = keyHandler.altShotKeyPressed;
        state.guard = keyHandler.spacePressed;
    }

    @Override public boolean up() { return state.up; }
    @Override public boolean down() { return state.down; }
    @Override public boolean left() { return state.left; }
    @Override public boolean right() { return state.right; }
    @Override public boolean attack() { return state.attack; }
    @Override public boolean interact() { return state.interact; }
    @Override public boolean cast() { return state.cast; }
    @Override public boolean altCast() { return state.altCast; }
    @Override public boolean guard() { return state.guard; }

    public SimpleInputState toSimpleState() {
        SimpleInputState copy = new SimpleInputState();
        copy.up = state.up;
        copy.down = state.down;
        copy.left = state.left;
        copy.right = state.right;
        copy.attack = state.attack;
        copy.interact = state.interact;
        copy.cast = state.cast;
        copy.altCast = state.altCast;
        copy.guard = state.guard;
        return copy;
    }
}
