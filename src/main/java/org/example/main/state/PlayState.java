package org.example.main.state;

import org.example.entity.Entity;
import org.example.main.DamageNumber;
import org.example.main.GamePanel;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;

public class PlayState implements GameState {

    @Override
    public void update(GamePanel gp) {

        // PLAYER
        gp.player.update();

        // NPC
        for (int i = 0; i < gp.npc[1].length; i++) {
            if (gp.npc[gp.currentMap][i] != null) {
                gp.npc[gp.currentMap][i].update();
            }
        }

        // MONSTER
        for (int i = 0; i < gp.monster[1].length; i++) {
            if (gp.monster[gp.currentMap][i] != null) {
                if (gp.monster[gp.currentMap][i].alive && !gp.monster[gp.currentMap][i].dying) {
                    gp.monster[gp.currentMap][i].update();
                }
                if (!gp.monster[gp.currentMap][i].alive) {
                    gp.monster[gp.currentMap][i].checkDrop();
                    gp.monster[gp.currentMap][i] = null;
                }
            }
        }

        // PROJECTILE
        for (int i = 0; i < gp.projectile[1].length; i++) {
            if (gp.projectile[gp.currentMap][i] != null) {
                if (gp.projectile[gp.currentMap][i].alive) {
                    gp.projectile[gp.currentMap][i].update();
                }
                if (!gp.projectile[gp.currentMap][i].alive) {
                    gp.projectile[gp.currentMap][i] = null;
                }
            }
        }

        // DAMAGE NUMBERS
        for (int i = 0; i < gp.damageNumbers.size(); i++) {
            DamageNumber dn = gp.damageNumbers.get(i);
            if (dn != null) {
                dn.update();
                if (!dn.alive) {
                    gp.damageNumbers.remove(i);
                    i--;
                }
            }
        }

        // PARTICLES
        for (int i = 0; i < gp.particleList.size(); i++) {
            if (gp.particleList.get(i) != null) {
                if (gp.particleList.get(i).alive) {
                    gp.particleList.get(i).update();
                }
                if (!gp.particleList.get(i).alive) {
                    gp.particleList.remove(i);
                }
            }
        }

        // INTERACTIVE TILE
        for (int i = 0; i < gp.iTile[1].length; i++) {
            if (gp.iTile[gp.currentMap][i] != null) {
                gp.iTile[gp.currentMap][i].update();
            }
        }

        gp.eManager.update();
        gp.keyH.update();
    }

    @Override
    public void draw(GamePanel gp, Graphics2D g2) {
        // === this is basically the "else" part of drawToTempScreen() ===

        // TILE
        gp.tileM.draw(g2);

        // INTERACTIVE TILE
        for (int i = 0; i < gp.iTile[1].length; i++) {
            if (gp.iTile[gp.currentMap][i] != null) {
                gp.iTile[gp.currentMap][i].draw(g2);
            }
        }

        // ADD ENTITIES
        gp.entityList.add(gp.player);

        for (int i = 0; i < gp.npc[1].length; i++) {
            if (gp.npc[gp.currentMap][i] != null) {
                gp.entityList.add(gp.npc[gp.currentMap][i]);
            }
        }

        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                gp.entityList.add(gp.obj[gp.currentMap][i]);
            }
        }

        for (int i = 0; i < gp.monster[1].length; i++) {
            if (gp.monster[gp.currentMap][i] != null) {
                gp.entityList.add(gp.monster[gp.currentMap][i]);
            }
        }

        for (int i = 0; i < gp.projectile[1].length; i++) {
            if (gp.projectile[gp.currentMap][i] != null) {
                gp.entityList.add(gp.projectile[gp.currentMap][i]);
            }
        }

        // DAMAGE NUMBERS
        for (DamageNumber dn : gp.damageNumbers) {
            dn.draw(g2, gp);
        }

        for (int i = 0; i < gp.particleList.size(); i++) {
            if (gp.particleList.get(i) != null) {
                gp.entityList.add(gp.particleList.get(i));
            }
        }

        // SORT & DRAW
        Collections.sort(gp.entityList, Comparator.comparingInt(e -> e.worldY));
        for (Entity e : gp.entityList) {
            e.draw(g2);
        }
        gp.entityList.clear();

        // ENVIRONMENT, MAP, CUTSCENE, UI
        gp.eManager.draw(g2);
        gp.map.drawMiniMap(g2);
        gp.csManager.draw(g2);
        gp.ui.draw(g2);
    }
}
