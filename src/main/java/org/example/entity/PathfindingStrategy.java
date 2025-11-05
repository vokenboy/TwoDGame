package org.example.entity;

public class PathfindingStrategy implements MovementStrategy {

    @Override
    public void move(Entity entity) {
        entity.searchPath(entity.getGoalCol(entity.gp.player), entity.getGoalRow(entity.gp.player));
    }
}
