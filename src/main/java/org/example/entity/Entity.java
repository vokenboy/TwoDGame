package org.example.entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import org.example.main.DamageNumber;
import org.example.main.GamePanel;
import org.example.main.UtilityTool;

public class Entity {

    protected GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2, guardUp, guardDown, guardLeft, guardRight;
    public BufferedImage image, image2, image3;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    public String dialogues[][] = new String[20][20];
    public Entity attacker;
    public Entity linkedEntity; //link big rock and metal plate
    public boolean temp = false;

    //STATE
    public int worldX, worldY; // player's position on the map
    public String direction = "down";
    public int spriteNum = 1;
    public int dialogueSet = 0;
    public int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;
    public boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    public boolean deathCounted = false;
    public boolean hpBarOn = false;
    public boolean onPath = false;
    public boolean knockBack = false;
    public String knockBackDirection;
    public boolean guarding = false;
    public boolean transparent = false; //invincible when only gets damage
    public boolean offBalance = false;
    public Entity loot;
    public boolean opened = false;
    public boolean inRage = false;
    public boolean sleep = false;
    public boolean drawing = true;

    //COUNTER
    public int spriteCounter = 0;
    public int actionLockCounter = 0;
    public int invincibleCounter = 0;
    public int shotAvailableCounter = 0;
    int dyingCounter = 0;
    public int hpBarCounter = 0;
    int knockBackCounter = 0;
    public int guardCounter = 0;
    int offBalanceCounter = 0;
    private static final int DYING_ANIMATION_INTERVAL = 5;
    private static final int DYING_ANIMATION_STEPS =
        DYING_ANIMATION_INTERVAL * 8;

    //CHARACTER ATTRIBUTES
    public String name;
    public int defaultSpeed;
    public int speed;
    public int maxLife;
    public int life;
    public int maxMana;
    public int mana;
    public int ammo;
    public int level;
    public int strength;
    public int dexterity;
    public int attack;
    public int defense;
    public int exp;
    public int nextLevelExp;
    public int coin;
    public int motion1_duration;
    public int motion2_duration;
    public Entity currentWeapon;
    public Entity currentShield;
    public Entity currentLight;
    public Projectile projectile;
    public boolean boss;

    //ITEM ATTRIBUTES
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;
    public int attackValue;
    public int defenseValue;
    public String description = "";
    public int useCost;
    public int value;
    public int price;
    public int knockBackPower;
    public boolean stackable = false;
    public int amount = 1;
    public int lightRadius;

    //TYPE
    public int type; // 0=player, 1=npc, 2=monster etc.
    public final int type_player = 0;
    public final int type_npc = 1;
    public final int type_monster = 2;
    public final int type_sword = 3;
    public final int type_axe = 4;
    public final int type_shield = 5;
    public final int type_consumable = 6;
    public final int type_pickupOnly = 7;
    public final int type_obstacle = 8;
    public final int type_light = 9;
    public final int type_pickaxe = 10;

    // Add these new attributes for decorator effects
    public int lifeStealPercent = 0;
    public int criticalChance = 0;
    public boolean criticalHit = false;
    public int bonusDamagePercent = 0;
    public int bonusCritDamagePercent = 0;
    public int damageMitigationPercent = 0;
    public int elementalResistPercent = 0;
    public int manaRegenPerTick = 0;
    public int healthRegenPerTick = 0;
    public int speedPercent = 0;
    public int statusEffectChance = 0;
    public int guardStrength = 0;

    // Enchantment system attributes
    public Entity originalItem; // Store reference to unwrapped base item
    public int enchantmentSlots = 3; // Default 3 slots
    public int usedEnchantmentSlots = 0;
    public ArrayList<String> appliedEnchantments = new ArrayList<>();

