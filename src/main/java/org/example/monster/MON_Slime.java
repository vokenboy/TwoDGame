package org.example.monster;

import org.example.main.GamePanel;

public abstract class MON_Slime extends Monster {
    public MON_Slime(GamePanel gp) {
        super(gp);
        name = "Slime";
        defaultSpeed = 2;
        defense = 0;
        type = type_monster;
    }
}
