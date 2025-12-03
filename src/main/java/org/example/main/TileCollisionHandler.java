package org.example.main;

import org.example.entity.Entity;

public class TileCollisionHandler extends BaseCollisionHandler {
    @Override
    public void handle(Entity entity, CollisionChecker context) {
        context.checkTile(entity);
        super.handle(entity, context);
    }
}
