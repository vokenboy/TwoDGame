package org.example.entity;

public class ChasePlayerStrategy implements MovementStrategy {

    private int chaseDistance;
    private int stopDistance;
    private int rate;
    private RandomMovementStrategy randomStrategy;

    public ChasePlayerStrategy(int chaseDistance, int stopDistance, int rate, int randomInterval) {
        this.chaseDistance = chaseDistance;
        this.stopDistance = stopDistance;
        this.rate = rate;
        this.randomStrategy = new RandomMovementStrategy(randomInterval);
    }

    @Override
    public void move(Entity entity) {
        if (entity.onPath) {
            entity.checkStopChasingOrNot(entity.gp.player, stopDistance, rate);
            entity.searchPath(entity.getGoalCol(entity.gp.player), entity.getGoalRow(entity.gp.player));
        } else {
            entity.checkStartChasingOrNot(entity.gp.player, chaseDistance, rate);
            randomStrategy.move(entity);
        }
    }
}