    // STRATEGY CODE
    private MovementStrategy movementStrategy;

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public void performMove() {
        if (movementStrategy != null) {
            movementStrategy.move(this);
        }
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public GamePanel getGp() {
        return gp;
    }

    protected Entity deepCopy(Entity source) {
        source.solidArea = new Rectangle(source.solidArea);
        source.attackArea = new Rectangle(source.attackArea);
        return source;
    }

    public int getScreenX() {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        return screenX;
    }

    public int getScreenY() {
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        return screenY;
    }

    public int getLeftX() {
        return worldX + solidArea.x;
    }

    public int getRightX() {
        return worldX + solidArea.width + solidArea.width;
    }

    public int getTopY() {
        return worldY + solidArea.y;
    }

    public int getBottomY() {
        return worldY + solidArea.y + solidArea.height;
    }

    public int getCol() {
        return (worldX + solidArea.x) / gp.tileSize;
    }

    public int getRow() {
        return (worldY + solidArea.y) / gp.tileSize;
    }

    public int getCenterX() {
        int centerX = worldX + left1.getWidth() / 2;
        return centerX;
    }

    public int getCenterY() {
        int centerY = worldY + up1.getWidth() / 2;
        return centerY;
    }

    public int getXdistance(Entity target) {
        int xDistance = Math.abs(getCenterX() - target.getCenterX());
        return xDistance;
    }

    public int getYdistance(Entity target) {
        int yDistance = Math.abs(getCenterY() - target.getCenterY());
        return yDistance;
    }

    public int getTileDistance(Entity target) {
        int tileDistance =
            (getXdistance(target) + getYdistance(target)) / gp.tileSize;
        return tileDistance;
    }

    public int getGoalCol(Entity target) {
        int goalCol = (target.worldX + target.solidArea.x) / gp.tileSize;
        return goalCol;
    }

    public int getGoalRow(Entity target) {
        int goalRow = (target.worldY + target.solidArea.y) / gp.tileSize;
        return goalRow;
    }

    protected Player getClosestPlayer() {
        Player closest = null;
        int best = Integer.MAX_VALUE;
        for (Player p : gp.getPlayers()) {
            if (p == null) continue;
            int dist = getTileDistance(p);
            if (dist < best) {
                best = dist;
                closest = p;
            }
        }
        if (closest == null) {
            closest = gp.player;
        }
        return closest;
    }

    public void resetCounter() {
        spriteCounter = 0;
        actionLockCounter = 0;
        invincibleCounter = 0;
        shotAvailableCounter = 0;
        dyingCounter = 0;
        hpBarCounter = 0;
        knockBackCounter = 0;
        guardCounter = 0;
        offBalanceCounter = 0;
    }

    public void setDialogue() {}

    public void setLoot(Entity loot) {}

    public void setAction() {}

    public void move(String direction) {}

    public void damageReaction() {}

    public void speak() {}

    public void facePlayer() {
        switch (gp.player.direction) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }

    public void startDialogue(Entity entity, int setNum) {
        gp.gameState = gp.dialogueState;
        gp.ui.npc = entity;
        dialogueSet = setNum;
    }

    public void interact() {}

    public boolean use(Entity entity) {
        return false;
        //return "true" if you used the item and "false" if you failed to use it.
    }

    public void checkDrop() {}

    public void dropItem(Entity droppedItem) {
        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] == null) {
                gp.obj[gp.currentMap][i] = droppedItem;
                gp.obj[gp.currentMap][i].worldX = worldX; //the dead monster's worldX
                gp.obj[gp.currentMap][i].worldY = worldY; //the dead monster's worldY
                break; //end loop after finding empty slot on array
            }
        }
    }

    public Color getParticleColor() {
        Color color = null;
        //Sub-class specifications
        return color;
    }

    public int getParticleSize() {
        int size = 0; //pixels
        //Sub-class specifications
        return size;
    }

    public int getParticleSpeed() {
        int speed = 0;
        //Sub-class specifications
        return speed;
    }

    public int getParticleMaxLife() {
        int maxLife = 0;
        //Sub-class specifications
        return maxLife;
    }

    public void generateParticle(Entity generator, Entity target) {
        Color color = generator.getParticleColor();
        int size = generator.getParticleSize();
        int speed = generator.getParticleSpeed();
        int maxLife = generator.getParticleMaxLife();

        //generator becomes target so particles appear where the monster is.
        Particle p1 = new Particle(
            gp,
            target,
            color,
            size,
            speed,
            maxLife,
            -2,
            -1
        ); //TOP-LEFT
        Particle p2 = new Particle(
            gp,
            target,
            color,
            size,
            speed,
            maxLife,
            2,
            -1
        ); //TOP-RIGHT
        Particle p3 = new Particle(
            gp,
            target,
            color,
            size,
            speed,
            maxLife,
            -2,
            1
        ); //DOWN-LEFT
        Particle p4 = new Particle(
            gp,
            target,
            color,
            size,
            speed,
            maxLife,
            2,
            1
        ); //DOWN-RIGHT

        gp.particleList.add(p1);
        gp.particleList.add(p2);
        gp.particleList.add(p3);
        gp.particleList.add(p4);
    }

    public void checkCollision() {
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.npc);
        gp.cChecker.checkEntity(this, gp.monster);
        gp.cChecker.checkEntity(this, gp.iTile);
        Player contactPlayer = gp.cChecker.checkPlayer(this);
        if (this.type == type_monster && contactPlayer != null) {
            damagePlayer(contactPlayer, attack);
        }
    }

    public void update() {
        if (sleep == false) {
            if (knockBack == true) {
                checkCollision();
                if (collisionOn == true) {
                    knockBackCounter = 0;
                    knockBack = false;
                    speed = defaultSpeed;
                } else if (collisionOn == false) {
                    switch (knockBackDirection) {
                        case "up":
                            worldY -= speed;
                            break;
                        case "down":
                            worldY += speed;
                            break;
                        case "left":
                            worldX -= speed;
                            break;
                        case "right":
                            worldX += speed;
                            break;
                    }
                }
                knockBackCounter++;
                if (knockBackCounter == 10) {
                    knockBackCounter = 0;
                    knockBack = false;
                    speed = defaultSpeed;
                }
            } else if (attacking == true) {
                attacking();
            } else {
                setAction();
                checkCollision();

                if (collisionOn == false) {
                    switch (direction) {
                        case "up":
                            worldY -= speed;
                            break;
                        case "down":
                            worldY += speed;
                            break;
                        case "left":
                            worldX -= speed;
                            break;
                        case "right":
                            worldX += speed;
                            break;
                    }
                }
                spriteCounter++;
                if (spriteCounter > 24) {
                    if (
                        spriteNum == 1 //Every 12 frames sprite num changes.
                    ) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 1;
                    }
                    spriteCounter = 0; // spriteCounter reset
                }
            }
            //Like player's invincible method
            if (invincible == true) {
                invincibleCounter++;
                if (invincibleCounter > 40) {
                    invincible = false;
                    invincibleCounter = 0;
                }
            }
            if (shotAvailableCounter < 30) {
                shotAvailableCounter++;
            }
            if (offBalance == true) {
                offBalanceCounter++;
                if (offBalanceCounter > 60) {
                    offBalance = false;
                    offBalanceCounter = 0;
                }
            }
        }
    }

    public void tickHudTimers() {
        if (hpBarOn) {
            hpBarCounter++;
            if (hpBarCounter > 600) {
                hpBarCounter = 0;
                hpBarOn = false;
            }
        }
    }

    public void progressDying() {
        if (dying) {
            dyingCounter++;
            if (dyingCounter > DYING_ANIMATION_STEPS) {
                alive = false;
            }
        }
    }

    public void checkAttackOrNot(int rate, int straight, int horizontal) {
        boolean tartgetInRange = false;
        Player targetPlayer = getClosestPlayer();
        int xDis = getXdistance(targetPlayer);
        int yDis = getYdistance(targetPlayer);

        switch (direction) {
            case "up":
                if (
                    targetPlayer.getCenterY() < getCenterY() &&
                    yDis < straight &&
                    xDis < horizontal
                ) {
                    tartgetInRange = true;
                }
                break;
            case "down":
                if (
                    targetPlayer.getCenterY() > getCenterY() &&
                    yDis < straight &&
                    xDis < horizontal
                ) {
                    tartgetInRange = true;
                }
                break;
            case "left":
                if (
                    targetPlayer.getCenterX() < getCenterX() &&
                    xDis < straight &&
                    yDis < horizontal
                ) {
                    tartgetInRange = true;
                }
                break;
            case "right":
                if (
                    targetPlayer.getCenterX() > getCenterX() &&
                    xDis < straight &&
                    yDis < horizontal
                ) {
                    tartgetInRange = true;
                }
                break;
        }

        if (tartgetInRange == true) {
            //Check if it initiates an attack
            int i = new Random().nextInt(rate);
            if (i == 0) {
                attacking = true;
                spriteNum = 1;
                spriteCounter = 0;
                shotAvailableCounter = 0;
            }
        }
    }

    public void checkShootOrNot(int rate, int shotInterval) {
        int i = new Random().nextInt(rate);
        if (
            i == 0 &&
            projectile.alive == false &&
            shotAvailableCounter == shotInterval
        ) {
            projectile.set(worldX, worldY, direction, true, this);
            //gp.projectileList.add(projectile);
            //CHECK VACANCY
            for (int ii = 0; ii < gp.projectile[1].length; ii++) {
                if (gp.projectile[gp.currentMap][ii] == null) {
                    gp.projectile[gp.currentMap][ii] = projectile;
                    break;
                }
            }
            shotAvailableCounter = 0;
        }
    }

    public void checkStartChasingOrNot(Entity target, int distance, int rate) {
        if (getTileDistance(target) < distance) {
            int i = new Random().nextInt(rate);
            if (i == 0) {
                onPath = true;
            }
        }
    }

    public void checkStopChasingOrNot(Entity target, int distance, int rate) {
        if (getTileDistance(target) > distance) {
            int i = new Random().nextInt(rate);
            if (i == 0) {
                onPath = false;
            }
        }
    }

    public void searchPath(int goalCol, int goalRow) {
        int startCol = (worldX + solidArea.x) / gp.tileSize;
        int startRow = (worldY + solidArea.y) / gp.tileSize;

        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow, this);

        if (gp.pFinder.search()) {
            // Next world coordinates
            int nextX = gp.pFinder.pathList.get(0).col * gp.tileSize;
            int nextY = gp.pFinder.pathList.get(0).row * gp.tileSize;

            // Entity's solidArea position
            int enLeftX = worldX + solidArea.x;
            int enRightX = worldX + solidArea.x + solidArea.width;
            int enTopY = worldY + solidArea.y;
            int enBottomY = worldY + solidArea.y + solidArea.height;

            // TOP PATH
            if (
                enTopY > nextY &&
                enLeftX >= nextX &&
                enRightX < nextX + gp.tileSize
            ) {
                direction = "up";
            }
            // BOTTOM PATH
            else if (
                enTopY < nextY &&
                enLeftX >= nextX &&
                enRightX < nextX + gp.tileSize
            ) {
                direction = "down";
            }
            // RIGHT - LEFT PATH
            else if (enTopY >= nextY && enBottomY < nextY + gp.tileSize) {
                // LEFT
                if (enLeftX > nextX) {
                    direction = "left";
                }
                // RIGHT
                else if (enLeftX < nextX) {
                    direction = "right";
                }
            }
            // OTHER CASES
            else if (enTopY > nextY && enLeftX > nextX) {
                // up or left
                direction = "up";
                checkCollision();
                if (collisionOn) {
                    direction = "left";
                }
            } else if (enTopY > nextY && enLeftX < nextX) {
                // up or right
                direction = "up";
                checkCollision();
                if (collisionOn) {
                    direction = "right";
                }
            } else if (enTopY < nextY && enLeftX > nextX) {
                // down or left
                direction = "down";
                checkCollision();
                if (collisionOn) {
                    direction = "left";
                }
            } else if (enTopY < nextY && enLeftX < nextX) {
                // down or right
                direction = "down";
                checkCollision();
                if (collisionOn) {
                    direction = "right";
                }
            }

            // If you want to stop when reaching target (for NPCs)
            // int nextCol = gp.pFinder.pathList.get(0).col;
            // int nextRow = gp.pFinder.pathList.get(0).row;
            // if (nextCol == goalCol && nextRow == goalRow) {
            //     onPath = false;
            // }
        }
    }

    public void moveTowardPlayer(int interval) {
        actionLockCounter++;

        if (actionLockCounter > interval) {
            Player target = getClosestPlayer();
            if (
                getXdistance(target) > getYdistance(target) //if entity far to the player on X axis moves right or left
            ) {
                if (
                    target.getCenterX() < getCenterX() //Player is left side, entity moves to left
                ) {
                    direction = "left";
                } else {
                    direction = "right";
                }
            } else if (
                getXdistance(target) < getYdistance(target) //if entity far to the player on Y axis moves up or down
            ) {
                if (
                    target.getCenterY() < getCenterY() //Player is up side, entity moves to up
                ) {
                    direction = "up";
                } else {
                    direction = "down";
                }
            }
            actionLockCounter = 0;
        }
    }

    public String getOppositeDirection(String direction) {
        String oppositeDirection = "";

        switch (direction) {
            case "up":
                oppositeDirection = "down";
                break;
            case "down":
                oppositeDirection = "up";
                break;
            case "left":
                oppositeDirection = "right";
                break;
            case "right":
                oppositeDirection = "left";
                break;
        }

        return oppositeDirection;
    }

    public void attacking() {
        spriteCounter++;

        if (spriteCounter <= motion1_duration) {
            spriteNum = 1;
        }
        if (
            spriteCounter > motion1_duration &&
            spriteCounter <= motion2_duration
        ) {
            spriteNum = 2;

            //Save the current worldX, worldY, solidArea
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            //Adjust player's worldX/worldY for the attackArea
            switch (direction) {
                case "up":
                    worldY -= attackArea.height;
                    break; //attackArea's size
                case "down":
                    worldY += gp.tileSize;
                    break; //gp.tileSize(player's size)
                case "left":
                    worldX -= attackArea.width;
                    break; //attackArea's size
                case "right":
                    worldX += gp.tileSize;
                    break; //gp.tileSize(player's size)
            }

            //attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            if (type == type_monster) {
                Player victim = gp.cChecker.checkPlayer(this);
                if (victim != null) {
                    //This means attack is hitting player
                    damagePlayer(victim, attack);
                }
            }
            // Player (or other controllable entity using player attack rules)
            else {
                Player attackingPlayer = (this instanceof Player)
                    ? (Player) this
                    : gp.player;
                //Check monster collision with the updated worldX, worldY and solidArea
                int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
                attackingPlayer.damageMonster(
                    monsterIndex,
                    this,
                    attack,
                    currentWeapon.knockBackPower
                );

                int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
                attackingPlayer.damageInteractiveTile(iTileIndex);

                int projectileIndex = gp.cChecker.checkEntity(
                    this,
                    gp.projectile
                );
                attackingPlayer.damageProjectile(projectileIndex);
            }

            //After checking collision, restore the original data
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if (spriteCounter > motion2_duration) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void damagePlayer(Player target, int attack) {
        if (!target.invincible) {
            int damage = attack - target.defense;
            String canGuardDirection = getOppositeDirection(direction);

            if (target.guarding && target.direction.equals(canGuardDirection)) {
                if (target.guardCounter < 10) {
                    damage = 0;
                    gp.gameFacade.playSoundEffect(16);
                    setKnockBack(this, target, knockBackPower);
                    offBalance = true;
                    spriteCounter -= 60;
                } else {
                    damage = target.mitigateIncomingDamage(
                        Math.max(1, damage),
                        true
                    );
                    gp.gameFacade.playSoundEffect(15);
                }
            } else {
                gp.gameFacade.playSoundEffect(6);
                if (damage < 1) damage = 1;
            }

            if (damage != 0) {
                target.transparent = true;
                setKnockBack(target, this, knockBackPower);
            }

            target.life -= damage;
            target.invincible = true;

            gp.damageNumbers.add(
                new DamageNumber(
                    damage,
                    target.worldX,
                    target.worldY - gp.tileSize / 2
                )
            );
        }
    }

    public void takeDamage(int damage) {
        if (!invincible && alive) {
            damage = gp.player.mitigateIncomingDamage(
                Math.max(1, damage),
                false
            );
            if (type == type_monster) {
                hpBarOn = true;
                hpBarCounter = 0;
            }
            life -= damage;
            invincible = true;

            gp.damageNumbers.add(
                new DamageNumber(damage, worldX, worldY - gp.tileSize / 2)
            );

            if (life <= 0) {
                dying = true;
                life = 0;
            }
        }
    }

    public void setKnockBack(
        Entity target,
        Entity attacker,
        int knockBackPower
    ) {
        this.attacker = attacker;
        target.knockBackDirection = attacker.direction;
        target.speed += knockBackPower;
        target.knockBack = true;
    }

    public boolean inCamera() {
        boolean inCamera = false;
        if (
            worldX + gp.tileSize * 5 > gp.player.worldX - gp.player.screenX && //*5 because skeleton lord disappears when the top left corner isn't on the screen
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize * 5 > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY
        ) {
            inCamera = true;
        }
        return inCamera;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (inCamera() == true) {
            int tempScreenX = getScreenX();
            int tempScreenY = getScreenY();

            switch (direction) {
                case "up":
                    if (
                        attacking == false //Normal walking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = up1;
                        }
                        if (spriteNum == 2) {
                            image = up2;
                        }
                    }
                    if (
                        attacking == true //Attacking sprites
                    ) {
                        tempScreenY = getScreenY() - up1.getHeight(); //Adjusted the player's position one tile to up. Explained why I did it at where I call attacking() in update().
                        if (spriteNum == 1) {
                            image = attackUp1;
                        }
                        if (spriteNum == 2) {
                            image = attackUp2;
                        }
                    }
                    break;
                case "down":
                    if (
                        attacking == false //Normal walking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = down1;
                        }
                        if (spriteNum == 2) {
                            image = down2;
                        }
                    }
                    if (
                        attacking == true //Attacking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = attackDown1;
                        }
                        if (spriteNum == 2) {
                            image = attackDown2;
                        }
                    }
                    break;
                case "left":
                    if (
                        attacking == false //Normal walking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = left1;
                        }
                        if (spriteNum == 2) {
                            image = left2;
                        }
                    }
                    if (
                        attacking == true //Attacking sprites
                    ) {
                        tempScreenX = getScreenX() - up1.getWidth(); //Adjusted the player's position one tile left. Explained why I did it at where I call attacking() in update().
                        if (spriteNum == 1) {
                            image = attackLeft1;
                        }
                        if (spriteNum == 2) {
                            image = attackLeft2;
                        }
                    }
                    break;
                case "right":
                    if (
                        attacking == false //Normal walking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = right1;
                        }
                        if (spriteNum == 2) {
                            image = right2;
                        }
                    }
                    if (
                        attacking == true //Attacking sprites
                    ) {
                        if (spriteNum == 1) {
                            image = attackRight1;
                        }
                        if (spriteNum == 2) {
                            image = attackRight2;
                        }
                    }
                    break;
            }

            //Make entity half-transparent (%30) when invincible
            if (invincible == true) {
                hpBarOn = true; //when player attacks monster play hpBar
                hpBarCounter = 0; //reset monster aggro
                changeAlpha(g2, 0.4F);
            }

            if (dying == true) {
                dyingAnimation(g2);
            }

            g2.drawImage(image, tempScreenX, tempScreenY, null);

            //Reset graphics opacity / alpha
            changeAlpha(g2, 1F);
        }
    }

    // Every 5 frames switch alpha between 0 and 1
    public void dyingAnimation(Graphics2D g2) {
        int i = DYING_ANIMATION_INTERVAL; //interval

        if (dyingCounter <= i) {
            changeAlpha(g2, 0f);
        } //If you want add death animation or something like that, you can use your sprites instead of changing alpha inside of if statements
        if (dyingCounter > i && dyingCounter <= i * 2) {
            changeAlpha(g2, 1f);
        }
        if (dyingCounter > i * 2 && dyingCounter <= i * 3) {
            changeAlpha(g2, 0f);
        }
        if (dyingCounter > i * 3 && dyingCounter <= i * 4) {
            changeAlpha(g2, 1f);
        }
        if (dyingCounter > i * 4 && dyingCounter <= i * 5) {
            changeAlpha(g2, 0f);
        }
        if (dyingCounter > i * 5 && dyingCounter <= i * 6) {
            changeAlpha(g2, 1f);
        }
        if (dyingCounter > i * 6 && dyingCounter <= i * 7) {
            changeAlpha(g2, 0f);
        }
        if (dyingCounter > i * 7 && dyingCounter <= i * 8) {
            changeAlpha(g2, 1f);
        }
    }

    public void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue)
        );
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(
                getClass().getResourceAsStream(imagePath + ".png")
            );
            image = uTool.scaleImage(image, width, height); //it scales to tilesize , will fix for player attack(16px x 32px) by adding width and height
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getDetected(Entity user, Entity target[][], String targetName) {
        int index = 999;

        //Check the surrounding object
        int nextWorldX = user.getLeftX();
        int nextWorldY = user.getTopY();

        switch (user.direction) {
            case "up":
                nextWorldY = user.getTopY() - gp.player.speed;
                break;
            case "down":
                nextWorldY = user.getBottomY() + gp.player.speed;
                break;
            case "left":
                nextWorldX = user.getLeftX() - gp.player.speed;
                break;
            case "right":
                nextWorldX = user.getRightX() + gp.player.speed;
                break;
        }
        int col = nextWorldX / gp.tileSize;
        int row = nextWorldY / gp.tileSize;

        for (int i = 0; i < target[1].length; i++) {
            if (target[gp.currentMap][i] != null) {
                if (
                    target[gp.currentMap][i].getCol() == col && //checking if player 1 tile away from target (key etc.) (must be same direction)
                    target[gp.currentMap][i].getRow() == row &&
                    target[gp.currentMap][i].name.equals(targetName)
                ) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
}
