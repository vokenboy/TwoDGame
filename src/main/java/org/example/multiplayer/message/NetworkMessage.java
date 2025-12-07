package org.example.multiplayer.message;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Lightweight message envelope exchanged between clients and the multiplayer server.
 */
public final class NetworkMessage {

    private final String type;
    private final Map<String, String> data;

    private NetworkMessage(String type, Map<String, String> data) {
        this.type = Objects.requireNonNull(type, "type");
        this.data = data == null ? new LinkedHashMap<>() : new LinkedHashMap<>(data);
    }

    public static NetworkMessage of(String type) {
        return new NetworkMessage(type, new LinkedHashMap<>());
    }

    public static NetworkMessage from(String type, Map<String, String> data) {
        return new NetworkMessage(type, data);
    }

    public NetworkMessage with(String key, Object value) {
        data.put(key, value == null ? "" : String.valueOf(value));
        return this;
    }

    public String type() {
        return type;
    }

    public Map<String, String> data() {
        return Collections.unmodifiableMap(data);
    }

    public String get(String key) {
        return data.get(key);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(data.get(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(data.get(key));
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
