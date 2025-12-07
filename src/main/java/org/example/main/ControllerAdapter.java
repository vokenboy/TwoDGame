package org.example.main;

/**
 * Placeholder implementation that keeps the Controls abstraction intact even when
 * external controller libraries (e.g., JInput) are unavailable.
 */
public class ControllerAdapter implements Controls {

    @Override
    public void update() {
        // No controller integration available in this environment.
    }

    @Override
    public boolean isUpPressed() {
        return false;
    }

    @Override
    public boolean isDownPressed() {
        return false;
    }

    @Override
    public boolean isLeftPressed() {
        return false;
    }

    @Override
    public boolean isRightPressed() {
        return false;
    }

    @Override
    public boolean isEnterPressed() {
        return false;
    }

    @Override
    public boolean isShotPressed() {
        return false;
    }

    @Override
    public boolean isAltShotPressed() {
        return false;
    }

    @Override
    public boolean isSpacePressed() {
        return false;
    }

    @Override
    public boolean isPausePressed() {
        return false;
    }

    @Override
    public boolean isCharacterPressed() {
        return false;
    }

    @Override
    public boolean isMapPressed() {
        return false;
    }

    @Override
    public boolean isEscapePressed() {
        return false;
    }
}

