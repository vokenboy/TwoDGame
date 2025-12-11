package org.example.monster;

import java.util.List;
import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;
import org.example.object.OBJ_Rock;

public class MON_RedBat extends MON_Bat {

    public MON_RedBat(GamePanel gp) {
        super(gp);
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
    public void getImage() {}

    @Override
    public void getAttackImage() {
        // No special attack image yet
    }

    public void setAction() {
        if (onPath) {
            checkStopChasingOrNot(gp.player, 15, 100);
            if (
                !(getMovementStrategy() instanceof
                        org.example.entity.PathfindingStrategy)
            ) {
                setMovementStrategy(
                    new org.example.entity.PathfindingStrategy()
                );
            }
        } else {
            checkStartChasingOrNot(gp.player, 5, 100);
            if (
                !(getMovementStrategy() instanceof
                        org.example.entity.RandomMovementStrategy)
            ) {
                setMovementStrategy(
                    new org.example.entity.RandomMovementStrategy(120)
                );
            }
        }

        performMove();
    }

    @Override
    public void damageReaction() {
        actionLockCounter = 0;
    }

    @Override
    protected List<DropEntry> dropTable() {
        return List.of(
            DropEntry.of(39, () -> new OBJ_Coin_Bronze(gp)),
            DropEntry.of(69, () -> new OBJ_Heart(gp)),
            DropEntry.of(100, () -> new OBJ_ManaCrystal(gp))
        );
    }

    @Override
    public void setDialogue() {
        // No dialogue yet
    }
}
