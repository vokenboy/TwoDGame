package org.example.main;

public interface Controls {
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isLeftPressed();
    boolean isRightPressed();
    boolean isEnterPressed();
    boolean isInteractPressed();
    boolean isShotPressed();
    boolean isAltShotPressed();
    boolean isSpacePressed();
    boolean isPausePressed();
    boolean isCharacterPressed();
    boolean isMapPressed();
    boolean isEscapePressed();
    boolean isAchievementsPressed();
    boolean isChatPressed();

    void update();
}
