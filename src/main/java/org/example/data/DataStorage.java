package org.example.data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataStorage implements Serializable {

    //PLAYER STATS
    int level;
    int maxLife;
    int life;
    int maxMana;
    int mana;
    int strength;
    int dexterity;
    int exp;
    int nextLevelExp;
    int coin;

    // PLAYER POSITION/CONTEXT
    int playerWorldX;
    int playerWorldY;
    String playerDirection;
    int currentMap;
    int currentArea;

    //PLAYER INVENTORY
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<Integer> itemAmounts = new ArrayList<>();
    int currentWeaponSlot;
    int currentShieldSlot;
    int currentLightSlot;

    //OBJECT ON MAP
    String mapObjectNames[][];
    int mapObjectWorldX[][];
    int mapObjectWorldY[][];
    String mapObjectLootNames[][];
    boolean mapObjectOpened[][];

    // ENVIRONMENT
    int dayState;
    int dayCounter;
    float filterAlpha;

    // NPCS
    boolean npcPresent[][];
    int npcWorldX[][];
    int npcWorldY[][];
    String npcDirection[][];

    // MONSTERS
    boolean monsterPresent[][];
    int monsterWorldX[][];
    int monsterWorldY[][];
    int monsterLife[][];
    String monsterDirection[][];
}
