package org.example.main.iterators;

import org.example.entity.Entity;

public class ObjectIterator implements EntityIterator {

    private final Entity[][] objects;
    private final int map;
    private int index = 0;

    public ObjectIterator(Entity[][] objects, int map) {
        this.objects = objects;
        this.map = map;
    }

    @Override
    public boolean hasNext() {
        while (index < objects[map].length) {
            if (objects[map][index] != null) return true;
            index++;
        }
        return false;
    }

    @Override
    public Entity next() {
        return objects[map][index++];
    }
}
