package org.example.memento;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

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

    public boolean restoreAtIndex(int index) {
        if (index < 0 || index >= history.size()) {
            return false;
        }
        GameStateMemento target = null;
        int i = 0;
        for (GameStateMemento memento : history) {
            if (i == index) {
                target = memento;
                break;
            }
            i++;
        }
        if (target == null) {
            return false;
        }
        saveLoad.restore(target);
        return true;
    }

    public List<GameStateMemento> getHistorySnapshot() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }

    public int size() {
        return history.size();
    }
}
