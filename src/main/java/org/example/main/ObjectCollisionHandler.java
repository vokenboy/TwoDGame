package org.example.main;

import org.example.entity.Entity;

public class ObjectCollisionHandler extends BaseCollisionHandler {
    @Override
    public void handle(Entity entity, CollisionChecker context) {
        boolean isPlayer = entity.type == entity.type_player;
        context.checkObject(entity, isPlayer);
        super.handle(entity, context);
    }
}
