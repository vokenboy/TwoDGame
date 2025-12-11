package org.example.monster;

import java.util.List;
import org.example.entity.Entity;
import org.example.entity.MovementStrategy;
import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;
import org.example.object.OBJ_Rock;

public class MON_GreenSlime extends MON_Slime {

    public MON_GreenSlime(GamePanel gp) {
        super(gp);
        type = type_monster;
        name = "Green Slime";
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 4;
        life = maxLife;
        attack = 2;
        defense = 0;
        exp = 2;
        //projectile = new OBJ_Rock(gp);

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        MonsterSpriteSet s = MonsterSpriteFactory.getGreenSlime(gp);
        up1 = s.up1;
        up2 = s.up2;
        down1 = s.down1;
        down2 = s.down2;
        left1 = s.left1;
        left2 = s.left2;
        right1 = s.right1;
        right2 = s.right2;
    }

    public void getImage() {}

    @Override
    public void getAttackImage() {}

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

    public void damageReaction() {
        actionLockCounter = 0;
        //direction = gp.player.direction;
        onPath = true; // gets aggro
    }

    @Override
    protected List<DropEntry> dropTable() {
        return List.of(
            DropEntry.of(49, () -> new OBJ_Coin_Bronze(gp)),
            DropEntry.of(74, () -> new OBJ_Heart(gp)),
            DropEntry.of(99, () -> new OBJ_ManaCrystal(gp))
        );
    }

    @Override
    public void setDialogue() {}
}
