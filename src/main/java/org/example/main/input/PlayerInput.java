package org.example.main.input;

/**
 * Abstraction for controlling a player.
 * Implementations can be keyboard driven (local) or network driven (remote).
 */
public interface PlayerInput {
    boolean up();
    boolean down();
    boolean left();
    boolean right();
    boolean attack();
    boolean interact();
    boolean cast();
    boolean altCast();
    boolean guard();

    /**
     * Called once per frame to allow the input to refresh its cached state.
     */
    default void tick() {
        // no-op
    }

    /**
     * Lightweight serializable snapshot used over the network.
     */
    class SimpleInputState implements java.io.Serializable {
        public boolean up, down, left, right;
        public boolean attack, interact, cast, altCast, guard;
    }
}
