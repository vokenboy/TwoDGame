package org.example.main.iterators;

import org.example.entity.Entity;
import org.example.main.GamePanel;

public class MonsterIterator implements EntityIterator {

    private final GamePanel gp;
    private final Entity[][] monsters;
    private final int map;
    private int index = 0;
    private Entity nextMonster;

    public MonsterIterator(GamePanel gp, Entity[][] monsters, int map) {
        this.gp = gp;
        this.monsters = monsters;
        this.map = map;
        advanceToNextValid();
    }

    @Override
    public boolean hasNext() {
        return nextMonster != null;
    }

    @Override
    public Entity next() {
        Entity current = nextMonster;
        advanceToNextValid();
        return current;
    }

    private void advanceToNextValid() {
        nextMonster = null;

        while (index < monsters[map].length) {
            Entity e = monsters[map][index++];
            if (e != null && e.alive && !e.dying && e.inCamera()) {
                nextMonster = e;
                break;
            }
        }
    }
}
