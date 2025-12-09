package org.example.main.iterators;

import org.example.entity.Entity;
import org.example.main.GamePanel;

public class MonsterIterator implements EntityIterator {

    private final Entity[][] monsters;
    private final int map;
    private int index = 0;
    private Entity nextMonster;

    public MonsterIterator(Entity[][] monsters, int map) {
        this.monsters = monsters;
        this.map = map;
        advanceToNextNonNull();
    }

    @Override
    public boolean hasNext() {
        return nextMonster != null;
    }

    @Override
    public Entity next() {
        Entity current = nextMonster;
        advanceToNextNonNull();
        return current;
    }

    private void advanceToNextNonNull() {
        nextMonster = null;

        while (index < monsters[map].length) {
            Entity e = monsters[map][index++];
            if (e != null) {
                nextMonster = e;
                break;
            }
        }
    }
}
