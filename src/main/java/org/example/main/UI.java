package org.example.main;

import org.example.entity.Entity;
import org.example.entity.decorator.EquipmentBuilder;
import org.example.entity.decorator.ElementalDecorator;
import org.example.object.OBJ_Coin_Bronze;
import org.example.object.OBJ_Heart;
import org.example.object.OBJ_ManaCrystal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    public Font maruMonica, purisaB;
    BufferedImage heart_full, heart_half, heart_blank, crystal_full, crystal_blank, coin;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    public int titleScreenState = 0; // 0 : Main Menu, 1 : the second screen
    //Player Inventory
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    //Merchant NPC Inventory
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;

    int subState = 0;
    int counter = 0; // transition
    public Entity npc;
    int charIndex = 0;
    String combinedText = "";

      private static final String[] ENCHANT_PREFIXES = {
        "Arcane", "Mystic", "Prismatic", "Runic", "Aether", "Azure", "Celestial", "Umbral"
    };

    private static final String[] ENCHANT_SUFFIXES = {
        "Weave", "Gambit", "Edge", "Ward", "Brand", "Aegis", "Catalyst", "Resonance"
    };

    private final Random rng = new Random();
    private EnchantmentOption lastEnchantmentResult;
    private String lastEnchantedItemName = "";

    private enum OperationType {
        DAMAGE("Damage Glyph", 1),
        DEFENSE("Bulwark Sigil", 1),
        LIFESTEAL("Vampiric Rune", 2),
        CRITICAL("Assassin Sigil", 1),
        ELEMENT("Elemental Brand", 1),
        MANA("Arcane Seal", 1),
        HEALTH("Fortitude Crest", 1),
        SPEED("Tempest Script", 1);

        final String label;
        final int baseSlots;

        OperationType(String label, int baseSlots) {
            this.label = label;
            this.baseSlots = baseSlots;
        }

        int slots(ElementalDecorator.ElementType elementType) {
            if(this == ELEMENT && elementType != null) {
                return elementType.slotsRequired;
            }
            return baseSlots;
        }
    }

    private static final class EnchantOperation {
        final OperationType type;
        final int level;
        final int value;
        final ElementalDecorator.ElementType elementType;
        final String summary;

        EnchantOperation(OperationType type, int level, int value,
                         ElementalDecorator.ElementType elementType, String summary) {
            this.type = type;
            this.level = level;
            this.value = value;
            this.elementType = elementType;
            this.summary = summary;
        }
    }

    private static final class EnchantmentOption {
        final String title;
        final List<EnchantOperation> operations;
        final List<String> detailLines;

        EnchantmentOption(String title, List<EnchantOperation> operations, List<String> detailLines) {
            this.title = title;
            this.operations = operations;
            this.detailLines = detailLines;
        }

        int slotsUsed() {
            int total = 0;
            for(EnchantOperation op : operations) {
                total += op.type.slots(op.elementType);
            }
            return total;
        }
    }

    public UI(GamePanel gp)
    {
        this.gp = gp;
        try
        {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
            purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch (FontFormatException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
        Entity crystal = new OBJ_ManaCrystal(gp);
        crystal_full = crystal.image;
        crystal_blank = crystal.image2;
        Entity bronzeCoin = new OBJ_Coin_Bronze(gp);
        coin = bronzeCoin.down1;
    }
    public void drawPauseScreen()
    {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,80F));
        String text = "GAME PAUSED";
        int x = getXforCenteredText(text);
        int y = gp.screenHeight / 2;
        g2.drawString(text,x,y);

    }
    public void drawDialogueScreen()
    {
        // WINDOW
        int x = gp.tileSize * 3;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 6);
        int height = gp.tileSize * 4;

        drawSubWindow(x,y,width,height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,28F));
        x += gp.tileSize;
        y += gp.tileSize;

        if(npc.dialogues[npc.dialogueSet][npc.dialogueIndex] != null)
        {
            //currentDialogue = npc.dialogues[npc.dialogueSet][npc.dialogueIndex];//For display text once, enable this and disable letter by letter.(Letter by letter: The if statement below there)

            char characters[] = npc.dialogues[npc.dialogueSet][npc.dialogueIndex].toCharArray();

            if(charIndex < characters.length)
            {
                gp.gameFacade.playSoundEffect(17);//Speak sound
                String s = String.valueOf(characters[charIndex]);
                combinedText = combinedText + s; //every loop add one character to combinedText
                currentDialogue = combinedText;

                charIndex++;
            }
            if(gp.keyH.enterPressed == true)
            {
                charIndex = 0;
                combinedText = "";
                if(gp.gameState == gp.dialogueState || gp.gameState == gp.cutsceneState)
                {
                    npc.dialogueIndex++;
                    gp.keyH.enterPressed = false;
                }
            }
        }
        else //If no text is in the array
        {
            npc.dialogueIndex = 0;
            if(gp.gameState == gp.dialogueState)
            {
                gp.gameState = gp.playState;
            }
            if(gp.gameState == gp.cutsceneState)
            {
                gp.csManager.scenePhase++;
            }
        }


        for(String line : currentDialogue.split("\n"))   // splits dialogue until "\n" as a line
        {
            g2.drawString(line,x,y);
            y += 40;
        }

    }
    public void  drawCharacterScreen()
    {
        // CREATE A FRAME
        final int frameX = gp.tileSize * 2;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 5;
        final int frameHeight = gp.tileSize *10;
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        // TEXT
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        int textX = frameX + 20;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 35;

        // NAMES
        g2.drawString("Level", textX,textY);
        textY += lineHeight;
        g2.drawString("Life", textX,textY);
        textY += lineHeight;
        g2.drawString("Mana", textX,textY);
        textY += lineHeight;
        g2.drawString("Strength", textX,textY);
        textY += lineHeight;
        g2.drawString("Dexterity", textX,textY);
        textY += lineHeight;
        g2.drawString("Attack", textX,textY);
        textY += lineHeight;
        g2.drawString("Defence", textX,textY);
        textY += lineHeight;
        g2.drawString("Exp", textX,textY);
        textY += lineHeight;
        g2.drawString("Next Level", textX,textY);
        textY += lineHeight;
        g2.drawString("Coin", textX,textY);
        textY += lineHeight + 10;
        g2.drawString("Weapon", textX,textY);
        textY += lineHeight + 15;
        g2.drawString("Shield", textX,textY);


        // VALUES
        int tailX = (frameX + frameWidth) - 30;
        // Reset textY
        textY = frameY + gp.tileSize;
        String value;

        value = String.valueOf(gp.player.level);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.mana + "/" + gp.player.maxMana);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.strength);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.dexterity);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.attack);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.defense);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLevelExp);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = getXforAlignToRight(value,tailX);
        g2.drawString(value,textX,textY);
        textY += lineHeight;

        g2.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize + 5, textY - 24, null);
        textY += gp.tileSize;
        if(gp.player.currentShield!= null)
        {
            g2.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize + 5, textY - 24, null);
        }
    }
    public void drawInventory(Entity entity, boolean cursor)
    {
        int frameX = 0;
        int frameY = 0;
        int frameWidth = 0;
        int frameHeight = 0;
        int slotCol = 0;
        int slotRow = 0;
        if(entity == gp.player)
        {
            //FRAME
            frameX = gp.tileSize * 12;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        }
        else
        {
            //FRAME
            frameX = gp.tileSize * 2;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize * 6;
            frameHeight = gp.tileSize * 5;
            slotCol = npcSlotCol;
            slotRow = npcSlotRow;
        }


        //DRAW FRAME
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        //SLOT
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;


        //DRAW PLAYER'S ITEMS
        for(int i = 0; i < entity.inventory.size(); i++)
        {

            //EQUIP CURSOR
            if(entity.inventory.get(i) == entity.currentWeapon ||
                    entity.inventory.get(i) == entity.currentShield || entity.inventory.get(i) == entity.currentLight)
            {
                g2.setColor(new Color(240,190,90));
                g2.fillRoundRect(slotX,slotY, gp.tileSize, gp.tileSize,10,10 );
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX,slotY,null);  //draw item

            //DISPLAY AMOUNT
            if(entity == gp.player && entity.inventory.get(i).amount > 1)  //merchant npc's inventory cannot stack items
            {
                g2.setFont(g2.getFont().deriveFont(32f));
                int amountX;
                int amountY;

                String s = "" + entity.inventory.get(i).amount;
                amountX = getXforAlignToRight(s, slotX + 44);
                amountY = slotY + gp.tileSize;

                //SHADOW
                g2.setColor(new Color(60,60,60));
                g2.drawString(s,amountX,amountY);
                //NUMBER
                g2.setColor(Color.white);
                g2.drawString(s,amountX-3,amountY-3);

            }

            slotX += slotSize;

            if(i == 4 || i == 9 || i == 14)
            {
                //reset slotX
                slotX = slotXstart;
                //next row
                slotY += slotSize;
            }
        }

        //CURSOR
        if(cursor == true)
        {
            int cursorX = slotXstart + (slotSize * slotCol);
            int cursorY = slotYstart + (slotSize * slotRow);
            int cursorWidth = gp.tileSize;
            int cursorHeight = gp.tileSize;

            //DRAW CURSOR
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX,cursorY,cursorWidth,cursorHeight,10,10);

            //DESCRIPTION FRAME
            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = frameWidth;
            int dFrameHeight = gp.tileSize * 3;

            //DRAW DESCRIPTION TEXT
            int textX = dFrameX + 20;
            int textY = dFrameY + gp.tileSize;
            g2.setFont(g2.getFont().deriveFont(28F));

            int itemIndex = getItemIndexOnSlot(slotCol, slotRow);
            if(itemIndex < entity.inventory.size())
            {
                drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
                for(String line : entity.inventory.get(itemIndex).description.split("\n"))
                {
                    g2.drawString(line,textX,textY);
                    textY += 32;
                }
            }
        }
    }
    public void drawTransition()
    {
        counter++;
        g2.setColor(new Color(0,0,0,counter*5));
        g2.fillRect(0,0,gp.screenWidth2,gp.screenHeight2); // screen gets darker

        if(counter == 50) //the transition is done
        {
            counter = 0;
            gp.gameState = gp.playState;
            gp.player.worldX =  gp.tileSize * gp.eHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
            gp.currentMap = gp.eHandler.tempMap;
            gp.eHandler.previousEventX = gp.player.worldX;
            gp.eHandler.previousEventY = gp.player.worldY;
            gp.changeArea();
        }
    }
    public void drawTradeScreen()
    {
        switch(subState)
        {
            case 0: trade_select(); break;
            case 1: trade_buy(); break;
            case 2: trade_sell(); break;
        }
        gp.keyH.enterPressed = false;
    }
    public void trade_select()
    {
        npc.dialogueSet = 0;
        drawDialogueScreen();

        //DRAW WINDOW
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize *3;
        int height = (int)(gp.tileSize*3.5);
        drawSubWindow(x,y,width,height);

        //DRAW TEXTS
        x += gp.tileSize;
        y += gp.tileSize;
        g2.drawString("Buy",x,y);
        if(commandNum == 0)
        {
            g2.drawString(">", x-24,y);
            if(gp.keyH.enterPressed == true)
            {
                subState = 1;
            }
        }
        y += gp.tileSize;
        g2.drawString("Sell",x,y);
        if(commandNum == 1)
        {
            g2.drawString(">", x-24,y);
            if(gp.keyH.enterPressed == true)
            {
                subState = 2;
            }
        }
        y += gp.tileSize;
        g2.drawString("Leave",x,y);
        if(commandNum == 2)
        {
            g2.drawString(">", x-24,y);
            if(gp.keyH.enterPressed == true)
            {
                //leave trade
                commandNum = 0;
                npc.startDialogue(npc,1);
            }
        }
    }
    public void trade_buy()
    {
        // DRAW PLAYER INVENTORY
        drawInventory(gp.player, false); // i want to move cursor on merchant's inventory so cursor = false.
        // DRAW PLAYER INVENTORY
        drawInventory(npc, true);

        // DRAW HINT WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize * 9;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        drawSubWindow(x,y,width,height);
        g2.drawString("[ESC] Back", x+24,y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 12;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x,y,width,height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24,y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(npcSlotCol,npcSlotRow);
        if(itemIndex < npc.inventory.size())
        {
            x = (int)(gp.tileSize * 5.5);
            y = (int)(gp.tileSize * 5.5);
            width = (int)(gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x,y,width,height);
            g2.drawImage(coin, x+10, y+8, 32,32,null );

            int price = npc.inventory.get(itemIndex).price;
            String text = String.valueOf(price);
            x = getXforAlignToRight(text,(gp.tileSize * 8) - 20);
            g2.drawString(text, x, y+34);

            //BUY AN ITEM
            if(gp.keyH.enterPressed == true)
            {
                if(npc.inventory.get(itemIndex).price > gp.player.coin) //not enough coin
                {
                    subState = 0;
                    npc.startDialogue(npc,2);
                }
                else
                {
                    if(gp.player.canObtainItem(npc.inventory.get(itemIndex)) == true)
                    {
                        gp.player.coin -= npc.inventory.get(itemIndex).price;  //-price
                    }
                    else
                    {
                        subState = 0;
                        npc.startDialogue(npc,3);
                    }
                }
//                else if(gp.player.inventory.size() == gp.player.maxInventorySize) //full inventory
//                {
//                    subState = 0;
//                    gp.gameState = gp.dialogueState;
//                    currentDialogue = "You can not carry any more!";
//                }
//                else
//                {
//                    gp.player.coin -= npc.inventory.get(itemIndex).price;  //-price
//                    gp.player.inventory.add(npc.inventory.get(itemIndex)); //add item to player's inventory
//                }
            }
        }
    }
    public void trade_sell()
    {
        //DRAW PLAYER INVENTORY
        drawInventory(gp.player, true);
        int x;
        int y;
        int width;
        int height;

        // DRAW HINT WINDOW
        x = gp.tileSize * 2;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x,y,width,height);
        g2.drawString("[ESC] Back", x+24,y+60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 12;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x,y,width,height);
        g2.drawString("Your Coin: " + gp.player.coin, x+24,y+60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSlot(playerSlotCol,playerSlotRow);
        if(itemIndex < gp.player.inventory.size())
        {
            x = (int)(gp.tileSize * 15.5);
            y = (int)(gp.tileSize * 5.5);
            width = (int)(gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x,y,width,height);
            g2.drawImage(coin, x+10, y+8, 32,32,null );

            int price = gp.player.inventory.get(itemIndex).price / 2;
            String text = String.valueOf(price);
            x = getXforAlignToRight(text,(gp.tileSize * 18) - 20);
            g2.drawString(text, x, y+34);

            //SELL AN ITEM
            if(gp.keyH.enterPressed == true)
            {
                if(gp.player.inventory.get(itemIndex) == gp.player.currentWeapon ||
                        gp.player.inventory.get(itemIndex) == gp.player.currentShield) //equipped items cant sell
                {
                    commandNum = 0;
                    subState = 0;
                    npc.startDialogue(npc,4);
                }
                else
                {
                    if(gp.player.inventory.get(itemIndex).amount > 1)
                    {
                        gp.player.inventory.get(itemIndex).amount--;
                    }
                    else
                    {
                        gp.player.inventory.remove(itemIndex);
                    }
                    gp.player.coin += price;
                }
            }
        }

    }
    public void drawSleepScreen()
    {
        counter++;
        if(counter < 120)
        {
            gp.eManager.lighting.filterAlpha += 0.01f;
            if(gp.eManager.lighting.filterAlpha > 1f)
            {
                gp.eManager.lighting.filterAlpha = 1f;
            }
        }
        if(counter >= 120)
        {
            gp.eManager.lighting.filterAlpha -= 0.01f;
            if(gp.eManager.lighting.filterAlpha <= 0f)
            {
                gp.eManager.lighting.filterAlpha = 0f;
                counter = 0;
                gp.eManager.lighting.dayState = gp.eManager.lighting.day;
                gp.eManager.lighting.dayCounter = 0;
                gp.gameState = gp.playState;
                gp.player.getImage();
            }
        }
    }
    public void drawEnchantScreen()
    {
        switch(subState)
        {
            case 0 -> enchant_select();
            case 1 -> enchant_selectItem();
            default -> enchant_select();
        }
        gp.keyH.enterPressed = false;
    }
    public void enchant_select()
    {
        npc.dialogueSet = 0;
        drawDialogueScreen();

        //DRAW WINDOW
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize * 3;
        int height = (int)(gp.tileSize * 3.5);
        drawSubWindow(x, y, width, height);

        //DRAW TEXTS
        x += gp.tileSize;
        y += gp.tileSize;
        g2.drawString("Enchant", x, y);
        if(commandNum == 0)
        {
            g2.drawString(">", x - 24, y);
            if(gp.keyH.enterPressed == true)
            {
                subState = 1;
            }
        }
        y += gp.tileSize;
        g2.drawString("Leave", x, y);
        if(commandNum == 1)
        {
            g2.drawString(">", x - 24, y);
            if(gp.keyH.enterPressed == true)
            {
                commandNum = 0;
                npc.startDialogue(npc, 1);
            }
        }
    }
    public void enchant_selectItem()
    {
        drawInventory(gp.player, true);

        int x = gp.tileSize * 2;
        int y = gp.tileSize * 9;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("[ESC] Back", x + 24, y + 50);
        g2.drawString("Press [ENTER] to infuse a random enchant.", x + 24, y + 80);

        if(lastEnchantmentResult != null)
        {
            x = gp.tileSize * 11;
            y = gp.tileSize * 5;
            width = gp.tileSize * 7;
            height = gp.tileSize * 4;
            drawSubWindow(x, y, width, height);
            int textX = x + 24;
            int textY = y + 36;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
            g2.drawString("Last Enchant", textX, textY);
            textY += 24;
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16F));
            g2.drawString(lastEnchantedItemName, textX, textY);
            textY += 22;
            g2.drawString(lastEnchantmentResult.title, textX, textY);
            textY += 22;
            for(String line : lastEnchantmentResult.detailLines)
            {
                g2.drawString(line, textX, textY);
                textY += 18;
                if(textY > y + height - 16)
                {
                    break;
                }
            }
        }

        int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
        if(itemIndex < gp.player.inventory.size() && gp.keyH.enterPressed)
        {
            Entity selectedItem = gp.player.inventory.get(itemIndex);
            if(isEnchantable(selectedItem))
            {
                performRandomEnchant(itemIndex, selectedItem);
            }
            else
            {
                subState = 0;
                npc.startDialogue(npc, 3);
            }
            gp.keyH.enterPressed = false;
        }
    }

    private void performRandomEnchant(int itemIndex, Entity selectedItem)
    {
        Entity baseItem = selectedItem.originalItem != null ? selectedItem.originalItem : selectedItem;
        EnchantmentOption option = generateRandomOption(baseItem);
        if(option == null)
        {
            addMessage("The runes fall silent. Try again.");
            return;
        }

        Entity enchantedItem = buildEnchantedItem(baseItem, option);
        if(enchantedItem == null)
        {
            addMessage("The ritual fizzled. Try again.");
            return;
        }

        gp.player.inventory.set(itemIndex, enchantedItem);

        if(selectedItem == gp.player.currentWeapon) {
            gp.player.equipWeapon(enchantedItem);
            gp.player.attack = gp.player.getAttack();
        }
        if(selectedItem == gp.player.currentShield) {
            gp.player.equipShield(enchantedItem);
            gp.player.defense = gp.player.getDefense();
        }

        lastEnchantmentResult = option;
        lastEnchantedItemName = enchantedItem.name;

        gp.gameFacade.playSoundEffect(9);
        addMessage(option.title + " unleashed!");
        int detailLinesToShow = Math.min(3, option.detailLines.size());
        for(int i = 0; i < detailLinesToShow; i++) {
            addMessage(option.detailLines.get(i));
        }
    }

    private boolean isEnchantable(Entity item) {
        if(item == null) {
            return false;
        }
        int type = item.type;
        return item.attackValue > 0 ||
               type == gp.player.type_sword ||
               type == gp.player.type_axe ||
               type == gp.player.type_pickaxe ||
               type == gp.player.type_shield;
    }

    private EnchantmentOption generateRandomOption(Entity baseItem)
    {
        if(baseItem == null) {
            return null;
        }
        int availableSlots = Math.max(1, baseItem.enchantmentSlots - baseItem.usedEnchantmentSlots);
        boolean baseHasElement = hasElementalMark(baseItem);
        EnchantmentOption option = createRandomOption(baseItem, availableSlots, baseHasElement);
        if(option != null)
        {
            option.detailLines.add(String.format("Slots used: %d/%d", option.slotsUsed(), baseItem.enchantmentSlots));
        }
        return option;
    }

    private boolean hasElementalMark(Entity item)
    {
        if(item.appliedEnchantments == null) {
            return false;
        }
        for(String tag : item.appliedEnchantments) {
            if(tag != null && tag.startsWith("ELEMENT_")) {
                return true;
            }
        }
        return false;
    }

    private EnchantmentOption createRandomOption(Entity baseItem, int maxSlots, boolean baseHasElement)
    {
        List<EnchantOperation> operations = new ArrayList<>();
        int remaining = Math.max(1, maxSlots);
        boolean elementAdded = baseHasElement;
        int attempts = 0;

        while(remaining > 0 && operations.size() < 4 && attempts < 10) {
            EnchantOperation op = createRandomOperation(baseItem, remaining, !elementAdded);
            if(op == null) {
                attempts++;
                continue;
            }
            operations.add(op);
            remaining -= op.type.slots(op.elementType);
            if(op.type == OperationType.ELEMENT) {
                elementAdded = true;
            }
            if(remaining <= 0 || rng.nextDouble() < 0.35) {
                break;
            }
        }

        if(operations.isEmpty()) {
            operations.add(createOperation(OperationType.DAMAGE, baseItem,
                    Math.max(1, Math.min(3, 1 + rng.nextInt(3))), null));
        }

        List<String> lines = new ArrayList<>();
        for(EnchantOperation op : operations) {
            lines.add(op.summary);
        }

        String title = buildOptionTitle(operations);
        return new EnchantmentOption(title, operations, lines);
    }

    private EnchantOperation createRandomOperation(Entity baseItem, int remainingSlots, boolean allowElement)
    {
        List<OperationType> pool = new ArrayList<>();
        for(OperationType type : OperationType.values()) {
            if(type == OperationType.ELEMENT && !allowElement) {
                continue;
            }
            int slotCost = type == OperationType.ELEMENT ? 1 : type.baseSlots;
            if(type == OperationType.LIFESTEAL && remainingSlots < 2) {
                continue;
            }
            if(type != OperationType.LIFESTEAL && remainingSlots < slotCost) {
                continue;
            }
            pool.add(type);
        }
        if(pool.isEmpty()) {
            return null;
        }

        OperationType chosen = pool.get(rng.nextInt(pool.size()));
        ElementalDecorator.ElementType elementType = null;
        if(chosen == OperationType.ELEMENT) {
            List<ElementalDecorator.ElementType> elements = new ArrayList<>();
            for(ElementalDecorator.ElementType element : ElementalDecorator.ElementType.values()) {
                if(element.slotsRequired <= remainingSlots) {
                    elements.add(element);
                }
            }
            if(elements.isEmpty()) {
                return null;
            }
            elementType = elements.get(rng.nextInt(elements.size()));
        }

        int baseLevel = 1 + rng.nextInt(3);
        if(rng.nextDouble() < 0.3) {
            baseLevel++;
        }
        int level = Math.min(5, baseLevel);
        if(chosen == OperationType.ELEMENT) {
            level = Math.min(3, level);
        }

        return createOperation(chosen, baseItem, level, elementType);
    }

    private EnchantOperation createOperation(OperationType type, Entity baseItem, int level, ElementalDecorator.ElementType elementType)
    {
        int value = computeMagnitude(baseItem, type, level, elementType);
        String summary = buildSummary(type, level, value, elementType);
        return new EnchantOperation(type, level, value, elementType, summary);
    }

    private int computeMagnitude(Entity baseItem, OperationType type, int level, ElementalDecorator.ElementType elementType)
    {
        return switch(type) {
            case DAMAGE -> {
                int baseAttack = Math.max(2, baseItem.attackValue);
                yield Math.max(2, baseAttack / 3 + level * 3 + rng.nextInt(4));
            }
            case DEFENSE -> {
                int baseDefense = Math.max(1, baseItem.defenseValue);
                yield Math.max(2, baseDefense / 2 + level * 2 + rng.nextInt(3));
            }
            case LIFESTEAL -> 8 + level * 4 + rng.nextInt(6);
            case CRITICAL -> Math.min(70, 12 + level * 6 + rng.nextInt(8));
            case MANA -> 6 + level * 3 + rng.nextInt(5);
            case HEALTH -> 10 + level * 4 + rng.nextInt(6);
            case SPEED -> 1 + level + (rng.nextDouble() < 0.35 ? 1 : 0);
            case ELEMENT -> 0;
        };
    }

    private String buildSummary(OperationType type, int level, int value, ElementalDecorator.ElementType elementType)
    {
        String tier = "T" + level;
        return switch(type) {
            case DAMAGE -> String.format("%s %s (+%d ATK)", type.label, tier, value);
            case DEFENSE -> String.format("%s %s (+%d DEF)", type.label, tier, value);
            case LIFESTEAL -> String.format("%s %s (+%d%% leech)", type.label, tier, value);
            case CRITICAL -> String.format("%s %s (+%d%% crit)", type.label, tier, value);
            case MANA -> String.format("%s %s (+%d mana)", type.label, tier, value);
            case HEALTH -> String.format("%s %s (+%d HP)", type.label, tier, value);
            case SPEED -> String.format("%s %s (+%d SPD)", type.label, tier, value);
            case ELEMENT -> String.format("%s %s (%s)", type.label, tier, formatElement(elementType));
        };
    }

    private String formatElement(ElementalDecorator.ElementType elementType)
    {
        if(elementType == null) {
            return "Element";
        }
        String name = elementType.name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String buildOptionTitle(List<EnchantOperation> operations)
    {
        String prefix = ENCHANT_PREFIXES[rng.nextInt(ENCHANT_PREFIXES.length)];
        String suffix = ENCHANT_SUFFIXES[rng.nextInt(ENCHANT_SUFFIXES.length)];
        List<String> highlights = new ArrayList<>();
        for(EnchantOperation op : operations) {
            String highlight = op.type == OperationType.ELEMENT && op.elementType != null
                ? formatElement(op.elementType)
                : op.type.label.split(" ")[0];
            if(!highlights.contains(highlight)) {
                highlights.add(highlight);
            }
            if(highlights.size() == 2) {
                break;
            }
        }
        String focus = highlights.isEmpty() ? "" : " (" + String.join(" & ", highlights) + ")";
        return prefix + " " + suffix + focus;
    }

    private Entity buildEnchantedItem(Entity baseItem, EnchantmentOption option)
    {
        if(baseItem == null || option == null) {
            return null;
        }
        EquipmentBuilder builder = EquipmentBuilder.create(baseItem)
            .withSlots(Math.max(baseItem.enchantmentSlots, option.slotsUsed()))
            .withBudget(Integer.MAX_VALUE)
            .enableAutoOptimize();

        for(EnchantOperation op : option.operations) {
            switch(op.type) {
                case DAMAGE -> builder.withDamageBuff(op.value, op.level);
                case DEFENSE -> builder.withDefenseBuff(op.value, op.level);
                case LIFESTEAL -> builder.withLifeSteal(op.value, op.level);
                case CRITICAL -> builder.withCriticalChance(op.value, op.level);
                case ELEMENT -> builder.withElement(op.elementType, op.level);
                case MANA -> builder.withMana(op.value, op.level);
                case HEALTH -> builder.withHealth(op.value, op.level);
                case SPEED -> builder.withSpeed(op.value, op.level);
            }
        }

        return builder.build();
    }
    public int getItemIndexOnSlot(int slotCol, int slotRow)
    {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }
    public void drawPlayerLife()
    {
        //gp.player.life = 5;
        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;
        int iconSize = 32;
        int manaStartX = (gp.tileSize/2) - 5;
        int manaStartY = 0;

        //DRAW MAX LIFE (BLANK)
        while(i < gp.player.maxLife/2)
        {
            g2.drawImage(heart_blank, x, y, iconSize, iconSize, null);
            i++;
            x += iconSize;
            manaStartY = y + 32;

            if(i % 8 == 0)
            {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }
        //reset
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;
        //DRAW CURRENT HEART // ITS LIKE COLORING THE BLANK HEARTS
        while(i < gp.player.life)
        {
            g2.drawImage(heart_half,x,y,iconSize, iconSize, null);
            i++;
            if(i < gp.player.life)
            {
                g2.drawImage(heart_full,x,y,iconSize, iconSize, null);
            }
            i++;
            x += iconSize;

            if(i % 16 == 0)
            {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }

        //DRAW MAX MANA (BLANK)
        x = manaStartX;
        y = manaStartY;
        i = 0;
        while(i < gp.player.maxMana)
        {
            g2.drawImage(crystal_blank,x,y, iconSize, iconSize, null);
            i++;
            x += 20;

            if(i % 10 == 0)
            {
                x = manaStartX;
                y += iconSize;
            }
        }
        //reset
        x = manaStartX;
        y = manaStartY;
        i = 0;
        //DRAW MANA
        while(i < gp.player.mana)
        {
            g2.drawImage(crystal_full,x,y,iconSize,iconSize,null);
            i++;
            x += 20;
            if(i % 10 == 0)
            {
                x = manaStartX;
                y += iconSize;
            }
        }
    }
    public void drawMonsterLife()
    {
        //Monster HP Bar
        for(int i = 0; i < gp.monster[1].length; i++)
        {
            Entity monster = gp.monster[gp.currentMap][i];

            if(monster != null && monster.inCamera() == true)
            {
                if(monster.hpBarOn == true && monster.boss == false)
                {
                    double oneScale = (double)gp.tileSize/monster.maxLife; // (bar lenght / maxlife) Ex: if monster hp = 2, tilesize = 48px. So, 1 hp = 24px
                    double hpBarValue = oneScale * monster.life;

                    if(hpBarValue < 0) //Ex: You attack 5 hp to monster which has 3 hp. Monster's hp will be -2 and bar will ofset to left. To avoid that check if hpBarValue less than 0.
                    {
                        hpBarValue = 0;
                    }

                    g2.setColor(new Color(35,35,35));
                    g2.fillRect(monster.getScreenX()-1,monster.getScreenY()-16,gp.tileSize+2,12);

                    g2.setColor(new Color(255,0,30));
                    g2.fillRect(monster.getScreenX(),monster.getScreenY() - 15, (int)hpBarValue,10);

                    monster.hpBarCounter++;
                    if(monster.hpBarCounter > 600)  // 10
                    {
                        monster.hpBarCounter = 0;
                        monster.hpBarOn = false;
                    }
                }
                else if(monster.boss == true)
                {
                    double oneScale = (double)gp.tileSize*8/monster.maxLife; // (bar lenght / maxlife) Ex: if monster hp = 2, tilesize = 48px. So, 1 hp = 24px
                    double hpBarValue = oneScale * monster.life;
                    int x = gp.screenWidth/2 - gp.tileSize*4;
                    int y = gp.tileSize * 10;

                    if(hpBarValue < 0)  //Ex: You attack 5 hp to monster which has 3 hp. Monster's hp will be -2 and bar will ofset to left. To avoid that check if hpBarValue less than 0.
                    {
                        hpBarValue = 0;
                    }

                    g2.setColor(new Color(35,35,35));
                    g2.fillRect(x-1,y-1,gp.tileSize*8 + 2,22);

                    g2.setColor(new Color(255,0,30));
                    g2.fillRect(x,y, (int)hpBarValue,20);

                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24f));
                    g2.setColor(Color.white);
                    g2.drawString(monster.name, x+4, y-10);
                }
            }
        }

    }
    public void drawMessage()
    {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,24F));

        for(int i = 0; i < message.size(); i++)
        {
            if(message.get(i) != null)
            {
                //Shadow
                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX+2,messageY+2);
                //Text
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX,messageY);

                int counter = messageCounter.get(i) + 1; //messageCounter++
                messageCounter.set(i,counter);           //set the counter to the array
                messageY += 50;

                if(messageCounter.get(i) > 150)          //display 2.5 seconds
                {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }

        }
    }
    public void drawTitleScreen()
    {
        g2.setColor(new Color(0,0,0));             // FILL BACKGROUND BLACK
        g2.fillRect(0,0, gp.screenWidth, gp.screenHeight);
        //MAIN MENU
        if(titleScreenState == 0)
        {

            //TITLE NAME
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Blue Boy Adventure\n";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            //SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text,x+5,y+5);
            //MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            //BLUE BOY IMAGE
            x = gp.screenWidth/2 - (gp.tileSize * 2) / 2;
            y += gp.tileSize*2;
            g2.drawImage(gp.player.down1,x,y,gp.tileSize*2,gp.tileSize*2,null);

            //MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            text = "NEW GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3.5;
            g2.drawString(text,x,y);
            if(commandNum == 0)
            {
                g2.drawString(">",x - gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 1)
            {
                g2.drawString(">",x - gp.tileSize, y);
            }

            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 2)
            {
                g2.drawString(">",x - gp.tileSize, y);
            }
        }
        //SECOND SCREEN
        else if(titleScreenState == 1)
        {

            //CLASS SELECTION SCREEN
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text,x,y);

            text = "Fighter";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3;
            g2.drawString(text,x,y);
            if(commandNum == 0)
            {
                g2.drawString(">",x-gp.tileSize,y);
            }

            text = "Thief";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 1)
            {
                g2.drawString(">",x-gp.tileSize,y);
            }

            text = "Sorcerer";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text,x,y);
            if(commandNum == 2)
            {
                g2.drawString(">",x-gp.tileSize,y);
            }

            text = "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text,x,y);
            if(commandNum == 3)
            {
                g2.drawString(">",x-gp.tileSize,y);
            }
        }
    }
    public void drawGameOverScreen()
    {
        g2.setColor(new Color(0,0,0,150)); //Half-black
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,110f));
        text = "Game Over";

        //Shadow
        g2.setColor(Color.BLACK);
        x = getXforCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text,x,y);
        //Text
        g2.setColor(Color.white);
        g2.drawString(text,x-4,y-4);

        //RETRY
        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text,x,y);
        if(commandNum == 0)
        {
            g2.drawString(">", x-40, y);
        }

        //BACK TO THE TITLE SCREEN
        text = "Quit";
        x = getXforCenteredText(text);
        y += 55;
        g2.drawString(text,x,y);
        if(commandNum == 1)
        {
            g2.drawString(">", x-40, y);
        }

    }
    public void drawOptionsScreen()
    {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW

        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;

        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        switch(subState)
        {
            case 0: options_top(frameX,frameY); break;
            case 1: options_fullScreenNotification(frameX,frameY); break;
            case 2: options_control(frameX,frameY); break;
            case 3: options_endGameConfirmation(frameX,frameY);

        }
        gp.keyH.enterPressed = false;
    }
    public void options_top(int frameX, int frameY)
    {
        int textX;
        int textY;

        //TITLE
        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text,textX,textY);

        //FULL SCREEN ON/OFF
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        g2.drawString("Full Screen", textX, textY);
        if(commandNum == 0)
        {
            g2.drawString(">", textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                if(gp.fullScreenOn == false)
                {
                    gp.fullScreenOn = true;
                }
                else if(gp.fullScreenOn == true)
                {
                    gp.fullScreenOn = false;
                }
                subState = 1;
            }
        }

        //MUSIC
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if(commandNum == 1)
        {
            g2.drawString(">", textX-25,textY);
        }

        //SE
        textY += gp.tileSize;
        g2.drawString("SE", textX, textY);
        if(commandNum == 2)
        {
            g2.drawString(">", textX-25,textY);
        }

        //CONTROLS
        textY += gp.tileSize;
        g2.drawString("Controls", textX, textY);
        if(commandNum == 3)
        {
            g2.drawString(">", textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 2;
                commandNum = 0;
            }
        }

        //END GAME
        textY += gp.tileSize;
        g2.drawString("End Game", textX, textY);
        if(commandNum == 4)
        {
            g2.drawString(">", textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 3;
                commandNum = 0;
            }
        }

        //BACK
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if(commandNum == 5)
        {
            g2.drawString(">", textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                gp.gameState = gp.playState;
                commandNum = 0;
            }
        }

        //FULL SCREEN CHECK BOX
        textX = frameX + (int)(gp.tileSize * 4.5);
        textY = frameY + gp.tileSize * 2 + 24;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX,textY,24,24);
        if(gp.fullScreenOn == true)
        {
            g2.fillRect(textX,textY,24,24);
        }

        //MUSIC VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX,textY,120, 24); //120/5 = 24px = 1 scale
        int volumeWidth = 24 * gp.music.volumeScale;
        g2.fillRect(textX,textY,volumeWidth,24);

        //SE VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX,textY,120, 24);
        volumeWidth = 24 * gp.se.volumeScale;
        g2.fillRect(textX,textY,volumeWidth,24);

        //SAVE OPTIONS
        gp.config.saveConfig();
    }
    public void options_fullScreenNotification(int frameX, int frameY)
    {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 3;

        currentDialogue = "The change will take \neffect after restarting \nthe game.";
        for(String line: currentDialogue.split("\n"))
        {
            g2.drawString(line,textX,textY);
            textY += 40;
        }

        //BACK
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX,textY);
        if(commandNum == 0)
        {
            g2.drawString(">", textX-25, textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 0;
            }
        }
    }
    public void options_control(int frameX,int frameY)
    {
        int textX;
        int textY;

        //TITLE
        String text = "Controls";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text,textX,textY);

        textX = frameX +gp.tileSize;
        textY += gp.tileSize;
        g2.drawString("Move", textX,textY); textY += gp.tileSize;
        g2.drawString("Confirm/Attack", textX,textY); textY += gp.tileSize;
        g2.drawString("Shoot/Cast", textX,textY); textY += gp.tileSize;
        g2.drawString("Character Screen", textX,textY); textY += gp.tileSize;
        g2.drawString("Pause", textX,textY); textY += gp.tileSize;
        g2.drawString("Options", textX,textY); textY += gp.tileSize;

        //KEYS
        textX = frameX + gp.tileSize * 6;
        textY = frameY + gp.tileSize * 2;
        g2.drawString("WASD", textX,textY); textY += gp.tileSize;
        g2.drawString("ENTER", textX,textY); textY += gp.tileSize;
        g2.drawString("F", textX,textY); textY += gp.tileSize;
        g2.drawString("C", textX,textY); textY += gp.tileSize;
        g2.drawString("P", textX,textY); textY += gp.tileSize;
        g2.drawString("ESC", textX,textY); textY += gp.tileSize;


        //BACK
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX,textY);
        if(commandNum == 0)
        {
            g2.drawString(">", textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 0;
                commandNum = 3; //back to control row
            }
        }
    }
    public void options_endGameConfirmation(int frameX, int frameY)
    {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 3;

        currentDialogue = "Quit the game and \nreturn to the title screen?";
        for(String line: currentDialogue.split("\n"))
        {
            g2.drawString(line,textX,textY);
            textY += 40;
        }
        //YES
        String text = "Yes";
        textX = getXforCenteredText(text);
        textY += gp.tileSize * 3;
        g2.drawString(text,textX,textY);
        if(commandNum == 0)
        {
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 0;
                gp.ui.titleScreenState = 0;
                gp.gameState = gp.titleState;
                gp.resetGame(true);
                gp.gameFacade.stopBackgroundMusic();
            }
        }

        //NO
        text = "No";
        textX = getXforCenteredText(text);
        textY += gp.tileSize;
        g2.drawString(text,textX,textY);
        if(commandNum == 1)
        {
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed == true)
            {
                subState = 0;
                commandNum = 4; //back to end row
            }
        }
    }
    public void drawSubWindow(int x, int y, int width, int height)
    {
        Color c = new Color(0,0,0,210);  // R,G,B, alfa(opacity)
        g2.setColor(c);
        g2.fillRoundRect(x,y,width,height,35,35);

        c = new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));    // 5 = width of outlines of graphics
        g2.drawRoundRect(x+5,y+5,width-10,height-10,25,25);

    }
    public int getXforCenteredText(String text)
    {
        int textLenght;
        textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth(); // Gets width of text.
        int x = gp.screenWidth / 2 - textLenght/2;
        return x;
    }
    public int getXforAlignToRight(String text, int tailX)
    {
        int textLenght;
        textLenght = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth(); // Gets width of text.
        int x = tailX - textLenght;
        return x;
    }
    public void addMessage(String text)
    {
        message.add(text);
        messageCounter.add(0);
    }
    public void draw(Graphics2D g2)
    {
        this.g2 = g2;
        g2.setFont(maruMonica);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);  // Anti Aliasing // Smoothes the text
        g2.setColor(Color.white);

        //TITLE STATE
        if(gp.gameState == gp.titleState)
        {
            drawTitleScreen();
        }
        //OTHERS
        else
        {
            //PLAY STATE
            if(gp.gameState == gp.playState)
            {
                drawPlayerLife();
                drawMonsterLife();
                drawMessage();
            }
            //PAUSE STATE
            if(gp.gameState == gp.pauseState)
            {
                drawPlayerLife();
                drawPauseScreen();
            }
            //DIALOGUE STATE
            if(gp.gameState == gp.dialogueState)
            {
                drawDialogueScreen();
            }
            //CHARACTER STATE
            if(gp.gameState == gp.characterState)
            {
                drawCharacterScreen();
                drawInventory(gp.player, true);
            }
            //OPTIONS STATE
            if(gp.gameState == gp.optionsState)
            {
                drawOptionsScreen();
            }
            //GAME OVER STATE
            if(gp.gameState == gp.gameOverState)
            {
                drawGameOverScreen();
            }
            //TRANSITION STATE
            if(gp.gameState == gp.transitionState)
            {
                drawTransition();
            }
            //TRADE STATE
            if(gp.gameState == gp.tradeState)
            {
                drawTradeScreen();
            }
            //SLEEP STATE
            if(gp.gameState == gp.sleepState)
            {
                drawSleepScreen();
            }
            if(gp.gameState == gp.enchantState)
            {
                drawEnchantScreen();
            }
        }
    }
}
