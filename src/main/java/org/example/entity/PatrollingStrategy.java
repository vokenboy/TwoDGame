package org.example.entity;

import java.util.List;

public class PatrollingStrategy implements MovementStrategy {

    private final List<int[]> patrolPoints;
    private int currentIndex = 0;

    public PatrollingStrategy(List<int[]> patrolPoints, int rate) {
        if (patrolPoints == null || patrolPoints.isEmpty()) {
            throw new IllegalArgumentException("Patrol points cannot be null or empty");
        }
        this.patrolPoints = patrolPoints;
    }

    @Override
    public void move(Entity entity) {
        if (patrolPoints.isEmpty()) return;

        int[] targetPoint = patrolPoints.get(currentIndex);
        int goalCol = targetPoint[0];
        int goalRow = targetPoint[1];

        entity.searchPath(goalCol, goalRow);

    }
}
