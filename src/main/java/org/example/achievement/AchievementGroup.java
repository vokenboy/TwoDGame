package org.example.achievement;

public class AchievementGroup extends Achievement {

    public AchievementGroup(String name, String description) {
        super(name, description, 0);
    }

    @Override
    public void add(Achievement achievement) {
        children.add(achievement);
    }

    @Override
    public void remove(Achievement achievement) {
        children.remove(achievement);
    }

    @Override
    public void checkProgress() {
        boolean allAchieved = true;
        for (Achievement child : children) {
            child.checkProgress();
            if (!child.isAchieved()) {
                allAchieved = false;
            }
        }
        this.achieved = allAchieved;
    }

    @Override
    public boolean isAchieved() {
        checkProgress();
        return achieved;
    }
}
