package org.example.main.net.chat;

import java.util.List;
import org.example.main.GamePanel;
import org.example.main.net.MultiplayerClient;
import org.example.main.net.MultiplayerServer;
import org.example.main.net.NetworkMessages;
import org.example.main.net.chat.interpreter.ChatContext;
import org.example.main.net.chat.interpreter.ChatInterpreter;
import org.example.main.net.chat.ChatCommandEvent;

public class ChatManager {
    private final GamePanel gp;
    private MultiplayerServer server;
    private MultiplayerClient client;
    private final ChatServerMediator mediator = new ChatServerMediator();
    private final ChatInterpreter interpreter = new ChatInterpreter();

    public ChatManager(GamePanel gp) {
        this.gp = gp;
    }

    public void setServer(MultiplayerServer server) {
        this.server = server;
    }

    public void setClient(MultiplayerClient client) {
        this.client = client;
    }

    public void registerLocal(String id, ChatColleague colleague) {
        mediator.register(id, colleague);
    }

    public void unregisterLocal(String id) {
        mediator.unregister(id);
    }

    public void clearLocals() {
        mediator.getColleagues().clear();
    }

    public void sendText(String senderId, String senderName, String text) {
        ChatContext ctx = new ChatContext(text, senderId);
        interpreter.interpret(ctx);
        if (ctx.resultEvent == null) {
            return;
        }
        if (ctx.resultEvent.senderId == null) {
            ctx.resultEvent.senderId = senderId;
        }
        dispatch(senderId, senderName, ctx.resultEvent);
    }

    private void dispatch(String senderId, String senderName, ChatEvent event) {
        // Command-type chats stay local only (no network fan-out).
        if (event instanceof ChatCommandEvent) {
            mediator.distribute(senderId, event);
            return;
        }

        if (gp.isHost && server != null) {
            NetworkMessages.ChatMessage chat = new NetworkMessages.ChatMessage();
            chat.senderId = parseInt(senderId);
            chat.senderName = senderName;
            chat.event = event;
            mediator.distribute(senderId, event);
            server.broadcastChat(chat);
        } else if (client != null) {
            client.sendChat(event);
        } else {
            mediator.distribute(senderId, event);
        }
    }

    public void pollIncoming() {
        if (client == null) {
            return;
        }
        List<NetworkMessages.ChatMessage> incoming = client.pollChatMessages();
        for (NetworkMessages.ChatMessage chat : incoming) {
            if (chat != null && chat.event instanceof ChatEvent ce) {
                mediator.distribute(String.valueOf(chat.senderId), ce);
            }
        }
    }

    public void onServerChatReceived(NetworkMessages.ChatMessage chat) {
        if (chat != null && chat.event instanceof ChatEvent ce) {
            mediator.distribute(String.valueOf(chat.senderId), ce);
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
