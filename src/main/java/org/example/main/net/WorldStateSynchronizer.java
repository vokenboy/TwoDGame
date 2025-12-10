package org.example.main.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.entity.Entity;
import org.example.entity.Player;
import org.example.main.GamePanel;
import org.example.main.input.RemotePlayerInput;
import org.example.object.OBJ_Fireball;

/**
 * Utility to capture and apply authoritative world state snapshots.
 */
public final class WorldStateSynchronizer {

    private WorldStateSynchronizer() {}

    public static NetworkMessages.WorldState capture(GamePanel gp, long tick) {
        NetworkMessages.WorldState ws = new NetworkMessages.WorldState();
        ws.tick = tick;

        for (Player p : gp.getPlayers()) {
            if (p == null) continue;
            NetworkMessages.PlayerState ps = new NetworkMessages.PlayerState();
            ps.playerId = p.getNetworkId();
            ps.name = p.name;
            ps.worldX = p.worldX;
            ps.worldY = p.worldY;
            ps.direction = p.direction;
            ps.life = p.life;
            ps.maxLife = p.maxLife;
            ps.attacking = p.attacking;
            ps.guarding = p.guarding;
            ps.spriteNum = p.spriteNum;
            ps.alive = p.alive;
            ws.players.add(ps);
        }

        // NPCs
        for (int map = 0; map < gp.maxMap; map++) {
            for (int i = 0; i < gp.npc[map].length; i++) {
                Entity e = gp.npc[map][i];
                if (e != null) {
                    ws.npcs.add(toEntityState(map, i, e));
                } else {
                    ws.npcs.add(emptyState(map, i));
                }
            }
        }

        // Monsters
        for (int map = 0; map < gp.maxMap; map++) {
            for (int i = 0; i < gp.monster[map].length; i++) {
                Entity e = gp.monster[map][i];
                if (e != null) {
                    ws.monsters.add(toEntityState(map, i, e));
                } else {
                    ws.monsters.add(emptyState(map, i));
                }
            }
        }

        // Projectiles
        for (int map = 0; map < gp.maxMap; map++) {
            for (int i = 0; i < gp.projectile[map].length; i++) {
                Entity e = gp.projectile[map][i];
                if (e != null) {
                    ws.projectiles.add(toEntityState(map, i, e));
                } else {
                    ws.projectiles.add(emptyState(map, i));
                }
            }
        }

        // Objects
        for (int map = 0; map < gp.maxMap; map++) {
            for (int i = 0; i < gp.obj[map].length; i++) {
                Entity e = gp.obj[map][i];
                if (e != null) {
                    ws.objects.add(toEntityState(map, i, e));
                } else {
                    ws.objects.add(emptyState(map, i));
                }
            }
        }

        return ws;
    }

    private static NetworkMessages.EntityState toEntityState(
        int map,
        int slot,
        Entity e
    ) {
        NetworkMessages.EntityState es = new NetworkMessages.EntityState();
        es.mapIndex = map;
        es.slotIndex = slot;
        es.name = e.name;
        es.worldX = e.worldX;
        es.worldY = e.worldY;
        es.direction = e.direction;
        es.life = e.life;
        es.maxLife = e.maxLife;
        es.alive = e.alive;
        es.dying = e.dying;
        es.spriteNum = e.spriteNum;
        es.hpBarOn = e.hpBarOn;
        es.hpBarCounter = e.hpBarCounter;
        return es;
    }

    private static NetworkMessages.EntityState emptyState(int map, int slot) {
        NetworkMessages.EntityState es = new NetworkMessages.EntityState();
        es.mapIndex = map;
        es.slotIndex = slot;
        es.alive = false;
        return es;
    }

    public static void apply(GamePanel gp, NetworkMessages.WorldState ws) {
        if (ws == null) return;

        // Players
        for (NetworkMessages.PlayerState ps : ws.players) {
            Player p = gp.getPlayerById(ps.playerId);
            if (p == null) {
                p = gp.addPlayer(new RemotePlayerInput(), ps.name, ps.playerId);
            }
            p.name = ps.name;
            p.worldX = ps.worldX;
            p.worldY = ps.worldY;
            p.direction = ps.direction;
            p.life = ps.life;
            p.maxLife = ps.maxLife;
            p.attacking = ps.attacking;
            p.guarding = ps.guarding;
            p.spriteNum = ps.spriteNum;
            p.alive = ps.alive;
        }

        // NPCs
        syncEntityList(ws.npcs, gp.npc);

        // Monsters
        syncEntityList(ws.monsters, gp.monster);

        // Projectiles
        syncProjectiles(ws.projectiles, gp.projectile, gp);

        // Objects
        syncEntityList(ws.objects, gp.obj);
    }

    private static void syncEntityList(
        java.util.List<NetworkMessages.EntityState> states,
        Entity[][] target
    ) {
        for (NetworkMessages.EntityState es : states) {
            if (es.mapIndex < 0 || es.mapIndex >= target.length) continue;
            if (
                es.slotIndex < 0 || es.slotIndex >= target[es.mapIndex].length
            ) continue;
            if (!es.alive) {
                target[es.mapIndex][es.slotIndex] = null;
                continue;
            }
            Entity e = target[es.mapIndex][es.slotIndex];
            if (e != null) {
                e.worldX = es.worldX;
                e.worldY = es.worldY;
                e.direction = es.direction;
                e.life = es.life;
                e.maxLife = es.maxLife;
                e.alive = es.alive;
                e.dying = es.dying;
                e.spriteNum = es.spriteNum;
                e.hpBarOn = es.hpBarOn;
                e.hpBarCounter = es.hpBarCounter;
            }
        }
    }

    private static void syncProjectiles(
        java.util.List<NetworkMessages.EntityState> states,
        Entity[][] target,
        GamePanel gp
    ) {
        for (NetworkMessages.EntityState es : states) {
            if (es.mapIndex < 0 || es.mapIndex >= target.length) continue;
            if (
                es.slotIndex < 0 || es.slotIndex >= target[es.mapIndex].length
            ) continue;
            if (!es.alive) {
                target[es.mapIndex][es.slotIndex] = null;
                continue;
            }
            Entity e = target[es.mapIndex][es.slotIndex];
            if (e == null) {
                // Basic projectile placeholder so clients can render fireballs
                e = new OBJ_Fireball(gp);
                target[es.mapIndex][es.slotIndex] = e;
            }
            e.worldX = es.worldX;
            e.worldY = es.worldY;
            e.direction = es.direction;
            e.life = es.life;
            e.maxLife = es.maxLife;
            e.alive = es.alive;
            e.dying = es.dying;
            e.spriteNum = es.spriteNum;
            e.hpBarOn = es.hpBarOn;
            e.hpBarCounter = es.hpBarCounter;
        }
    }
}
