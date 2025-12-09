package org.example.main.iterators;

import org.example.entity.Entity;
import org.example.main.GamePanel;

public class ObjectIterator implements EntityIterator {

    private final GamePanel gp;
    private final Entity[][] objects;
    private final int map;
    private int index = 0;
    private Entity nextObject;

    public ObjectIterator(GamePanel gp, Entity[][] objects, int map) {
        this.gp = gp;
        this.objects = objects;
        this.map = map;
        advanceToNextValid();
    }

    @Override
    public boolean hasNext() {
        return nextObject != null;
    }

    @Override
    public Entity next() {
        Entity current = nextObject;
        advanceToNextValid();
        return current;
    }

    private void advanceToNextValid() {
        nextObject = null;

        while (index < objects[map].length) {
            Entity e = objects[map][index++];

            if (e != null && e.inCamera()) {
                nextObject = e;
                break;
            }
        }
    }
}
