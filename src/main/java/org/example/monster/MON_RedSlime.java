package org.example.monster;

import org.example.entity.Entity;
import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;
import org.example.object.OBJ_Rock;

import java.util.Random;

public class MON_RedSlime extends MON_Slime {

    GamePanel gp; // cuz of different package
    public MON_RedSlime(GamePanel gp) {
        super(gp);

        this.gp = gp;

        type = type_monster;
        name = "Red Slime";
        defaultSpeed = 2;
        speed = defaultSpeed;
        maxLife = 8;
        life = maxLife;
        attack = 4;
        defense = 0;
        exp = 4;
        projectile = new OBJ_Rock(gp);


        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        MonsterSpriteSet s = MonsterSpriteFactory.getRedSlime(gp);
        up1 = s.up1;
        up2 = s.up2;
        down1 = s.down1;
        down2 = s.down2;
        left1 = s.left1;
        left2 = s.left2;
        right1 = s.right1;
        right2 = s.right2;
    }

    public void getImage()
    {
    }

    @Override
    public void getAttackImage() {

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


    public void damageReaction() {
        actionLockCounter = 0;
        //direction = gp.player.direction;
        onPath = true; // gets aggro
    }
    public void checkDrop()
    {
        //CAST A DIE
        int i = new Random().nextInt(100)+1;

        //SET THE MONSTER DROP
        if(i < 50)
        {
            dropItem(new OBJ_Coin_Bronze(gp));
        }
        if(i >= 50 && i < 75)
        {
            dropItem(new OBJ_Heart(gp));
        }
        if(i >= 75 && i < 100)
        {
            dropItem(new OBJ_ManaCrystal(gp));
        }
    }

    @Override
    public void setDialogue() {

    }
}
