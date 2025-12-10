package org.example.memento;

import java.util.ArrayDeque;
import java.util.Deque;

import org.example.data.SaveLoad;

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
