package org.example.achievement;

public abstract class Achievement {
    protected String name;
    protected String description;
    protected int points;
    protected boolean achieved;

    public Achievement(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.achieved = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPoints() {
        return points;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public abstract void checkProgress();

    public void add(Achievement achievement) {
        throw new UnsupportedOperationException("Cannot add to a leaf achievement.");
    }

    public void remove(Achievement achievement) {
        throw new UnsupportedOperationException("Cannot remove from a leaf achievement.");
    }
}
