package org.example.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import org.example.data.SaveLoad;

/**
 * Caretaker that keeps a history of mementos for undo/checkpoint flows.
 */
public class GameStateCaretaker {

    private final SaveLoad saveLoad;
    private final Deque<GameStateMemento> history = new ArrayDeque<>();

    public GameStateCaretaker(SaveLoad saveLoad) {
        this.saveLoad = saveLoad;
    }

    public void checkpoint() {
        history.push(saveLoad.createMemento());
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        saveLoad.restore(history.pop());
        return true;
    }

    public void clear() {
        history.clear();
    }

    public int size() {
        return history.size();
    }
}
