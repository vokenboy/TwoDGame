package org.example.main;

import org.example.entity.Entity;
import org.example.main.iterators.EntityIterator;
import org.example.main.iterators.MonsterIterator;
import org.example.main.iterators.NPCIterator;
import org.example.main.iterators.ObjectIterator;

public class EntityManager {

    private final GamePanel gp;

    public EntityManager(GamePanel gp) {
        this.gp = gp;
    }

    public EntityIterator getMonsterIterator() {
        return new MonsterIterator(gp.monster, gp.currentMap);
    }

    public EntityIterator getNPCIterator() {
        return new NPCIterator(gp.npc, gp.currentMap);
    }

    public EntityIterator getObjectIterator() {
        return new ObjectIterator(gp.obj, gp.currentMap);
    }

    public void removeMonster(Entity m) {
        Entity[] current = gp.monster[gp.currentMap];
        for (int i = 0; i < current.length; i++) {
            if (current[i] == m) {
                current[i] = null;
                return;
            }
        }
    }
}
