package org.example.main.input;

/**
 * Network driven player input. The server updates the state via applyState().
 */
public class RemotePlayerInput implements PlayerInput {
    private final SimpleInputState state = new SimpleInputState();

    public void applyState(SimpleInputState newState) {
        if (newState == null) return;
        state.up = newState.up;
        state.down = newState.down;
        state.left = newState.left;
        state.right = newState.right;
        state.attack = newState.attack;
        state.interact = newState.interact;
        state.cast = newState.cast;
        state.altCast = newState.altCast;
        state.guard = newState.guard;
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
}
