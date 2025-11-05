package org.example.main;

import org.example.commands.AttackCommand;
import org.example.commands.CastSpellCommand;
import org.example.commands.Command;
import org.example.commands.MoveDownCommand;
import org.example.commands.MoveLeftCommand;
import org.example.commands.MoveRightCommand;
import org.example.commands.MoveUpCommand;
import java.awt.event.KeyAdapter;

public class KeyHandler extends KeyAdapter {

    private final Command attackCommand = new AttackCommand();
    private final Command castSpellCommand = new CastSpellCommand();
    private final Command moveUpCommand = new MoveUpCommand();
    private final Command moveDownCommand = new MoveDownCommand();
    private final Command moveLeftCommand = new MoveLeftCommand();
    private final Command moveRightCommand = new MoveRightCommand();
    private final Controls keyboard;
    private final Controls controller;
    private final GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean enterPressed, shotKeyPressed, spacePressed;
    private boolean prevPause, prevCharacter, prevMap, prevEscape;
    private boolean prevLeft, prevRight;
    private boolean prevUp, prevDown, prevEnter;
    private boolean prevShot, prevSpace;
    public boolean showDebugText = false;
    public boolean godModeOn = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
        this.keyboard = new KeyboardAdapter();
        this.controller = new ControllerAdapter();
        gp.addKeyListener((KeyboardAdapter) keyboard);
    }

    private boolean justPressed(boolean current, boolean previous) {
        return current && !previous;
    }

    public void update() {
        keyboard.update();
        controller.update();

        upPressed = keyboard.isUpPressed() || controller.isUpPressed();
        downPressed = keyboard.isDownPressed() || controller.isDownPressed();
        leftPressed = keyboard.isLeftPressed() || controller.isLeftPressed();
        rightPressed = keyboard.isRightPressed() || controller.isRightPressed();

        enterPressed = keyboard.isEnterPressed() || controller.isEnterPressed();
        shotKeyPressed = keyboard.isShotPressed() || controller.isShotPressed();
        spacePressed = keyboard.isSpacePressed() || controller.isSpacePressed();

        boolean pausePressed = keyboard.isPausePressed() || controller.isPausePressed();
        boolean characterPressed = keyboard.isCharacterPressed() || controller.isCharacterPressed();
        boolean mapPressed = keyboard.isMapPressed() || controller.isMapPressed();
        boolean escapePressed = keyboard.isEscapePressed() || controller.isEscapePressed();

        if (gp.gameState == gp.titleState) {
            handleTitleInput();
        } else if (gp.gameState == gp.playState) {
            handlePlayInput(pausePressed, characterPressed, mapPressed, escapePressed);
        } else if (gp.gameState == gp.pauseState) {
            handlePauseInput();
        } else if (gp.gameState == gp.dialogueState || gp.gameState == gp.cutsceneState) {
            handleDialogueInput();
        } else if (gp.gameState == gp.characterState) {
            handleCharacterInput();
        } else if (gp.gameState == gp.optionsState) {
            handleOptionsInput();
        } else if (gp.gameState == gp.gameOverState) {
            handleGameOverInput();
        } else if (gp.gameState == gp.tradeState) {
            handleTradeInput();
        } else if (gp.gameState == gp.mapState) {
            handleMapInput();
        }

        prevUp = upPressed;
        prevDown = downPressed;
        prevLeft = leftPressed;
        prevRight = rightPressed;
        prevEnter = enterPressed;
        prevShot = shotKeyPressed;
        prevSpace = spacePressed;
        prevPause = pausePressed;
        prevCharacter = characterPressed;
        prevMap = mapPressed;
        prevEscape = escapePressed;
    }

    private void handleTitleInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean enter = justPressed(enterPressed, prevEnter);

        if (gp.ui.titleScreenState == 0) {
            if (up) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 2;
                gp.playSE(9);
            }
            if (down) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) gp.ui.commandNum = 0;
                gp.playSE(9);
            }

            if (enter) {
                switch (gp.ui.commandNum) {
                    case 0 -> gp.ui.titleScreenState = 1;
                    case 1 -> {
                        gp.saveLoad.load();
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
                    }
                    case 2 -> System.exit(0);
                }
            }
        }

        else if (gp.ui.titleScreenState == 1) {
            int maxClasses = 3; // Fighter, Thief, Sorcerer, Back (0–3)

            if (up) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = maxClasses;
                gp.playSE(9);
            }
            if (down) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > maxClasses) gp.ui.commandNum = 0;
                gp.playSE(9);
            }

            if (enter) {
                switch (gp.ui.commandNum) {
                    case 0 -> System.out.println("Fighter chosen!");
                    case 1 -> System.out.println("Thief chosen!");
                    case 2 -> System.out.println("Sorcerer chosen!");
                    case 3 -> {
                        gp.ui.titleScreenState = 0;
                        return;
                    }
                }
                gp.gameState = gp.playState;
                gp.playMusic(0);
            }
        }
    }

    private void handlePlayInput(boolean pausePressed, boolean characterPressed, boolean mapPressed, boolean escapePressed) {
        if (justPressed(pausePressed, prevPause)) gp.gameState = gp.pauseState;
        if (justPressed(characterPressed, prevCharacter)) gp.gameState = gp.characterState;
        if (justPressed(mapPressed, prevMap)) gp.gameState = gp.mapState;
        if (justPressed(escapePressed, prevEscape)) gp.gameState = gp.optionsState;

        if (upPressed) {
            moveUpCommand.execute(gp.player);
        } else if (downPressed) {
            moveDownCommand.execute(gp.player);
        } else if (leftPressed) {
            moveLeftCommand.execute(gp.player);
        } else if (rightPressed) {
            moveRightCommand.execute(gp.player);
        }

        if (justPressed(enterPressed, prevEnter)) {
            attackCommand.execute(gp.player);
        }
        if (justPressed(shotKeyPressed, prevShot)) {
            castSpellCommand.execute(gp.player);
        }
    }

    private void handlePauseInput() {
        if (justPressed(keyboard.isPausePressed() || controller.isPausePressed(), prevPause))
            gp.gameState = gp.playState;
    }

    private void handleDialogueInput() {
        if (justPressed(keyboard.isEnterPressed() || controller.isEnterPressed(), prevEnter)) {
            gp.gameState = gp.playState;
        }
    }

    private void handleCharacterInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean left = justPressed(leftPressed, prevLeft);
        boolean right = justPressed(rightPressed, prevRight);
        boolean enter = justPressed(enterPressed, prevEnter);
        boolean character = justPressed(
                keyboard.isCharacterPressed() || controller.isCharacterPressed(),
                prevCharacter
        );

        if (up && gp.ui.playerSlotRow > 0) {
            gp.ui.playerSlotRow--;
            gp.playSE(9);
        }
        if (down && gp.ui.playerSlotRow < 3) {
            gp.ui.playerSlotRow++;
            gp.playSE(9);
        }
        if (left && gp.ui.playerSlotCol > 0) {
            gp.ui.playerSlotCol--;
            gp.playSE(9);
        }
        if (right && gp.ui.playerSlotCol < 4) {
            gp.ui.playerSlotCol++;
            gp.playSE(9);
        }
        if (enter) {
            gp.player.selectItem();
            gp.playSE(9);
        }
        if (character) gp.gameState = gp.playState;
    }

    private void handleOptionsInput() {
        boolean enter = justPressed(enterPressed, prevEnter);
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean escape = justPressed(
                keyboard.isEscapePressed() || controller.isEscapePressed(),
                prevEscape
        );

        int maxOptions = 5;

        if (up) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) gp.ui.commandNum = maxOptions;
            gp.playSE(9);
        }
        if (down) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > maxOptions) gp.ui.commandNum = 0;
            gp.playSE(9);
        }

        int selected = gp.ui.commandNum;

        if (enter && selected == 0) {
            gp.fullScreenOn = !gp.fullScreenOn;
            gp.config.saveConfig();
            gp.playSE(9);
        }

        if (enter && selected == 3) {
            gp.playSE(9);
        }

        if (enter && selected == 4) {
            gp.gameState = gp.titleState;
            gp.stopMusic();
            gp.playSE(9);
        }

        if ((enter && selected == 5) || escape) {
            gp.gameState = gp.playState;
            gp.playSE(9);
        }
    }

    private void handleGameOverInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean enter = justPressed(enterPressed, prevEnter);

        if (up) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) gp.ui.commandNum = 1;
            gp.playSE(9);
        }
        if (down) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 1) gp.ui.commandNum = 0;
            gp.playSE(9);
        }

        if (enter) {
            switch (gp.ui.commandNum) {
                case 0 -> {
                    gp.gameState = gp.playState;
                    gp.resetGame(false);
                    gp.playMusic(0);
                }
                case 1 -> {
                    gp.ui.titleScreenState = 0;
                    gp.gameState = gp.titleState;
                    gp.resetGame(true);
                }
            }
        }
    }

    private void handleTradeInput() {
        if (justPressed(keyboard.isEnterPressed() || controller.isEnterPressed(), prevEnter)) {
            gp.gameState = gp.playState;
        }
    }

    private void handleMapInput() {
        if (justPressed(keyboard.isEnterPressed() || controller.isEnterPressed(), prevEnter)) {
            gp.gameState = gp.playState;
        }
    }
}
