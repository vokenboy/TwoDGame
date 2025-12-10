package org.example.main.net.chat;

import org.example.main.GamePanel;

public class PlayerColleague implements ChatColleague {
    private final GamePanel gp;
    private final ChatManager manager;
    private String id;
    private String displayName;

    public PlayerColleague(GamePanel gp, ChatManager manager, String id, String displayName) {
        this.gp = gp;
        this.manager = manager;
        this.id = id;
        this.displayName = displayName;
    }

    public void setIdentity(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public void receiveChat(ChatEvent event) {
        String label = displayName;
        try {
            int pid = Integer.parseInt(event.senderId);
            var p = gp.getPlayerById(pid);
            if (p != null) {
                label = p.name;
            }
        } catch (Exception ignored) {}

        if (event instanceof TextChatEvent text) {
            gp.ui.addMessage("[" + label + "] " + text.message);
        } else if (event instanceof ChatCommandEvent cmd) {
            switch (cmd.command) {
                case HELP -> gp.ui.addMessage("/help /w <target> <msg> /rng [max]");
                case WHISPER -> gp.ui.addMessage("[whisper " + label + "] " + cmd.args.getOrDefault("message", ""));
                case RNG -> gp.ui.addMessage("[rng " + label + "] rolled " + cmd.args.getOrDefault("roll", "?") + " of " + cmd.args.getOrDefault("max", "?"));
                default -> gp.ui.addMessage("[cmd] " + label);
            }
        }
    }

    @Override
    public void sendChat(String text) {
        manager.sendText(id, gp.player.name, text);
    }
}
