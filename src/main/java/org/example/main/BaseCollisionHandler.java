package org.example.main;

import org.example.entity.Entity;

public abstract class BaseCollisionHandler implements CollisionHandler {

    private CollisionHandler next;

    public CollisionHandler setNext(CollisionHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public void handle(Entity entity, CollisionChecker context) {
        if (next != null) {
            next.handle(entity, context);
        }
    }
}
