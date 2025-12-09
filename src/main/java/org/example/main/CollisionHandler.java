package org.example.main;

import org.example.entity.Entity;

public interface CollisionHandler {
    void handle(Entity entity, CollisionChecker context);
}
