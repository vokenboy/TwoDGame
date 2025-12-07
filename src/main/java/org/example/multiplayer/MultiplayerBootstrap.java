package org.example.multiplayer;

import org.example.main.GamePanel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.concurrent.CompletableFuture;

public final class MultiplayerBootstrap {

    private MultiplayerBootstrap() {
    }

    public static boolean prompt(GamePanel gp) {
        String[] options = {"Single Player", "Host Game", "Join Game"};
        int choice = JOptionPane.showOptionDialog(
                gp,
                "Choose how you want to play",
                "Multiplayer",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) {
            return hostFlow(gp);
        } else if (choice == 2) {
            return joinFlow(gp);
        }
        return false;
    }

    private static boolean hostFlow(GamePanel gp) {
        String name = promptInput(gp, "Enter a display name", "Host");
        if (name == null) {
            return false;
        }
        String portInput = promptInput(gp, "Choose a port", "5050");
        if (portInput == null) {
            return false;
        }
        int port = parsePort(portInput);
        CompletableFuture<Void> task = gp.getMultiplayerManager().hostAndConnect(port, name);
        task.whenComplete((ignored, throwable) -> handleCompletion(gp, throwable));
        return true;
    }

    private static boolean joinFlow(GamePanel gp) {
        String name = promptInput(gp, "Enter a display name", "Guest");
        if (name == null) {
            return false;
        }
        String host = promptInput(gp, "Enter host address", "localhost");
        if (host == null) {
            return false;
        }
        String portInput = promptInput(gp, "Port", "5050");
        if (portInput == null) {
            return false;
        }
        int port = parsePort(portInput);
        CompletableFuture<Void> task = gp.getMultiplayerManager().join(host, port, name);
        task.whenComplete((ignored, throwable) -> handleCompletion(gp, throwable));
        return true;
    }

    private static String promptInput(GamePanel gp, String text, String defaultValue) {
        return JOptionPane.showInputDialog(gp, text, defaultValue);
    }

    private static int parsePort(String portInput) {
        try {
            return Integer.parseInt(portInput);
        } catch (NumberFormatException ex) {
            return 5050;
        }
    }

    private static void handleCompletion(GamePanel gp, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        final Throwable root = throwable.getCause() != null ? throwable.getCause() : throwable;
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                        gp,
                        "Failed to initialize multiplayer: " + root.getMessage(),
                        "Multiplayer",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }
}
