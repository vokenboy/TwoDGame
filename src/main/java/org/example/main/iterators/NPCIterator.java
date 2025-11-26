package org.example.main.iterators;

import org.example.entity.Entity;

public class NPCIterator implements EntityIterator {

    private final Entity[][] npcs;
    private final int map;
    private int index = 0;

    public NPCIterator(Entity[][] npcs, int map) {
        this.npcs = npcs;
        this.map = map;
    }

    @Override
    public boolean hasNext() {
        while (index < npcs[map].length) {
            if (npcs[map][index] != null) return true;
            index++;
        }
        return false;
    }

    @Override
    public Entity next() {
        return npcs[map][index++];
    }
}
