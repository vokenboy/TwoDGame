package org.example.object;

import org.example.entity.ContainerItem;
import org.example.entity.Entity;
import org.example.main.GamePanel;

public class OBJ_Chest extends ContainerItem {

    public static final String objName = "Chest";
    public OBJ_Chest(GamePanel gp)
    {
        super(gp, 8); // basic chest capacity

        type = type_obstacle;
        name = objName;
        image = setup("/objects/chest",gp.tileSize,gp.tileSize);
        image2 = setup("/objects/chest_opened",gp.tileSize,gp.tileSize);
        down1 = image;
        collision = true;

        solidArea.x = 4;
        solidArea.y = 16;
        solidArea.width = 40;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

    }
    public void setLoot(Entity loot)
    {
        addItem(loot);
        setDialogue();
    }
    public void setDialogue()
    {
        String lootNames = describeContents();
        dialogues[0][0] = "You obtain " + lootNames + "!";
    }
    public void interact()
    {
        if(opened == false)
        {
            gp.gameFacade.playSoundEffect(3);
            setDialogue();
            boolean addedAny = transferToInventory(gp.player);
            setDialogue();
            if(addedAny && getChildren().isEmpty())
            {
                startDialogue(this,1);
                down1 = image2;
                opened = true;
            }
            else
            {
                startDialogue(this,0);
            }
        }
        else
        {
            startDialogue(this,2);
        }
    }
}
