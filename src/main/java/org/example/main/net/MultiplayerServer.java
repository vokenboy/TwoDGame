package org.example.main.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.entity.Player;
import org.example.main.GamePanel;
import org.example.main.input.PlayerInput;
import org.example.main.input.RemotePlayerInput;

/**
 * Authoritative host-side server. The host runs the game simulation and applies
 * input coming from clients.
 */
public class MultiplayerServer {

    private final GamePanel gp;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final Map<Integer, ClientHandler> clients =
        new ConcurrentHashMap<>();
    private final Map<Integer, RemotePlayerInput> remoteInputs =
        new ConcurrentHashMap<>();
    private final AtomicInteger nextPlayerId = new AtomicInteger(1); // 0 is host
    private volatile boolean running = false;

    public MultiplayerServer(GamePanel gp, int port) {
        this.gp = gp;
        this.port = port;
    }

    public void start() {
        running = true;
        pool.execute(this::acceptLoop);
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
        pool.shutdownNow();
    }

    private void acceptLoop() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket socket = serverSocket.accept();
                int playerId = nextPlayerId.getAndIncrement();
                String name = "Player " + (playerId + 1);

                RemotePlayerInput input = new RemotePlayerInput();
                Player newPlayer = gp.addPlayer(input, name, playerId);
                remoteInputs.put(playerId, input);

                ClientHandler handler = new ClientHandler(
                    socket,
                    playerId,
                    name
                );
                clients.put(playerId, handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns and clears the latest inputs gathered from clients.
     */
    public Map<Integer, PlayerInput.SimpleInputState> drainInputs() {
        Map<Integer, PlayerInput.SimpleInputState> snapshot =
            new ConcurrentHashMap<>();
        for (Map.Entry<Integer, ClientHandler> entry : clients.entrySet()) {
            PlayerInput.SimpleInputState state = entry
                .getValue()
                .consumeInput();
            if (state != null) {
                snapshot.put(entry.getKey(), state);
            }
        }
        return snapshot;
    }

    public void broadcastChat(NetworkMessages.ChatMessage chat) {
        for (ClientHandler handler : clients.values()) {
            handler.send(chat);
        }
    }

    public void broadcastState(NetworkMessages.WorldState state) {
        for (ClientHandler handler : clients.values()) {
            handler.send(state);
        }
    }

    private class ClientHandler implements Runnable {

        private final Socket socket;
        private final int playerId;
        private final String assignedName;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private volatile PlayerInput.SimpleInputState latestInput;
        private volatile boolean ready = false;

        ClientHandler(Socket socket, int playerId, String assignedName) {
            this.socket = socket;
            this.playerId = playerId;
            this.assignedName = assignedName;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                NetworkMessages.Handshake handshake =
                    new NetworkMessages.Handshake();
                handshake.playerId = playerId;
                handshake.assignedName = assignedName;
                out.writeObject(handshake);
                out.flush();
                ready = true;

                while (running && !socket.isClosed()) {
                    Object obj = in.readObject();
                    if (
                        obj instanceof NetworkMessages.InputMessage inputMessage
                    ) {
                        latestInput = inputMessage.input;
                        RemotePlayerInput remote = remoteInputs.get(playerId);
                        if (remote != null) {
                            remote.applyState(inputMessage.input);
                        }
                    } else if (obj instanceof NetworkMessages.ChatMessage chat) {
                        if (gp.chatManager != null) {
                            gp.chatManager.onServerChatReceived(chat);
                        }
                        broadcastChat(chat);
                    }
                }
            } catch (EOFException eof) {
                // client disconnected
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                clients.remove(playerId);
                remoteInputs.remove(playerId);
                gp.removePlayer(playerId);
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        void send(NetworkMessages.WorldState state) {
            if (!ready) {
                return;
            }
            try {
                if (out != null) {
                    synchronized (out) {
                        out.reset();
                        out.writeObject(state);
                        out.flush();
                    }
                }
            } catch (IOException ignored) {}
        }

        PlayerInput.SimpleInputState consumeInput() {
            PlayerInput.SimpleInputState tmp = latestInput;
            latestInput = null;
            return tmp;
        }

        void send(NetworkMessages.ChatMessage chat) {
            if (!ready) {
                return;
            }
            try {
                if (out != null) {
                    synchronized (out) {
                        out.reset();
                        out.writeObject(chat);
                        out.flush();
                    }
                }
            } catch (IOException ignored) {}
        }
    }
}
