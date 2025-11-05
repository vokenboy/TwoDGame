package org.example.entity;

import java.util.Random;

public class RandomMovementStrategy implements MovementStrategy {
    private int interval;
    private int actionLockCounter = 0;

    public RandomMovementStrategy(int interval) {
        this.interval = interval;
    }

    @Override
    public void move(Entity entity) {
        actionLockCounter++;

        if (actionLockCounter > interval) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            if (i <= 25) entity.direction = "up";
            else if (i <= 50) entity.direction = "down";
            else if (i <= 75) entity.direction = "left";
            else entity.direction = "right";

            actionLockCounter = 0;
        }
    }

    public void reset() {
        actionLockCounter = 0;
    }
}
