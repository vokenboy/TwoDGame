package org.example.entity;

import org.example.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC_Enchanter extends Entity {
    
    public NPC_Enchanter(GamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;

        getImage();
        setDialogue();

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;

        solidAreaDefaultX = 8;
        solidAreaDefaultY = 16;
    }
    
    public void getImage() {
        up1 = blueTint(setup("/npc/oldman_up_1", gp.tileSize, gp.tileSize));
        up2 = blueTint(setup("/npc/oldman_up_2", gp.tileSize, gp.tileSize));
        down1 = blueTint(setup("/npc/oldman_down_1", gp.tileSize, gp.tileSize));
        down2 = blueTint(setup("/npc/oldman_down_2", gp.tileSize, gp.tileSize));
        left1 = blueTint(setup("/npc/oldman_left_1", gp.tileSize, gp.tileSize));
        left2 = blueTint(setup("/npc/oldman_left_2", gp.tileSize, gp.tileSize));
        right1 = blueTint(setup("/npc/oldman_right_1", gp.tileSize, gp.tileSize));
        right2 = blueTint(setup("/npc/oldman_right_2", gp.tileSize, gp.tileSize));
    }
    
    public void setDialogue() {
        dialogues[0][0] = "Greetings, adventurer.\nI'm testing volatile runes today—\nfree enchantments while it lasts!";
        dialogues[1][0] = "A fresh weave every time.\nNo coin required.";
        dialogues[2][0] = "You need to select an item first!";
        dialogues[3][0] = "That item cannot be enchanted!";
    }
    
    public void speak() {
        facePlayer();
        gp.gameState = gp.enchantState;
        gp.ui.npc = this;
    }
    
    @Override
    public void setAction() {
        // Enchanter stays still
    }

    private BufferedImage blueTint(BufferedImage source) {
        if (source == null) {
            return null;
        }
        BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                if (alpha == 0) {
                    tinted.setRGB(x, y, argb);
                    continue;
                }
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                red = Math.min(255, (int)(red * 0.6));
                green = Math.min(255, (int)(green * 0.75 + 10));
                blue = Math.min(255, (int)(blue * 1.35 + 35));

                int tintedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                tinted.setRGB(x, y, tintedPixel);
            }
        }
        return tinted;
    }
}
