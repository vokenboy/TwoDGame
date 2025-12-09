package org.example.main;

import org.example.entity.Entity;
import org.example.main.iterators.*;

public class EntityManager {

    private final GamePanel gp;

    public EntityManager(GamePanel gp) {
        this.gp = gp;
    }

    public EntityIterator getMonsterIterator() {
        return new MonsterIterator(gp, gp.monster, gp.currentMap);
    }

    public EntityIterator getNPCIterator() {
        return new NPCIterator(gp, gp.npc, gp.currentMap);
    }

    public EntityIterator getObjectIterator() {
        return new ObjectIterator(gp, gp.obj, gp.currentMap);
    }

    public void removeMonster(Entity m) {
        for (int i = 0; i < gp.monster[gp.currentMap].length; i++) {
            if (gp.monster[gp.currentMap][i] == m) {
                gp.monster[gp.currentMap][i] = null;
                return;
            }
        }
    }
}
