package org.example.achievement;

import java.util.HashMap;
import java.util.Map;

public final class AchievementCatalog {

    private final AchievementGroup root = new AchievementGroup("All Achievements", "Complete the set");
    private final Map<String, Achievement> byKey = new HashMap<>();

    public AchievementCatalog() {
        // Map 0 (Outside) — 5 achievements
        AchievementGroup map0 = new AchievementGroup("Outside", "Starting grounds achievements");
        root.add(map0);
        add(map0, "map0_visit", new SingleAchievement(
                "First Footing",
                "Arrive on the Outside map",
                5,
                1
        ));
        add(map0, "map0_first_blood", new SingleAchievement(
                "First Blood (Outside)",
                "Defeat 1 monster on the Outside map",
                10,
                1
        ));
        add(map0, "map0_hunter", new SingleAchievement(
                "Local Hunter",
                "Defeat 5 monsters on the Outside map",
                15,
                5
        ));
        add(map0, "map0_defender", new SingleAchievement(
                "Village Defender",
                "Defeat 15 monsters on the Outside map",
                25,
                15
        ));
        add(map0, "map0_purifier", new SingleAchievement(
                "Field Purifier",
                "Defeat 30 monsters on the Outside map",
                35,
                30
        ));

        // Map 1 (Indoor) — a few achievements
        AchievementGroup map1 = new AchievementGroup("Indoor", "House & merchant zone achievements");
        root.add(map1);
        add(map1, "map1_visit", new SingleAchievement(
                "House Guest",
                "Enter the Indoor map",
                5,
                1
        ));
        add(map1, "map1_sweeper", new SingleAchievement(
                "Hallway Sweeper",
                "Defeat 5 monsters on the Indoor map",
                15,
                5
        ));

        // Map 2 (Dungeon) — a few achievements
        AchievementGroup map2 = new AchievementGroup("Dungeon", "Lower-level challenges");
        root.add(map2);
        add(map2, "map2_visit", new SingleAchievement(
                "Into the Depths",
                "Enter the Dungeon map",
                10,
                1
        ));
        add(map2, "map2_stalker", new SingleAchievement(
                "Dungeon Stalker",
                "Defeat 10 monsters in the Dungeon",
                25,
                10
        ));
    }

    private void add(AchievementGroup parent, String key, Achievement achievement) {
        parent.add(achievement);
        byKey.put(key, achievement);
    }

    public AchievementGroup getRoot() {
        return root;
    }

    public Achievement get(String key) {
        return byKey.get(key);
    }
}
