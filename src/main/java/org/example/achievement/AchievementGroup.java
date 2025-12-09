package org.example.achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementGroup extends Achievement {
    private final List<Achievement> children;

    public AchievementGroup(String name, String description) {
        super(name, description, 0);
        this.children = new ArrayList<>();
    }

    @Override
    public void add(Achievement achievement) {
        children.add(achievement);
    }

    @Override
    public void remove(Achievement achievement) {
        children.remove(achievement);
    }

    public List<Achievement> getChildren() {
        return children;
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
