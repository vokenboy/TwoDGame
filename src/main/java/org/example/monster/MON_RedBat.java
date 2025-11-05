package org.example.monster;

import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;
import org.example.object.OBJ_Rock;

import java.util.Random;

public class MON_RedBat extends MON_Bat {

    GamePanel gp;

    public MON_RedBat(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Red Bat";
        defaultSpeed = 5;
        speed = defaultSpeed;
        maxLife = 10;
        life = maxLife;
        attack = 8;
        defense = 1;
        exp = 4;
        projectile = new OBJ_Rock(gp);

        solidArea.x = 3;
        solidArea.y = 15;
        solidArea.width = 42;
        solidArea.height = 21;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    @Override
    public void getImage() {
        up1 = setup("/monster/redbat_down_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/redbat_down_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/redbat_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/redbat_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/redbat_down_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/redbat_down_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/redbat_down_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/redbat_down_2", gp.tileSize, gp.tileSize);
    }

    @Override
    public void getAttackImage() {
        // No special attack image yet
    }

    public void setAction() {
        if (onPath) {
            checkStopChasingOrNot(gp.player, 15, 100);
            if (!(getMovementStrategy() instanceof org.example.entity.PathfindingStrategy)) {
                setMovementStrategy(new org.example.entity.PathfindingStrategy());
            }
        } else {
            checkStartChasingOrNot(gp.player, 5, 100);
            if (!(getMovementStrategy() instanceof org.example.entity.RandomMovementStrategy)) {
                setMovementStrategy(new org.example.entity.RandomMovementStrategy(120));
            }
        }

        performMove();
    }


    @Override
    public void damageReaction() {
        actionLockCounter = 0;
    }

    @Override
    public void checkDrop() {
        int i = new Random().nextInt(100) + 1;
        if (i < 40) dropItem(new OBJ_Coin_Bronze(gp));
        else if (i < 70) dropItem(new OBJ_Heart(gp));
        else dropItem(new OBJ_ManaCrystal(gp));
    }

    @Override
    public void setDialogue() {
        // No dialogue yet
    }
}
