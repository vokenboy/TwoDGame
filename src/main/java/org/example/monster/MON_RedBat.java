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

        MonsterSpriteSet s = MonsterSpriteFactory.getRedBat(gp);
        up1 = s.up1;
        up2 = s.up2;
        down1 = s.down1;
        down2 = s.down2;
        left1 = s.left1;
        left2 = s.left2;
        right1 = s.right1;
        right2 = s.right2;
    }

    @Override
    public void getImage() {
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
