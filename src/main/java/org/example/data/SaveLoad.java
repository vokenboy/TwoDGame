package org.example.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.example.memento.GameStateMemento;
import org.example.main.GamePanel;
import org.example.object.*;

public class SaveLoad {

    private final GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public GameStateMemento createMemento() {
        return new GameStateMemento(captureSnapshot());
    }

    public void restore(GameStateMemento memento) {
        if (memento == null || memento.getSnapshot() == null) {
            return;
        }
        applySnapshot(memento.getSnapshot());
    }

    public String describeMemento(GameStateMemento memento) {
        if (memento == null || memento.getSnapshot() == null) {
            return "Unknown snapshot";
        }
        DataStorage ds = memento.getSnapshot();
        String areaLabel = areaName(ds.currentArea);
        return String.format(
            "%s | Map %d | Lv %d HP %d/%d",
            areaLabel,
            ds.currentMap,
            ds.level,
            ds.life,
            ds.maxLife
        );
    }

    public void save() {
        try (
            ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("save.dat"))
            )
        ) {
            GameStateMemento memento = createMemento();
            oos.writeObject(memento.getSnapshot());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        try (
            ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(new File("save.dat"))
            )
        ) {
            DataStorage ds = (DataStorage) ois.readObject();
            restore(new GameStateMemento(ds));
        } catch (Exception e) {
            System.out.println("Load Exception!");
        }
    }

    private DataStorage captureSnapshot() {
        DataStorage ds = new DataStorage();

        //PLAYER STATS
        ds.level = gp.player.level;
        ds.maxLife = gp.player.maxLife;
        ds.life = gp.player.life;
        ds.maxMana = gp.player.maxMana;
        ds.mana = gp.player.mana;
        ds.strength = gp.player.strength;
        ds.dexterity = gp.player.dexterity;
        ds.exp = gp.player.exp;
        ds.nextLevelExp = gp.player.nextLevelExp;
        ds.coin = gp.player.coin;

        // PLAYER POSITION/CONTEXT
        ds.playerWorldX = gp.player.worldX;
        ds.playerWorldY = gp.player.worldY;
        ds.playerDirection = gp.player.direction;
        ds.currentMap = gp.currentMap;
        ds.currentArea = gp.currentArea;

        //PLAYER INVENTORY
        for (int i = 0; i < gp.player.inventory.size(); i++) {
            ds.itemNames.add(gp.player.inventory.get(i).name);
            ds.itemAmounts.add(gp.player.inventory.get(i).amount);
        }

        //PLAYER EQUIPMENT
        ds.currentWeaponSlot = gp.player.getCurrentWeaponSlot();
        ds.currentShieldSlot = gp.player.getCurrentShieldSlot();
        ds.currentLightSlot = gp.player.getCurrentLightSlot();

        //OBJECTS ON MAP
        ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length]; //2nd dimension of obj array
        ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];
        ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

        ds.npcPresent = new boolean[gp.maxMap][gp.npc[1].length];
        ds.npcWorldX = new int[gp.maxMap][gp.npc[1].length];
        ds.npcWorldY = new int[gp.maxMap][gp.npc[1].length];
        ds.npcDirection = new String[gp.maxMap][gp.npc[1].length];

        ds.monsterPresent = new boolean[gp.maxMap][gp.monster[1].length];
        ds.monsterWorldX = new int[gp.maxMap][gp.monster[1].length];
        ds.monsterWorldY = new int[gp.maxMap][gp.monster[1].length];
        ds.monsterLife = new int[gp.maxMap][gp.monster[1].length];
        ds.monsterDirection = new String[gp.maxMap][gp.monster[1].length];

        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            for (int i = 0; i < gp.obj[1].length; i++) {
                if (gp.obj[mapNum][i] == null) {
                    ds.mapObjectNames[mapNum][i] = "NA";
                } else {
                    ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
                    ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
                    ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;
                    if (gp.obj[mapNum][i].loot != null) {
                        ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name;
                    }
                    ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
                }
            }
        }

        // NPCs
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            for (int i = 0; i < gp.npc[1].length; i++) {
                if (gp.npc[mapNum][i] != null && gp.npc[mapNum][i].alive) {
                    ds.npcPresent[mapNum][i] = true;
                    ds.npcWorldX[mapNum][i] = gp.npc[mapNum][i].worldX;
                    ds.npcWorldY[mapNum][i] = gp.npc[mapNum][i].worldY;
                    ds.npcDirection[mapNum][i] = gp.npc[mapNum][i].direction;
                } else {
                    ds.npcPresent[mapNum][i] = false;
                }
            }
        }

        // Monsters
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            for (int i = 0; i < gp.monster[1].length; i++) {
                if (
                    gp.monster[mapNum][i] != null &&
                    gp.monster[mapNum][i].alive
                ) {
                    ds.monsterPresent[mapNum][i] = true;
                    ds.monsterWorldX[mapNum][i] = gp.monster[mapNum][i].worldX;
                    ds.monsterWorldY[mapNum][i] = gp.monster[mapNum][i].worldY;
                    ds.monsterLife[mapNum][i] = gp.monster[mapNum][i].life;
                    ds.monsterDirection[mapNum][i] =
                        gp.monster[mapNum][i].direction;
                } else {
                    ds.monsterPresent[mapNum][i] = false;
                }
            }
        }

        // ENVIRONMENT
        var env = gp.getEnvironmentManager();
        if (env != null && env.lighting != null) {
            ds.dayState = env.lighting.dayState;
            ds.dayCounter = env.lighting.dayCounter;
            ds.filterAlpha = env.lighting.filterAlpha;
        }

        return ds;
    }

    private String areaName(int currentArea) {
        if (currentArea == gp.outside) {
            return "Outside";
        }
        if (currentArea == gp.indoor) {
            return "Indoor";
        }
        if (currentArea == gp.dungeon) {
            return "Dungeon";
        }
        return "Area " + currentArea;
    }

    private void applySnapshot(DataStorage ds) {
        //PLAYER STATS
        gp.player.level = ds.level;
        gp.player.maxLife = ds.maxLife;
        gp.player.life = ds.life;
        gp.player.maxMana = ds.maxMana;
        gp.player.mana = ds.mana;
        gp.player.strength = ds.strength;
        gp.player.dexterity = ds.dexterity;
        gp.player.exp = ds.exp;
        gp.player.nextLevelExp = ds.nextLevelExp;
        gp.player.coin = ds.coin;

        // PLAYER POSITION/CONTEXT
        gp.currentMap = ds.currentMap;
        gp.currentArea = ds.currentArea;
        gp.player.worldX = ds.playerWorldX;
        gp.player.worldY = ds.playerWorldY;
        if (ds.playerDirection != null) {
            gp.player.direction = ds.playerDirection;
        }

        //PLAYER INVENTORY
        gp.player.inventory.clear();
        for (int i = 0; i < ds.itemNames.size(); i++) {
            gp.player.inventory.add(gp.eGenerator.getObject(ds.itemNames.get(i)));
            gp.player.inventory.get(i).amount = ds.itemAmounts.get(i);
        }

        //PLAYER EQUIPMENT
        gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
        gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
        gp.player.getAttack();
        gp.player.getDefense();
        gp.player.getAttackImage();
        if (
            ds.currentLightSlot >= 0 &&
            ds.currentLightSlot < gp.player.inventory.size()
        ) {
            gp.player.currentLight = gp.player.inventory.get(ds.currentLightSlot);
            gp.player.lightUpdated = true;
        } else {
            gp.player.currentLight = null;
        }

        // Reset NPCs/Monsters to a known state before applying snapshot data.
        gp.aSetter.setNPC();
        gp.aSetter.setMonster();

        //OBJECTS ON MAP
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            for (int i = 0; i < gp.obj[1].length; i++) {
                if (ds.mapObjectNames[mapNum][i].equals("NA")) {
                    gp.obj[mapNum][i] = null;
                } else {
                    gp.obj[mapNum][i] = gp.eGenerator.getObject(ds.mapObjectNames[mapNum][i]);
                    gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                    gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];
                    if (ds.mapObjectLootNames[mapNum][i] != null) {
                        gp.obj[mapNum][i].setLoot(
                            gp.eGenerator.getObject(ds.mapObjectLootNames[mapNum][i])
                        );
                    }
                    gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
                    if (gp.obj[mapNum][i].opened == true) {
                        gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
                    }
                    gp.obj[mapNum][i].setDialogue(); // added this line
                }
            }
        }

        // NPCs
        if (ds.npcPresent != null) {
            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
                int npcSlots = Math.min(
                    gp.npc[mapNum].length,
                    ds.npcPresent.length > mapNum
                        ? ds.npcPresent[mapNum].length
                        : 0
                );
                for (int i = 0; i < npcSlots; i++) {
                    boolean present = ds.npcPresent[mapNum][i];
                    if (!present) {
                        gp.npc[mapNum][i] = null;
                        continue;
                    }
                    if (gp.npc[mapNum][i] != null) {
                        gp.npc[mapNum][i].worldX = ds.npcWorldX[mapNum][i];
                        gp.npc[mapNum][i].worldY = ds.npcWorldY[mapNum][i];
                        if (ds.npcDirection[mapNum][i] != null) {
                            gp.npc[mapNum][i].direction =
                                ds.npcDirection[mapNum][i];
                        }
                        gp.npc[mapNum][i].alive = true;
                        gp.npc[mapNum][i].sleep = false;
                    }
                }
            }
        }

        // Monsters
        if (ds.monsterPresent != null) {
            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
                int monsterSlots = Math.min(
                    gp.monster[mapNum].length,
                    ds.monsterPresent.length > mapNum
                        ? ds.monsterPresent[mapNum].length
                        : 0
                );
                for (int i = 0; i < monsterSlots; i++) {
                    boolean present = ds.monsterPresent[mapNum][i];
                    if (!present) {
                        gp.monster[mapNum][i] = null;
                        continue;
                    }
                    if (gp.monster[mapNum][i] != null) {
                        gp.monster[mapNum][i].worldX = ds.monsterWorldX[mapNum][i];
                        gp.monster[mapNum][i].worldY = ds.monsterWorldY[mapNum][i];
                        gp.monster[mapNum][i].life = ds.monsterLife[mapNum][i];
                        if (ds.monsterDirection[mapNum][i] != null) {
                            gp.monster[mapNum][i].direction =
                                ds.monsterDirection[mapNum][i];
                        }
                        gp.monster[mapNum][i].alive = true;
                        gp.monster[mapNum][i].dying = false;
                        gp.monster[mapNum][i].sleep = false;
                    }
                }
            }
        }

        // ENVIRONMENT
        var env = gp.getEnvironmentManager();
        if (env != null && env.lighting != null) {
            env.lighting.dayState = ds.dayState;
            env.lighting.dayCounter = ds.dayCounter;
            env.lighting.filterAlpha = ds.filterAlpha;
            env.lighting.setLightSource();
        }
    }
}
