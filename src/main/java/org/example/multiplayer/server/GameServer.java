package org.example.multiplayer.server;

import org.example.multiplayer.message.NetworkMessage;
import org.example.multiplayer.message.NetworkMessageCodec;
import org.example.multiplayer.model.PlayerSnapshot;
import org.example.multiplayer.model.WorldEntitySnapshot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple TCP server that relays player snapshots between any number of clients.
 */
public class GameServer implements Closeable {

    private final int port;
    private final ConcurrentMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, PlayerSnapshot> snapshots = new ConcurrentHashMap<>();
    private volatile List<WorldEntitySnapshot> latestEntities = List.of();
    private volatile String hostClientId;
    private final ExecutorService clientPool = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r, "mp-server-client");
        thread.setDaemon(true);
        return thread;
    });
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private ScheduledExecutorService broadcaster;
    private volatile boolean running;

    public GameServer(int port) {
        this.port = port;
    }

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }
        serverSocket = new ServerSocket(port);
        running = true;
        acceptThread = new Thread(this::acceptLoop, "mp-server-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        broadcaster = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "mp-server-broadcast");
            thread.setDaemon(true);
            return thread;
        });
        broadcaster.scheduleAtFixedRate(this::broadcastWorld, 100, 50, TimeUnit.MILLISECONDS);
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                clientPool.execute(new ClientHandler(socket));
            } catch (SocketException socketClosed) {
                break;
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void broadcastWorld() {
        if (clients.isEmpty()) {
            return;
        }
        NetworkMessage message = NetworkMessage.of("WORLD_STATE");
        message.with("count", snapshots.size());
        int index = 0;
        for (PlayerSnapshot snapshot : snapshots.values()) {
            message.with("id_" + index, snapshot.id());
            message.with("name_" + index, snapshot.name());
            message.with("worldX_" + index, snapshot.worldX());
            message.with("worldY_" + index, snapshot.worldY());
            message.with("direction_" + index, snapshot.direction());
            message.with("sprite_" + index, snapshot.spriteNum());
            message.with("moving_" + index, snapshot.moving());
            message.with("attacking_" + index, snapshot.attacking());
            message.with("map_" + index, snapshot.currentMap());
            message.with("timestamp_" + index, snapshot.timestamp());
            index++;
        }
        List<WorldEntitySnapshot> entities = latestEntities;
        message.with("entityCount", entities.size());
        for (int i = 0; i < entities.size(); i++) {
            WorldEntitySnapshot entity = entities.get(i);
            message.with("entityId_" + i, entity.id())
                    .with("entityClass_" + i, entity.className())
                    .with("entityName_" + i, entity.name())
                    .with("entityX_" + i, entity.worldX())
                    .with("entityY_" + i, entity.worldY())
                    .with("entityDir_" + i, entity.direction())
                    .with("entitySprite_" + i, entity.spriteNum())
                    .with("entityMap_" + i, entity.currentMap())
                    .with("entityAlive_" + i, entity.alive())
                    .with("entityLife_" + i, entity.life())
                    .with("entityMaxLife_" + i, entity.maxLife())
                    .with("entityCategory_" + i, entity.category())
                    .with("entityTime_" + i, entity.timestamp());
        }
        broadcast(message);
    }

    private void broadcast(NetworkMessage message) {
        for (ClientHandler handler : clients.values()) {
            handler.send(message);
        }
    }

    public synchronized void stop() {
        running = false;
        snapshots.clear();
        if (broadcaster != null) {
            broadcaster.shutdownNow();
        }
        if (acceptThread != null) {
            acceptThread.interrupt();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        for (ClientHandler handler : clients.values()) {
            handler.shutdown();
        }
        clients.clear();
        clientPool.shutdownNow();
    }

    @Override
    public void close() {
        stop();
    }

    private void removeClient(String playerId, String playerName) {
        if (playerId == null) {
            return;
        }
        clients.remove(playerId);
        snapshots.remove(playerId);
        if (playerId.equals(hostClientId)) {
            hostClientId = null;
            latestEntities = List.of();
        }
        NetworkMessage left = NetworkMessage.of("PLAYER_LEFT")
                .with("playerId", playerId)
                .with("name", playerName == null ? "Player" : playerName);
        broadcast(left);
    }

    private final class ClientHandler implements Runnable {
        private final Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private String playerId;
        private String playerName;
        private volatile boolean active = true;
        private boolean hostClient;

        ClientHandler(Socket socket) {
            this.socket = Objects.requireNonNull(socket, "socket");
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                while (active && !socket.isClosed()) {
                    NetworkMessage message = NetworkMessageCodec.read(in);
                    switch (message.type()) {
                        case "JOIN" -> handleJoin(message);
                        case "STATE" -> handleState(message);
                        case "WORLD_ENTITIES" -> handleWorldEntities(message);
                        case "PING" -> send(NetworkMessage.of("PONG"));
                        case "LEAVE" -> {
                            shutdown();
                            return;
                        }
                        default -> {
                        }
                    }
                }
            } catch (IOException e) {
                // Connection dropped.
            } finally {
                shutdown();
            }
        }

        private void handleJoin(NetworkMessage message) throws IOException {
            if (playerId != null) {
                return;
            }
            playerName = message.get("name");
            if (playerName == null || playerName.isBlank()) {
                playerName = "Player";
            }
            playerId = UUID.randomUUID().toString();
            clients.put(playerId, this);
            boolean wantsHost = message.getBoolean("host", false);
            synchronized (GameServer.this) {
                if (wantsHost && hostClientId == null) {
                    hostClientId = playerId;
                    hostClient = true;
                }
            }
            NetworkMessage welcome = NetworkMessage.of("WELCOME")
                    .with("playerId", playerId)
                    .with("serverTime", Instant.now().toEpochMilli());
            send(welcome);
            NetworkMessage joined = NetworkMessage.of("PLAYER_JOINED")
                    .with("playerId", playerId)
                    .with("name", playerName);
            broadcast(joined);
        }

        private void handleState(NetworkMessage message) {
            if (playerId == null) {
                return;
            }
            int worldX = message.getInt("worldX", 0);
            int worldY = message.getInt("worldY", 0);
            String direction = message.get("direction");
            if (direction == null || direction.isBlank()) {
                direction = "down";
            }
            int sprite = message.getInt("sprite", 1);
            boolean moving = message.getBoolean("moving", false);
            boolean attacking = message.getBoolean("attacking", false);
            int currentMap = message.getInt("map", 0);

            PlayerSnapshot snapshot = new PlayerSnapshot(
                    playerId,
                    playerName,
                    worldX,
                    worldY,
                    direction,
                    sprite,
                    moving,
                    attacking,
                    currentMap,
                    System.currentTimeMillis()
            );
            snapshots.put(playerId, snapshot);
        }

        private void handleWorldEntities(NetworkMessage message) {
            if (!hostClient || playerId == null || !playerId.equals(hostClientId)) {
                return;
            }
            int count = message.getInt("count", 0);
            List<WorldEntitySnapshot> parsed = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                String id = message.get("entityId_" + i);
                if (id == null) {
                    continue;
                }
                String className = message.get("entityClass_" + i);
                String name = message.get("entityName_" + i);
                int worldX = message.getInt("entityX_" + i, 0);
                int worldY = message.getInt("entityY_" + i, 0);
                String direction = message.get("entityDir_" + i);
                int sprite = message.getInt("entitySprite_" + i, 1);
                int map = message.getInt("entityMap_" + i, 0);
                boolean alive = message.getBoolean("entityAlive_" + i, true);
                int life = message.getInt("entityLife_" + i, 0);
                int maxLife = message.getInt("entityMaxLife_" + i, 0);
                String category = message.get("entityCategory_" + i);
                long timestamp = message.getLong("entityTime_" + i, System.currentTimeMillis());
                parsed.add(new WorldEntitySnapshot(
                        id,
                        className,
                        name,
                        worldX,
                        worldY,
                        direction,
                        sprite,
                        map,
                        alive,
                        life,
                        maxLife,
                        category,
                        timestamp));
            }
            latestEntities = parsed;
        }

        private synchronized void send(NetworkMessage message) {
            if (!active) {
                return;
            }
            try {
                NetworkMessageCodec.write(message, out);
            } catch (IOException e) {
                shutdown();
            }
        }

        private void shutdown() {
            if (!active) {
                return;
            }
            active = false;
            removeClient(playerId, playerName);
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
