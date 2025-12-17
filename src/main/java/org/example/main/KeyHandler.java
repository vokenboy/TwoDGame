package org.example.main;

import java.awt.event.KeyAdapter;
import org.example.commands.AltCastSpellCommand;
import org.example.commands.AttackCommand;
import org.example.commands.CastSpellCommand;
import org.example.commands.Command;
import org.example.commands.MoveDownCommand;
import org.example.commands.MoveLeftCommand;
import org.example.commands.MoveRightCommand;
import org.example.commands.MoveUpCommand;

public class KeyHandler extends KeyAdapter {

    private static final int ACHIEVEMENT_SCROLL_STEP = 32;

    private final Command attackCommand = new AttackCommand();
    private final Command castSpellCommand = new CastSpellCommand();
    private final Command moveUpCommand = new MoveUpCommand();
    private final Command moveDownCommand = new MoveDownCommand();
    private final Command moveLeftCommand = new MoveLeftCommand();
    private final Command moveRightCommand = new MoveRightCommand();
    private final Command altCastSpellCommand = new AltCastSpellCommand();
    private final Controls keyboard;
    private final Controls controller;
    private final GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean enterPressed, shotKeyPressed, altShotKeyPressed, spacePressed, achievementsPressed;
    public boolean chatPressed;
    public boolean enterOnce;
    public boolean interactPressed, interactOnce;
    public int mpSelectionIndex = 0;
    public boolean mpHostMode = true;
    private boolean prevPause, prevCharacter, prevMap, prevEscape, prevAchievements;
    private boolean prevLeft, prevRight;
    private boolean prevUp, prevDown, prevEnter, prevInteract;
    private boolean prevShot, prevAltShot;
    private boolean prevChat;
    public boolean showDebugText = false;
    public boolean godModeOn = false;

    private final ChatInputSink chatSink = new ChatInputSink() {
        @Override
        public void onTypedChar(char c) {
            if (!gp.chatInputActive) return;
            if (!Character.isISOControl(c)) {
                gp.appendChatChar(c);
            }
        }

        @Override
        public void onBackspace() {
            if (!gp.chatInputActive) return;
            gp.backspaceChatInput();
        }

        @Override
        public void onSubmit() {
            if (!gp.chatInputActive) return;
            gp.submitChatInput();
            if (!gp.chatInputActive) {
                detachChatSink();
            }
        }

        @Override
        public void onCancel() {
            if (!gp.chatInputActive) return;
            gp.cancelChatInput();
            detachChatSink();
        }
    };

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
        interactPressed =
            keyboard.isInteractPressed() || controller.isInteractPressed();
        shotKeyPressed = keyboard.isShotPressed() || controller.isShotPressed();
        altShotKeyPressed =
            keyboard.isAltShotPressed() || controller.isAltShotPressed();
        spacePressed = keyboard.isSpacePressed() || controller.isSpacePressed();
        achievementsPressed =
            keyboard.isAchievementsPressed() ||
            controller.isAchievementsPressed();
        chatPressed = keyboard.isChatPressed() || controller.isChatPressed();
        enterOnce = justPressed(enterPressed, prevEnter);
        interactOnce = justPressed(interactPressed, prevInteract);

        // Handle chat toggle at the input layer so it works for host and client paths.
        boolean chatToggle =
            gp.gameState == gp.playState && justPressed(chatPressed, prevChat);
        if (chatToggle) {
            if (!gp.chatInputActive) {
                gp.openChatInput();
                attachChatSink();
            } else {
                gp.cancelChatInput();
                detachChatSink();
            }
        }

        // When chat is active, ignore gameplay controls (leave only the sink-driven enter/backspace/typing).
        if (gp.chatInputActive) {
            upPressed = downPressed = leftPressed = rightPressed = false;
            enterPressed = false;
            interactPressed = false;
            shotKeyPressed = false;
            altShotKeyPressed = false;
            spacePressed = false;
            achievementsPressed = false;
        }

        boolean pausePressed =
            keyboard.isPausePressed() || controller.isPausePressed();
        boolean characterPressed =
            keyboard.isCharacterPressed() || controller.isCharacterPressed();
        boolean mapPressed =
            keyboard.isMapPressed() || controller.isMapPressed();
        boolean escapePressed =
            keyboard.isEscapePressed() || controller.isEscapePressed();
        boolean achievementsToggle = justPressed(
            achievementsPressed,
            prevAchievements
        );

        if (gp.chatInputActive) {
            pausePressed = false;
            characterPressed = false;
            mapPressed = false;
            escapePressed = false;
            achievementsToggle = false;
        }

