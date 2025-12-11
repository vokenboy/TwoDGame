package org.example.monster;

import java.util.List;
import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;

public class MON_GreenBat extends MON_Bat {

    public MON_GreenBat(GamePanel gp) {
        super(gp);
        name = "Green Bat";
        defaultSpeed = 3;
        speed = defaultSpeed;
        maxLife = 6;
        life = maxLife;
        attack = 4;
        defense = 0;
        exp = 2;

        solidArea.x = 3;
        solidArea.y = 15;
        solidArea.width = 42;
        solidArea.height = 21;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        MonsterSpriteSet s = MonsterSpriteFactory.getGreenBat(gp);
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
            DropEntry.of(49, () -> new OBJ_Coin_Bronze(gp)),
            DropEntry.of(79, () -> new OBJ_Heart(gp)),
            DropEntry.of(100, () -> new OBJ_ManaCrystal(gp))
        );
    }

    @Override
    public void setDialogue() {
        // No dialogue yet
    }
}
