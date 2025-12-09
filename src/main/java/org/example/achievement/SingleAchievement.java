package org.example.achievement;

public class SingleAchievement extends Achievement {
    private int progress;
    private int required;

    public SingleAchievement(String name, String description, int points, int required) {
        super(name, description, points);
        this.required = required;
        this.progress = 0;
    }

    public void updateProgress(int value) {
        if (!achieved) {
            this.progress += value;
            checkProgress();
        }
    }

    public void setProgress(int value) {
        if (!achieved) {
            this.progress = value;
            checkProgress();
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getRequired() {
        return required;
    }

    @Override
    public void checkProgress() {
        if (progress >= required) {
            achieved = true;
        }
    }
}
