package org.example.main.net;

import org.example.main.GamePanel;
import org.example.main.input.PlayerInput;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import org.example.main.net.chat.ChatEvent;

/**
 * Client connector. Sends local input to the host and consumes authoritative world state.
 */
public class MultiplayerClient {
    private final GamePanel gp;
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean running = false;
    private int playerId = -1;
    private String assignedName = "";
    private final AtomicReference<NetworkMessages.WorldState> latestState = new AtomicReference<>();
    private final Queue<NetworkMessages.ChatMessage> chatQueue = new ConcurrentLinkedQueue<>();

    public MultiplayerClient(GamePanel gp, String host, int port) {
        this.gp = gp;
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Object obj = in.readObject();
            if (obj instanceof NetworkMessages.Handshake handshake) {
                this.playerId = handshake.playerId;
                this.assignedName = handshake.assignedName;
                running = true;
                Thread t = new Thread(this::receiveLoop, "mp-client-recv");
                t.setDaemon(true);
                t.start();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void stop() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {}
    }

    private void receiveLoop() {
        try {
            while (running) {
                Object obj = in.readObject();
                if (obj instanceof NetworkMessages.WorldState ws) {
                    latestState.set(ws);
                } else if (obj instanceof NetworkMessages.ChatMessage chat) {
                    chatQueue.add(chat);
                }
            }
        } catch (Exception e) {
            if (running) {
                e.printStackTrace();
            }
        } finally {
            stop();
        }
    }

    public void sendInput(PlayerInput.SimpleInputState input) {
        if (!running || out == null || playerId < 0) return;
        NetworkMessages.InputMessage msg = new NetworkMessages.InputMessage();
        msg.playerId = playerId;
        msg.input = input;
        try {
            synchronized (out) {
                out.reset();
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException ignored) {
        }
    }

    public NetworkMessages.WorldState consumeWorldState() {
        return latestState.getAndSet(null);
    }

    public void sendChat(ChatEvent event) {
        if (!running || out == null || event == null) return;
        NetworkMessages.ChatMessage msg = new NetworkMessages.ChatMessage();
        msg.senderId = playerId;
        msg.senderName = assignedName;
        msg.event = event;
        try {
            synchronized (out) {
                out.reset();
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException ignored) {}
    }

    public List<NetworkMessages.ChatMessage> pollChatMessages() {
        List<NetworkMessages.ChatMessage> outMsgs = new java.util.ArrayList<>();
        NetworkMessages.ChatMessage m;
        while ((m = chatQueue.poll()) != null) {
            outMsgs.add(m);
        }
        return outMsgs;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getAssignedName() {
        return assignedName;
    }
}
