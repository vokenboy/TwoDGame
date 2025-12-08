package org.example.main;

import org.example.entity.Entity;

public class PlayerCollisionHandler extends BaseCollisionHandler {
    @Override
    public void handle(Entity entity, CollisionChecker context) {
        boolean contactPlayer = context.checkPlayer(entity);
        if (entity.type == entity.type_monster && contactPlayer) {
            entity.damagePlayer(entity.attack);
        }
        super.handle(entity, context);
    }
}
