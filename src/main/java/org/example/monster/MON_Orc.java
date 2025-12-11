package org.example.monster;

import java.util.List;
import org.example.main.GamePanel;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;

public class MON_Orc extends Monster {

    public MON_Orc(GamePanel gp) {
        super(gp);
        type = type_monster;
        name = "Orc";
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 8;
        life = maxLife;
        attack = 8;
        defense = 2;
        exp = 8;
        knockBackPower = 5;

        solidArea.x = 4;
        solidArea.y = 4;
        solidArea.width = 40;
        solidArea.height = 44;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        attackArea.width = 48;
        attackArea.height = 48;
        motion1_duration = 40;
        motion2_duration = 85;

        getImage();
        getAttackImage();
    }

    public void getImage() {
        up1 = setup("/monster/orc_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/orc_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/orc_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/orc_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/orc_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/orc_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/orc_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/orc_right_2", gp.tileSize, gp.tileSize);
    }

    public void getAttackImage() {
        attackUp1 = setup(
            "/monster/orc_attack_up_1",
            gp.tileSize,
            gp.tileSize * 2
        );
        attackUp2 = setup(
            "/monster/orc_attack_up_2",
            gp.tileSize,
            gp.tileSize * 2
        );
        attackDown1 = setup(
            "/monster/orc_attack_down_1",
            gp.tileSize,
            gp.tileSize * 2
        );
        attackDown2 = setup(
            "/monster/orc_attack_down_2",
            gp.tileSize,
            gp.tileSize * 2
        );
        attackLeft1 = setup(
            "/monster/orc_attack_left_1",
            gp.tileSize * 2,
            gp.tileSize
        );
        attackLeft2 = setup(
            "/monster/orc_attack_left_2",
            gp.tileSize * 2,
            gp.tileSize
        );
        attackRight1 = setup(
            "/monster/orc_attack_right_1",
            gp.tileSize * 2,
            gp.tileSize
        );
        attackRight2 = setup(
            "/monster/orc_attack_right_2",
            gp.tileSize * 2,
            gp.tileSize
        );
    }

    @Override
    public void setDialogue() {
        // No dialogue defined for Orc
    }

    @Override
    public void setAction() {
        // If no strategy is assigned yet, set ChasePlayerStrategy
        if (
            !(getMovementStrategy() instanceof
                    org.example.entity.ChasePlayerStrategy)
        ) {
            // Parameters: chaseDistance, stopDistance, rate, randomInterval
            // Example: chaseDistance=5 tiles, stopDistance=15 tiles, rate=100, randomInterval=120
            setMovementStrategy(
                new org.example.entity.ChasePlayerStrategy(5, 15, 100, 120)
            );
        }

        // Perform movement according to the strategy
        performMove();

        // Attack logic (only if not already attacking)
        if (!attacking) {
            checkAttackOrNot(30, gp.tileSize * 4, gp.tileSize);
        }
    }

    @Override
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
}
