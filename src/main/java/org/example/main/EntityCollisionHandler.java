package org.example.main;

import org.example.entity.Entity;

public class EntityCollisionHandler extends BaseCollisionHandler {
    @Override
    public void handle(Entity entity, CollisionChecker context) {
        context.checkEntity(entity, context.gp.npc);
        context.checkEntity(entity, context.gp.monster);
        context.checkEntity(entity, context.gp.iTile);
        super.handle(entity, context);
    }
}
