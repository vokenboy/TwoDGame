package org.example.main;

import java.awt.*;

public class DamageNumber {
    public int damage;
    public int worldX;
    public int worldY;
    public int counter = 0;
    public int maxLife = 60;
    public boolean alive = true;

    public DamageNumber(int damage, int worldX, int worldY) {
        this.damage = damage;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void update() {
        counter++;
        worldY -= 1;

        if(counter >= maxLife) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        float alpha = 1.0f - ((float)counter / maxLife);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.8f));
        g2.setColor(Color.BLACK);
        g2.drawString(String.valueOf(damage), screenX + 2, screenY + 2);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        if(damage >= 10) {
            g2.setColor(new Color(255, 50, 50));
        } else if(damage >= 5) {
            g2.setColor(new Color(255, 150, 0));
        } else {
            g2.setColor(Color.WHITE);
        }

        g2.drawString(String.valueOf(damage), screenX, screenY);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
