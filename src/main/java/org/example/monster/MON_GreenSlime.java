package org.example.monster;

import org.example.main.GamePanel;
import java.util.Random;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;

public class MON_GreenSlime extends MON_Slime {

    GamePanel gp;

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_monster;
        name = "Green Slime";
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 4;
        life = maxLife;
        attack = 2;
        defense = 0;
        exp = 2;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        MonsterSpriteSet s = MonsterSpriteFactory.getGreenSlime(gp);

        this.up1 = s.up1;
        this.up2 = s.up2;
        this.down1 = s.down1;
        this.down2 = s.down2;
        this.left1 = s.left1;
        this.left2 = s.left2;
        this.right1 = s.right1;
        this.right2 = s.right2;
    }

    @Override
    public void getImage() {}

    @Override
    public void getAttackImage() {}

    @Override
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
        onPath = true;
    }

    @Override
    public void checkDrop() {
        int i = new Random().nextInt(100) + 1;

        if (i < 50) dropItem(new OBJ_Coin_Bronze(gp));
        else if (i < 75) dropItem(new OBJ_Heart(gp));
        else dropItem(new OBJ_ManaCrystal(gp));
    }

    @Override
    public void setDialogue() {}
}
