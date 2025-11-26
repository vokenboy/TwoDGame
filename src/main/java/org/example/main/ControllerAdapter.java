package org.example.main;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerAdapter implements Controls {

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean pausePressed, characterPressed, mapPressed, escapePressed;
    private boolean enterPressed, shotPressed, altShotPressed, spacePressed, interactPressed;

    private Controller controller;
    private static final float DEADZONE = 0.3f;

    public ControllerAdapter() {
        Controller[] controllers = ControllerEnvironment
                .getDefaultEnvironment()
                .getControllers();

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
            }

            switch (id) {
                case "0" -> {
                    enterPressed = value == 1.0f;
                    interactPressed = value == 1.0f; // map primary button to interact as well
                }
                case "1" -> shotPressed = value == 1.0f;
                case "2" -> spacePressed = value == 1.0f;
                case "3" -> pausePressed = value == 1.0f;
                case "4" -> characterPressed = value == 1.0f;
                case "5" -> mapPressed = value == 1.0f;
                case "7" -> escapePressed = value == 1.0f;
            }
        }
    }

    private void resetInputs() {
        upPressed = downPressed = leftPressed = rightPressed = false;
        enterPressed = shotPressed = spacePressed = false;
        pausePressed = characterPressed = mapPressed = escapePressed = false;
    }

    @Override
    public boolean isUpPressed() { return upPressed; }
    @Override
    public boolean isDownPressed() { return downPressed; }
    @Override
    public boolean isLeftPressed() { return leftPressed; }
    @Override
    public boolean isRightPressed() { return rightPressed; }
    @Override
    public boolean isEnterPressed() { return enterPressed; }
    @Override
    public boolean isShotPressed() { return shotPressed; }
    @Override
    public boolean isAltShotPressed() { return altShotPressed; }
    @Override
    public boolean isInteractPressed() { return interactPressed; }
    @Override
    public boolean isSpacePressed() { return spacePressed; }
    @Override
    public boolean isPausePressed() { return pausePressed; }
    @Override
    public boolean isCharacterPressed() { return characterPressed; }
    @Override
    public boolean isMapPressed() { return mapPressed; }
    @Override
    public boolean isEscapePressed() { return escapePressed; }
}

