package org.example.memento;

import java.io.Serializable;
import org.example.data.DataStorage;

/**
 * Memento that carries an immutable snapshot of the game state.
 */
public class GameStateMemento implements Serializable {

    private final DataStorage snapshot;

    public GameStateMemento(DataStorage snapshot) {
        this.snapshot = snapshot;
    }

    public DataStorage getSnapshot() {
        return snapshot;
    }
}
