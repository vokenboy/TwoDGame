package org.example.multiplayer.client;

import org.example.multiplayer.model.PlayerSnapshot;
import org.example.multiplayer.model.WorldEntitySnapshot;

import java.util.List;
import java.util.Map;

public interface MultiplayerClientListener {
    void onWelcome(String playerId);

    void onWorldState(List<PlayerSnapshot> snapshots);

    void onWorldEntities(List<WorldEntitySnapshot> snapshots);

    void onPlayerEvent(String eventType, String playerId, String playerName);

    void onDisconnected(String reason);

    void onPlayerAction(String sourcePlayerId, String actionType, Map<String, String> payload);
}
