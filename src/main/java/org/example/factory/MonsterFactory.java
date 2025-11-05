package org.example.factory;

import org.example.main.GamePanel;
import org.example.monster.Monster;

public interface MonsterFactory {
    Monster createSlime(GamePanel gp);
    Monster createBat(GamePanel gp);
}
