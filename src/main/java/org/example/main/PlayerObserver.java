package org.example.main;

import org.example.entity.Player;
import org.example.entity.Entity;

public interface PlayerObserver {
    void onHealthChange(Player player);
    void onManaChange(Player player);
    void onLevelUp(Player player);
    void onMonsterDamaged(Entity monster, int damage);
}
