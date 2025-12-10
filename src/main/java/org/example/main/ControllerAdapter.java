package org.example.main;

/**
 * Placeholder controller adapter that compiles without external libraries.
 * If you need real gamepad support, add JInput to the classpath and restore
 * polling logic; for now everything returns false.
 */
public class ControllerAdapter implements Controls {

    @Override
    public void update() {
        // no-op: controller support disabled (missing JInput dependency)
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
    public boolean isInteractPressed() {
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
