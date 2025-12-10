package org.example.monster;

import org.example.main.GamePanel;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;

public class MonsterSpriteFactory {

    private static final Map<String, MonsterSpriteSet> cache = new HashMap<>();

    private static BufferedImage load(GamePanel gp, String path) {
        return gp.player.setup(path, gp.tileSize, gp.tileSize);
    }

    public static MonsterSpriteSet getGreenSlime(GamePanel gp) {
        if (cache.containsKey("GreenSlime")) return cache.get("GreenSlime");

        MonsterSpriteSet set = new MonsterSpriteSet();

        set.down1 = load(gp, "/monster/greenslime_down_1");
        set.down2 = load(gp, "/monster/greenslime_down_2");

        set.up1 = set.down1;
        set.up2 = set.down2;
        set.left1 = set.down1;
        set.left2 = set.down2;
        set.right1 = set.down1;
        set.right2 = set.down2;

        cache.put("GreenSlime", set);
        return set;
    }

    public static MonsterSpriteSet getRedSlime(GamePanel gp) {
        if (cache.containsKey("RedSlime")) return cache.get("RedSlime");

        MonsterSpriteSet set = new MonsterSpriteSet();

        set.down1 = load(gp, "/monster/redslime_down_1");
        set.down2 = load(gp, "/monster/redslime_down_2");

        set.up1 = set.down1;
        set.up2 = set.down2;
        set.left1 = set.down1;
        set.left2 = set.down2;
        set.right1 = set.down1;
        set.right2 = set.down2;

        cache.put("RedSlime", set);
        return set;
    }

    public static MonsterSpriteSet getGreenBat(GamePanel gp) {
        if (cache.containsKey("GreenBat")) return cache.get("GreenBat");

        MonsterSpriteSet set = new MonsterSpriteSet();

        set.down1 = load(gp, "/monster/greenbat_down_1");
        set.down2 = load(gp, "/monster/greenbat_down_2");

        set.up1 = set.down1;
        set.up2 = set.down2;
        set.left1 = set.down1;
        set.left2 = set.down2;
        set.right1 = set.down1;
        set.right2 = set.down2;

        cache.put("GreenBat", set);
        return set;
    }

    public static MonsterSpriteSet getRedBat(GamePanel gp) {
        if (cache.containsKey("RedBat")) return cache.get("RedBat");

        MonsterSpriteSet set = new MonsterSpriteSet();

        set.down1 = load(gp, "/monster/redbat_down_1");
        set.down2 = load(gp, "/monster/redbat_down_2");

        set.up1 = set.down1;
        set.up2 = set.down2;
        set.left1 = set.down1;
        set.left2 = set.down2;
        set.right1 = set.down1;
        set.right2 = set.down2;

        cache.put("RedBat", set);
        return set;
    }
}
