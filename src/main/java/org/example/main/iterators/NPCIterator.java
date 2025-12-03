package org.example.main.iterators;

import org.example.entity.Entity;
import org.example.main.GamePanel;

public class NPCIterator implements EntityIterator {

    private final GamePanel gp;
    private final Entity[][] npcs;
    private final int map;
    private int index = 0;
    private Entity nextNPC;

    public NPCIterator(GamePanel gp, Entity[][] npcs, int map) {
        this.gp = gp;
        this.npcs = npcs;
        this.map = map;
        advanceToNextValid();
    }

    @Override
    public boolean hasNext() {
        return nextNPC != null;
    }

    @Override
    public Entity next() {
        Entity current = nextNPC;
        advanceToNextValid();
        return current;
    }

    private void advanceToNextValid() {
        nextNPC = null;

        while (index < npcs[map].length) {
            Entity e = npcs[map][index++];

            if (e != null && e.inCamera()) {
                nextNPC = e;
                break;
            }
        }
    }
}
