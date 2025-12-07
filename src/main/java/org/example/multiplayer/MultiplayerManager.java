package org.example.multiplayer;

import org.example.entity.Entity;
import org.example.multiplayer.client.MultiplayerClient;
import org.example.multiplayer.client.MultiplayerClientListener;
import org.example.multiplayer.model.PlayerSnapshot;
import org.example.multiplayer.model.PlayerState;
import org.example.multiplayer.model.WorldEntitySnapshot;
import org.example.multiplayer.server.GameServer;
import org.example.main.GamePanel;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Coordinates multiplayer concerns between the Swing game loop and networking stack.
 */
public class MultiplayerManager implements MultiplayerClientListener {

    private static final long STATE_INTERVAL_NANOS = 50_000_000L; // 20 Hz
    private static final long STALE_PLAYER_THRESHOLD = 4_000L;

    private final GamePanel gp;
    private final Map<String, RemotePlayer> remotePlayers = new ConcurrentHashMap<>();
    private final Map<String, RemoteEntityMirror> remoteWorldEntities = new ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "mp-manager");
        thread.setDaemon(true);
        return thread;
    });

    private MultiplayerClient client;
    private GameServer embeddedServer;
    private String localPlayerId;
    private String requestedName;
    private volatile boolean hosting;
    private long lastStatePublished;
    private long lastEntityPublished;
    private boolean clientWorldInitialized;

    public MultiplayerManager(GamePanel gp) {
        this.gp = Objects.requireNonNull(gp, "gp");
    }

    public CompletableFuture<Void> hostAndConnect(int port, String playerName) {
        hosting = true;
        return CompletableFuture.runAsync(() -> {
            try {
                startServer(port);
                connectInternal("localhost", port, playerName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, worker);
    }

    public CompletableFuture<Void> join(String host, int port, String playerName) {
        hosting = false;
        return CompletableFuture.runAsync(() -> {
            try {
                connectInternal(host, port, playerName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }, worker);
    }

    private void startServer(int port) throws IOException {
        if (embeddedServer != null) {
            return;
        }
        embeddedServer = new GameServer(port);
        embeddedServer.start();
    }

    private void connectInternal(String host, int port, String playerName) throws IOException {
        requestedName = playerName;
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        client = new MultiplayerClient(host, port, playerName, hosting, this);
        try {
            client.connect();
        } catch (IOException ex) {
            client = null;
            throw ex;
        }
    }

    public void tick() {
        pruneStalePlayers();
        if (client == null || !client.isConnected() || gp.player == null) {
            return;
        }
        long now = System.nanoTime();
        if (now - lastStatePublished >= STATE_INTERVAL_NANOS) {
            lastStatePublished = now;
            PlayerState state = new PlayerState(
                localPlayerId,
                requestedName,
                gp.player.worldX,
                gp.player.worldY,
                gp.player.direction,
                gp.player.spriteNum,
                gp.keyH.upPressed || gp.keyH.downPressed || gp.keyH.leftPressed || gp.keyH.rightPressed,
                gp.player.attacking,
                gp.currentMap
            );
            client.sendState(state);
        }
        if (hosting) {
            publishWorldEntities(now);
        }
    }

    private void pruneStalePlayers() {
        long now = System.currentTimeMillis();
        remotePlayers.entrySet().removeIf(entry -> entry.getValue().isStale(now, STALE_PLAYER_THRESHOLD));
        remoteWorldEntities.entrySet().removeIf(entry -> entry.getValue().isStale(now, STALE_PLAYER_THRESHOLD));
    }

    public Collection<RemotePlayer> getRenderablePlayers() {
        List<RemotePlayer> players = new ArrayList<>();
        for (RemotePlayer player : remotePlayers.values()) {
            if (player.getCurrentMap() == gp.currentMap) {
                players.add(player);
            }
        }
        return players;
    }

    public Collection<Entity> getRemoteWorldEntities() {
        return List.of();
    }

    public void shutdown() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        if (embeddedServer != null) {
            embeddedServer.stop();
            embeddedServer = null;
        }
        worker.shutdownNow();
        remotePlayers.clear();
        remoteWorldEntities.clear();
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean isHosting() {
        return hosting;
    }

    public boolean isClient() {
        return !hosting;
    }

    @Override
    public void onWelcome(String playerId) {
        this.localPlayerId = playerId;
        postUiMessage("Connected to multiplayer as " + requestedName);
        if (!hosting) {
            initializeClientWorld();
        }
    }

    @Override
    public void onWorldState(List<PlayerSnapshot> snapshots) {
        if (snapshots == null) {
            return;
        }
        Set<String> updated = new HashSet<>();
        for (PlayerSnapshot snapshot : snapshots) {
            if (snapshot.id() == null || snapshot.id().equals(localPlayerId)) {
                continue;
            }
            RemotePlayer player = remotePlayers.computeIfAbsent(snapshot.id(), id -> new RemotePlayer(gp));
            player.apply(snapshot);
            updated.add(snapshot.id());
        }
        remotePlayers.keySet().removeIf(id -> !updated.contains(id));
    }

    @Override
    public void onWorldEntities(List<WorldEntitySnapshot> snapshots) {
        if (hosting) {
            return;
        }
        if (snapshots == null) {
            remoteWorldEntities.clear();
            if (!hosting) {
                writeRemoteEntitiesIntoWorld();
            }
            return;
        }
        Set<String> updated = new HashSet<>();
        for (WorldEntitySnapshot snapshot : snapshots) {
            RemoteEntityMirror entity = remoteWorldEntities.computeIfAbsent(
                    snapshot.id(), id -> new RemoteEntityMirror(gp));
            entity.apply(snapshot);
            updated.add(snapshot.id());
        }
        remoteWorldEntities.keySet().removeIf(id -> !updated.contains(id));
        if (!hosting) {
            writeRemoteEntitiesIntoWorld();
        }
    }

    @Override
    public void onPlayerEvent(String eventType, String playerId, String playerName) {
        String displayName = (playerName == null || playerName.isBlank()) ? "Player" : playerName;
        String text = switch (eventType) {
            case "PLAYER_JOINED" -> displayName + " joined.";
            case "PLAYER_LEFT" -> displayName + " left.";
            default -> null;
        };
        if (text != null) {
            postUiMessage(text);
        }
    }

    @Override
    public void onDisconnected(String reason) {
        remotePlayers.clear();
        remoteWorldEntities.clear();
        postUiMessage("Disconnected: " + reason);
    }

    @Override
    public void onPlayerAction(String sourcePlayerId, String actionType, Map<String, String> payload) {
        // Host authoritative handling will be added when action pipeline is wired in.
    }

    private void postUiMessage(String text) {
        if (text == null || gp.ui == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> gp.ui.addMessage(text));
    }

    private void publishWorldEntities(long now) {
        if (client == null || !client.isConnected()) {
            return;
        }
        if (now - lastEntityPublished < STATE_INTERVAL_NANOS) {
            return;
        }
        lastEntityPublished = now;
        client.sendWorldEntities(collectWorldEntities());
    }

    private List<WorldEntitySnapshot> collectWorldEntities() {
        List<WorldEntitySnapshot> snapshots = new ArrayList<>();
        collectEntityArray(snapshots, gp.npc, "NPC");
        collectEntityArray(snapshots, gp.monster, "MONSTER");
        return snapshots;
    }

    private void collectEntityArray(List<WorldEntitySnapshot> target, Entity[][] source, String category) {
        if (source == null) {
            return;
        }
        long timestamp = System.currentTimeMillis();
        for (int map = 0; map < source.length; map++) {
            Entity[] row = source[map];
            if (row == null) {
                continue;
            }
            for (int index = 0; index < row.length; index++) {
                Entity entity = row[index];
                if (entity == null) {
                    continue;
                }
                target.add(new WorldEntitySnapshot(
                        category + ":" + map + ":" + index,
                        entity.getClass().getName(),
                        entity.name,
                        entity.worldX,
                        entity.worldY,
                        entity.direction,
                        entity.spriteNum,
                        map,
                        entity.alive,
                        entity.life,
                        entity.maxLife,
                        category,
                        timestamp
                ));
            }
        }
    }

    private void initializeClientWorld() {
        if (clientWorldInitialized) {
            return;
        }
        clientWorldInitialized = true;
        SwingUtilities.invokeLater(() -> {
            clearEntityArray(gp.npc);
            clearEntityArray(gp.monster);
        });
    }

    private void clearEntityArray(Entity[][] array) {
        if (array == null) {
            return;
        }
        for (Entity[] row : array) {
            if (row != null) {
                Arrays.fill(row, null);
            }
        }
    }

    private void writeRemoteEntitiesIntoWorld() {
        SwingUtilities.invokeLater(() -> {
            clearEntityArray(gp.npc);
            clearEntityArray(gp.monster);
            for (RemoteEntityMirror entity : remoteWorldEntities.values()) {
                assignRemoteEntity(entity);
            }
        });
    }

    private void assignRemoteEntity(RemoteEntityMirror entity) {
        if (entity == null || entity.getRemoteId() == null) {
            return;
        }
        String[] parts = entity.getRemoteId().split(":");
        if (parts.length < 3) {
            return;
        }
        int map;
        int index;
        try {
            map = Integer.parseInt(parts[1]);
            index = Integer.parseInt(parts[2]);
        } catch (NumberFormatException ex) {
            return;
        }
        Entity[][] target = "NPC".equals(parts[0]) ? gp.npc : gp.monster;
        if (target == null || map < 0 || map >= target.length) {
            return;
        }
        Entity[] row = target[map];
        if (row == null || index < 0 || index >= row.length) {
            return;
        }
        row[index] = entity;
    }
}
