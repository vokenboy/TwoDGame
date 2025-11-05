package org.example.monster;

import org.example.entity.Entity;
import org.example.main.GamePanel;

public abstract class Monster extends Entity {

    protected GamePanel gp;

    public Monster(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.type = type_monster;
    }

    public abstract void getImage();
    public abstract void getAttackImage();
    public abstract void setAction();
    public abstract void damageReaction();
    public abstract void checkDrop();
    public abstract void setDialogue();
}
