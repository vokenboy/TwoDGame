package org.example.main;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * JInput-backed controller adapter that exposes gamepad state through the Controls interface.
 */
public class ControllerAdapter implements Controls {

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean pausePressed, characterPressed, mapPressed, escapePressed, achievementsPressed;
    private boolean enterPressed, interactPressed, shotPressed, altShotPressed, spacePressed;

    private Controller controller;
    private static final float DEADZONE = 0.3f;

    public ControllerAdapter() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (Controller c : controllers) {
            String name = c.getName().toLowerCase();
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                if (name.contains("xbox") || name.contains("360") || name.contains("xinput")) {
                    controller = c;
                    break;
                }
                if (controller == null && name.contains("wireless controller")) {
                    controller = c;
                }
            }
        }

        if (controller != null) {
            System.out.println("Controller connected: " + controller.getName() + " | Type: " + controller.getType());
        } else {
            System.out.println("No controller detected!");
        }
    }

    @Override
    public void update() {
        if (controller == null) return;

        controller.poll();
        Component[] components = controller.getComponents();

        resetInputs();

        for (Component c : components) {
            float value = c.getPollData();
            String id = c.getIdentifier().getName();

            switch (id) {
                case "x" -> {
                    if (value < -DEADZONE) leftPressed = true;
                    else if (value > DEADZONE) rightPressed = true;
                }
                case "y" -> {
                    if (value < -DEADZONE) upPressed = true;
                    else if (value > DEADZONE) downPressed = true;
                }
                case "pov" -> {
                    if (value == 0.25f) upPressed = true;
                    else if (value == 0.5f) rightPressed = true;
                    else if (value == 0.75f) downPressed = true;
                    else if (value == 1.0f) leftPressed = true;
                }
                case "rz" -> { // right trigger
                    if (value > DEADZONE) shotPressed = true;
                }
                case "z" -> { // left trigger
                    if (value > DEADZONE) {
                        altShotPressed = true;
                        spacePressed = true;
                    }
                }
            }

            switch (id) {
                case "0" -> {
                    enterPressed = value == 1.0f;
                    interactPressed = value == 1.0f;
                }
                case "1" -> shotPressed = value == 1.0f;
                case "2" -> altShotPressed = value == 1.0f;
                case "3" -> spacePressed = value == 1.0f;
                case "4" -> characterPressed = value == 1.0f;
                case "5" -> mapPressed = value == 1.0f;
                case "6" -> achievementsPressed = value == 1.0f;
                case "7" -> {
                    pausePressed = value == 1.0f;
                    escapePressed = value == 1.0f;
                }
            }
        }
    }

    private void resetInputs() {
        upPressed = downPressed = leftPressed = rightPressed = false;
        enterPressed = interactPressed = shotPressed = altShotPressed = spacePressed = false;
        pausePressed = characterPressed = mapPressed = escapePressed = achievementsPressed = false;
    }

    @Override public boolean isUpPressed() { return upPressed; }
    @Override public boolean isDownPressed() { return downPressed; }
    @Override public boolean isLeftPressed() { return leftPressed; }
    @Override public boolean isRightPressed() { return rightPressed; }
    @Override public boolean isEnterPressed() { return enterPressed; }
    @Override public boolean isInteractPressed() { return interactPressed; }
    @Override public boolean isShotPressed() { return shotPressed; }
    @Override public boolean isAltShotPressed() { return altShotPressed; }
    @Override public boolean isSpacePressed() { return spacePressed; }
    @Override public boolean isPausePressed() { return pausePressed; }
    @Override public boolean isCharacterPressed() { return characterPressed; }
    @Override public boolean isMapPressed() { return mapPressed; }
    @Override public boolean isEscapePressed() { return escapePressed; }
    @Override public boolean isAchievementsPressed() { return achievementsPressed; }
    @Override public boolean isChatPressed() { return false; }
}
