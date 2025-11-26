package org.example.main.iterators;

import org.example.entity.Entity;

public class MonsterIterator implements EntityIterator {

    private final Entity[][] monsters;
    private final int map;
    private int index = 0;

    public MonsterIterator(Entity[][] monsters, int map) {
        this.monsters = monsters;
        this.map = map;
    }

    @Override
    public boolean hasNext() {
        while (index < monsters[map].length) {
            if (monsters[map][index] != null) return true;
            index++;
        }
        return false;
    }

    @Override
    public Entity next() {
        return monsters[map][index++];
    }
}
