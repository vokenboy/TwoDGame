package org.example.multiplayer.client;

import org.example.multiplayer.message.NetworkMessage;
import org.example.multiplayer.message.NetworkMessageCodec;
import org.example.multiplayer.model.PlayerSnapshot;
import org.example.multiplayer.model.PlayerState;
import org.example.multiplayer.model.WorldEntitySnapshot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiplayerClient implements Closeable {

    private final String host;
    private final int port;
    private final String playerName;
    private final MultiplayerClientListener listener;
    private final boolean hostClient;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread listenerThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile String playerId;

    public MultiplayerClient(String host, int port, String playerName, boolean hostClient,
                             MultiplayerClientListener listener) {
        this.host = Objects.requireNonNull(host, "host");
        this.port = port;
        this.playerName = Objects.requireNonNull(playerName, "playerName");
        this.listener = Objects.requireNonNull(listener, "listener");
        this.hostClient = hostClient;
    }

    public void connect() throws IOException {
        if (running.get()) {
            return;
        }
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5_000);
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        running.set(true);
        send(NetworkMessage.of("JOIN")
            .with("name", playerName)
            .with("host", hostClient));
        listenerThread = new Thread(this::listen, "mp-client-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void listen() {
        try {
            while (running.get() && !socket.isClosed()) {
                NetworkMessage message = NetworkMessageCodec.read(in);
                switch (message.type()) {
                    case "WELCOME" -> {
                        playerId = message.get("playerId");
                        listener.onWelcome(playerId);
                    }
                    case "WORLD_STATE" -> {
                        listener.onWorldState(parsePlayerSnapshots(message));
                        listener.onWorldEntities(parseWorldEntities(message));
                    }
                    case "PLAYER_JOINED", "PLAYER_LEFT" ->
                            listener.onPlayerEvent(message.type(), message.get("playerId"), message.get("name"));
                    case "PLAYER_ACTION" ->
                            listener.onPlayerAction(message.get("sourceId"), message.get("action"), message.data());
                    default -> {
                    }
                }
            }
        } catch (IOException e) {
            if (running.get()) {
                listener.onDisconnected(e.getMessage());
            }
        } finally {
            running.set(false);
            closeQuietly();
        }
    }

    private List<PlayerSnapshot> parsePlayerSnapshots(NetworkMessage message) {
        int count = message.getInt("count", 0);
        List<PlayerSnapshot> snapshots = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String id = message.get("id_" + i);
            if (id == null) {
                continue;
            }
            String name = message.get("name_" + i);
            int worldX = message.getInt("worldX_" + i, 0);
            int worldY = message.getInt("worldY_" + i, 0);
            String direction = message.get("direction_" + i);
            int sprite = message.getInt("sprite_" + i, 1);
            boolean moving = message.getBoolean("moving_" + i, false);
            boolean attacking = message.getBoolean("attacking_" + i, false);
            int map = message.getInt("map_" + i, 0);
            long timestamp = 0L;
            String ts = message.get("timestamp_" + i);
            if (ts != null) {
                try {
                    timestamp = Long.parseLong(ts);
                } catch (NumberFormatException ignored) {
                }
            }
            snapshots.add(new PlayerSnapshot(id, name, worldX, worldY, direction, sprite, moving, attacking, map, timestamp));
        }
        return snapshots;
    }

    private List<WorldEntitySnapshot> parseWorldEntities(NetworkMessage message) {
        int count = message.getInt("entityCount", 0);
        if (count <= 0) {
            return List.of();
        }
        List<WorldEntitySnapshot> snapshots = new ArrayList<>(count);
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
            snapshots.add(new WorldEntitySnapshot(
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
        return snapshots;
    }

    public void sendState(PlayerState state) {
        if (!running.get() || state == null) {
            return;
        }
        NetworkMessage message = NetworkMessage.of("STATE")
                .with("worldX", state.worldX())
                .with("worldY", state.worldY())
                .with("direction", state.direction())
                .with("sprite", state.spriteNum())
                .with("moving", state.moving())
                .with("attacking", state.attacking())
                .with("map", state.currentMap());
        send(message);
    }

    public void sendWorldEntities(List<WorldEntitySnapshot> snapshots) {
        if (!running.get() || snapshots == null || !hostClient) {
            return;
        }
        NetworkMessage message = NetworkMessage.of("WORLD_ENTITIES")
                .with("count", snapshots.size());
        for (int i = 0; i < snapshots.size(); i++) {
            WorldEntitySnapshot snapshot = snapshots.get(i);
            message.with("entityId_" + i, snapshot.id())
                    .with("entityClass_" + i, snapshot.className())
                    .with("entityName_" + i, snapshot.name())
                    .with("entityX_" + i, snapshot.worldX())
                    .with("entityY_" + i, snapshot.worldY())
                    .with("entityDir_" + i, snapshot.direction())
                    .with("entitySprite_" + i, snapshot.spriteNum())
                    .with("entityMap_" + i, snapshot.currentMap())
                    .with("entityAlive_" + i, snapshot.alive())
                    .with("entityLife_" + i, snapshot.life())
                    .with("entityMaxLife_" + i, snapshot.maxLife())
                    .with("entityCategory_" + i, snapshot.category())
                    .with("entityTime_" + i, snapshot.timestamp());
        }
        send(message);
    }

    public void disconnect() {
        running.set(false);
        send(NetworkMessage.of("LEAVE"));
        closeQuietly();
    }

    public boolean isConnected() {
        return running.get();
    }

    private synchronized void send(NetworkMessage message) {
        if (!running.get() || out == null) {
            return;
        }
        try {
            NetworkMessageCodec.write(message, out);
        } catch (IOException e) {
            listener.onDisconnected(e.getMessage());
            running.set(false);
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public void sendAction(String actionType, Map<String, String> data) {
        if (!running.get()) {
            return;
        }
        NetworkMessage message = NetworkMessage.of("PLAYER_ACTION")
                .with("action", actionType);
        if (data != null) {
            data.forEach((k, v) -> message.with(k, v));
        }
        send(message);
    }

    @Override
    public void close() {
        disconnect();
    }

    private void closeQuietly() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
