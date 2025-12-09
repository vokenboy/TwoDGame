package org.example.achievement;

import java.util.HashSet;
import java.util.Set;

public final class MapVisitTracker {
    private static final Set<Integer> visitedMaps = new HashSet<>();

    private MapVisitTracker() {}

    public static void markVisited(int mapIndex) {
        visitedMaps.add(mapIndex);
    }

    public static int visitedCount() {
        return visitedMaps.size();
    }

    public static void reset() {
        visitedMaps.clear();
    }
}
