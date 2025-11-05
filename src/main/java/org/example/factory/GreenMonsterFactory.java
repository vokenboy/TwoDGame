package org.example.factory;

import org.example.main.GamePanel;
import org.example.monster.*;

public class GreenMonsterFactory implements MonsterFactory {

    @Override
    public Monster createSlime(GamePanel gp) {
        return new MON_GreenSlime(gp);
    }

    @Override
    public Monster createBat(GamePanel gp) {
        return new MON_GreenBat(gp);
    }

}
