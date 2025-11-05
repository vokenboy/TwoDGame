package org.example.factory;

import org.example.main.GamePanel;
import org.example.monster.*;

public class RedMonsterFactory implements MonsterFactory {

    @Override
    public Monster createSlime(GamePanel gp) {
        return new MON_RedSlime(gp);
    }

    @Override
    public Monster createBat(GamePanel gp) {
        return new MON_RedBat(gp);
    }

}