        if (gp.gameState == gp.titleState) {
            handleTitleInput();
        } else if (gp.gameState == gp.playState) {
            handlePlayInput(
                pausePressed,
                characterPressed,
                mapPressed,
                achievementsToggle,
                escapePressed
            );
        } else if (gp.gameState == gp.pauseState) {
            handlePauseInput();
        } else if (
            gp.gameState == gp.dialogueState || gp.gameState == gp.cutsceneState
        ) {
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
        } else if (gp.gameState == gp.achievementsState) {
            handleAchievementsInput(achievementsToggle, escapePressed);
        } else if (gp.gameState == gp.enchantState) {
            handleEnchantInput();
        }

        prevUp = upPressed;
        prevDown = downPressed;
        prevLeft = leftPressed;
        prevRight = rightPressed;
        prevEnter = enterPressed;
        prevInteract = interactPressed;
        prevShot = shotKeyPressed;
        prevAltShot = altShotKeyPressed;
        prevPause = pausePressed;
        prevCharacter = characterPressed;
        prevMap = mapPressed;
        prevEscape = escapePressed;
        prevAchievements = achievementsPressed;
        prevChat = chatPressed;
    }

    private void handleTitleInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean enter = enterOnce;

        if (gp.ui.titleScreenState == 0) {
            if (up) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 3;
                gp.gameFacade.playSoundEffect(9);
            }
            if (down) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 3) gp.ui.commandNum = 0;
                gp.gameFacade.playSoundEffect(9);
            }

            if (enter) {
                switch (gp.ui.commandNum) {
                    case 0 -> gp.ui.titleScreenState = 1;
                    case 1 -> {
                        gp.ui.titleScreenState = 2;
                        gp.ui.commandNum = 0;
                    }
                    case 2 -> {
                        gp.saveLoad.load();
                        gp.gameState = gp.playState;
                        gp.gameFacade.playSoundEffect(0);
                    }
                    case 3 -> System.exit(0);
                }
            }
        } else if (gp.ui.titleScreenState == 1) {
            int maxClasses = 3; // Fighter, Thief, Sorcerer, Back (0-3)

            if (up) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = maxClasses;
                gp.gameFacade.playSoundEffect(9);
            }
            if (down) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > maxClasses) gp.ui.commandNum = 0;
                gp.gameFacade.playSoundEffect(9);
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
                gp.gameFacade.playSoundEffect(0);
            }
        } else if (gp.ui.titleScreenState == 2) {
            if (up) {
                mpSelectionIndex--;
                if (mpSelectionIndex < 0) mpSelectionIndex = 3;
                gp.gameFacade.playSoundEffect(9);
            }
            if (down) {
                mpSelectionIndex++;
                if (mpSelectionIndex > 3) mpSelectionIndex = 0;
                gp.gameFacade.playSoundEffect(9);
            }

            if (enter) {
                switch (mpSelectionIndex) {
                    case 0 -> {
                        mpHostMode = !mpHostMode;
                        gp.gameFacade.playSoundEffect(9);
                    }
                    case 1 -> {
                        // Placeholder: IP editing can be wired to UI later.
                        gp.hostAddress = "127.0.0.1";
                        gp.gameFacade.playSoundEffect(9);
                    }
                    case 2 -> {
                        gp.gameFacade.playSoundEffect(0);
                        if (mpHostMode) {
                            gp.isHost = true;
                            gp.isClient = false;
                            gp.startHosting();
                            gp.gameState = gp.playState;
                        } else {
                            gp.isHost = false;
                            gp.isClient = true;
                            if (gp.joinHost(gp.hostAddress)) {
                                gp.gameState = gp.playState;
                            } else {
                                gp.ui.addMessage("Failed to join host");
                                gp.ui.titleScreenState = 0;
                            }
                        }
                    }
                    case 3 -> {
                        gp.ui.titleScreenState = 0;
                        gp.ui.commandNum = 0;
                    }
                }
            }
        }
    }

    private void handlePlayInput(
        boolean pausePressed,
        boolean characterPressed,
        boolean mapPressed,
        boolean achievementsToggle,
        boolean escapePressed
    ) {
        if (justPressed(pausePressed, prevPause)) gp.gameState = gp.pauseState;
        if (justPressed(characterPressed, prevCharacter)) gp.gameState =
            gp.characterState;
        if (justPressed(mapPressed, prevMap)) gp.gameState = gp.mapState;
        if (achievementsToggle) {
            gp.ui.resetAchievementsScroll();
            gp.gameState = gp.achievementsState;
        }
        if (justPressed(escapePressed, prevEscape)) gp.gameState =
            gp.optionsState;

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
        if (justPressed(altShotKeyPressed, prevAltShot)) {
            altCastSpellCommand.execute(gp.player);
        }
    }

    private void handlePauseInput() {
        if (
            justPressed(
                keyboard.isPausePressed() || controller.isPausePressed(),
                prevPause
            )
        ) gp.gameState = gp.playState;
    }

    private void handleDialogueInput() {
        if (enterOnce) {
            gp.gameState = gp.playState;
        }
    }

    private void handleCharacterInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean left = justPressed(leftPressed, prevLeft);
        boolean right = justPressed(rightPressed, prevRight);
        boolean enter = interactOnce;
        boolean character = justPressed(
            keyboard.isCharacterPressed() || controller.isCharacterPressed(),
            prevCharacter
        );

        if (up && gp.ui.playerSlotRow > 0) {
            gp.ui.playerSlotRow--;
            gp.gameFacade.playSoundEffect(9);
        }
        if (down && gp.ui.playerSlotRow < 3) {
            gp.ui.playerSlotRow++;
            gp.gameFacade.playSoundEffect(9);
        }
        if (left && gp.ui.playerSlotCol > 0) {
            gp.ui.playerSlotCol--;
            gp.gameFacade.playSoundEffect(9);
        }
        if (right && gp.ui.playerSlotCol < 4) {
            gp.ui.playerSlotCol++;
            gp.gameFacade.playSoundEffect(9);
        }
        if (enter) {
            gp.player.selectItem();
            gp.gameFacade.playSoundEffect(9);
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
            gp.gameFacade.playSoundEffect(9);
        }
        if (down) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > maxOptions) gp.ui.commandNum = 0;
            gp.gameFacade.playSoundEffect(9);
        }

        int selected = gp.ui.commandNum;

        if (enter && selected == 0) {
            gp.fullScreenOn = !gp.fullScreenOn;
            gp.config.saveConfig();
            gp.gameFacade.playSoundEffect(9);
        }

        if (enter && selected == 3) {
            gp.gameFacade.playSoundEffect(9);
        }

        if (enter && selected == 4) {
            gp.gameState = gp.titleState;
            gp.gameFacade.stopBackgroundMusic();
            gp.gameFacade.playSoundEffect(9);
        }

        if ((enter && selected == 5) || escape) {
            gp.gameState = gp.playState;
            gp.gameFacade.playSoundEffect(9);
        }
    }

    private void handleGameOverInput() {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean enter = justPressed(enterPressed, prevEnter);

        if (up) {
            gp.ui.commandNum--;
            if (gp.ui.commandNum < 0) gp.ui.commandNum = 1;
            gp.gameFacade.playSoundEffect(9);
        }
        if (down) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > 1) gp.ui.commandNum = 0;
            gp.gameFacade.playSoundEffect(9);
        }

        if (enter) {
            switch (gp.ui.commandNum) {
                case 0 -> {
                    gp.gameState = gp.playState;
                    // Try to undo to the last checkpoint; fall back to a soft reset if none.
                    boolean restored = gp.caretaker.undo();
                    if (!restored) {
                        gp.resetGame(false);
                    }
                    gp.gameFacade.playBackgroundMusic(0);
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
        // Close trade with interact or escape
        boolean escape = justPressed(
            keyboard.isEscapePressed() || controller.isEscapePressed(),
            prevEscape
        );
        if (interactOnce || escape) {
            gp.gameState = gp.playState;
        }
    }

    private void handleMapInput() {
        // Toggle map only with M
        if (
            justPressed(
                keyboard.isMapPressed() || controller.isMapPressed(),
                prevMap
            )
        ) {
            gp.gameState = gp.playState;
        }
    }

    private void handleAchievementsInput(
        boolean achievementsToggle,
        boolean escapePressed
    ) {
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);

        if (up) gp.ui.scrollAchievements(ACHIEVEMENT_SCROLL_STEP);
        if (down) gp.ui.scrollAchievements(-ACHIEVEMENT_SCROLL_STEP);

        if (achievementsToggle || justPressed(escapePressed, prevEscape)) {
            gp.gameState = gp.playState;
        }
    }

    private void handleEnchantInput() {
        // Reuse inventory-like navigation: arrows to move, E to confirm, Esc to back
        boolean up = justPressed(upPressed, prevUp);
        boolean down = justPressed(downPressed, prevDown);
        boolean left = justPressed(leftPressed, prevLeft);
        boolean right = justPressed(rightPressed, prevRight);
        boolean confirm = interactOnce;
        boolean escape = justPressed(
            keyboard.isEscapePressed() || controller.isEscapePressed(),
            prevEscape
        );

        // Delegate to UI to update enchant cursor/selection, similar to inventory navigation
        if (up) gp.ui.moveEnchantCursor(-1, 0);
        if (down) gp.ui.moveEnchantCursor(1, 0);
        if (left) gp.ui.moveEnchantCursor(0, -1);
        if (right) gp.ui.moveEnchantCursor(0, 1);

        if (confirm) gp.ui.confirmEnchantSelection();
        if (escape) gp.gameState = gp.playState;
    }

    private void attachChatSink() {
        if (keyboard instanceof KeyboardAdapter kb) {
            kb.setChatInputSink(chatSink);
        }
    }

    private void detachChatSink() {
        if (keyboard instanceof KeyboardAdapter kb) {
            kb.setChatInputSink(null);
        }
    }
}
