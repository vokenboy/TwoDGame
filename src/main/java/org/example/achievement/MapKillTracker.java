package org.example.achievement;

import java.util.HashMap;
import java.util.Map;

public final class MapKillTracker {
    private static final Map<Integer, Integer> killsByMap = new HashMap<>();

    private MapKillTracker() {}

    public static int increment(int mapIndex) {
        int next = killsByMap.getOrDefault(mapIndex, 0) + 1;
        killsByMap.put(mapIndex, next);
        return next;
    }

    public static int get(int mapIndex) {
        return killsByMap.getOrDefault(mapIndex, 0);
    }

    public static void reset() {
        killsByMap.clear();
    }
}
