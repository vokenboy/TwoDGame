package org.example.entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.example.achievement.Achievement;
import org.example.achievement.AchievementGroup;
import org.example.achievement.MapKillTracker;
import org.example.achievement.MapVisitTracker;
import org.example.achievement.SingleAchievement;
import org.example.main.GamePanel;
import org.example.main.KeyHandler;
import org.example.main.PlayerObserver;
import org.example.main.input.PlayerInput;
import org.example.object.*;
import org.example.tile_interactive.InteractiveTile;
import org.example.visitor.ChopVisitor;
import org.example.visitor.SmashVisitor;
import org.example.visitor.TileVisitor;

public class Player extends Entity {

    PlayerInput input;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public boolean attackCanceled = false;
    public boolean lightUpdated = false;
    private int networkId = -1;

    private List<PlayerObserver> observers = new ArrayList<>();
    private final Random combatRandom = new Random();
    private final Random achievementRandom = new Random();
    private AchievementGroup achievementRoot;
    private final Map<Integer, AchievementGroup> mapGroups = new HashMap<>();
    private final Map<String, CompositeAchievementNode> nodes = new LinkedHashMap<>();
    private static final List<Template> TEMPLATE_POOL = createTemplatePool();
    private static final Map<String, Template> TEMPLATE_INDEX = indexTemplates(
        TEMPLATE_POOL
    );
    private int lastTrackedMap = -1;

    public Player(GamePanel gp, PlayerInput input) {
        super(gp); // calling constructor of super class(from entity class)
        this.input = input;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = 8;
        solidAreaDefaultY = 16;

        //      attackArea.width = 36;  //For test sword
        //      attackArea.height = 36;

        setDefaultValues(); // when u create Player object, initialize with default values
    }

    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    private void notifyHealthChange() {
        for (PlayerObserver o : observers) {
            o.onHealthChange(this);
        }
    }

    private void notifyManaChange() {
        for (PlayerObserver o : observers) {
            o.onManaChange(this);
        }
    }

    private void notifyLevelUp() {
        for (PlayerObserver o : observers) {
            o.onLevelUp(this);
        }
    }

    private void notifyMonsterDamaged(Entity monster, int damage) {
        for (PlayerObserver o : observers) {
            o.onMonsterDamaged(monster, damage);
        }
    }

    public AchievementGroup getAchievementRoot() {
        return achievementRoot;
    }

    public void resetAchievementProgress() {
        MapKillTracker.reset();
        MapVisitTracker.reset();
        buildAchievements();
    }

    private void buildAchievements() {
        nodes.clear();
        mapGroups.clear();
        achievementRoot = new AchievementGroup("All Achievements", "Complete the set");

        AchievementGroup map0 = addMapGroup(0, "Outside", "Starting grounds achievements");
        AchievementGroup map1 = addMapGroup(1, "Indoor", "House & merchant zone achievements");
        AchievementGroup map2 = addMapGroup(2, "Dungeon", "Lower-level challenges");

        spawnBaseNodes(0, map0);
        spawnBaseNodes(1, map1);
        spawnBaseNodes(2, map2);
    }

    private AchievementGroup addMapGroup(int mapIndex, String name, String description) {
        AchievementGroup group = new AchievementGroup(name, description);
        achievementRoot.add(group);
        mapGroups.put(mapIndex, group);
        return group;
    }

    private void spawnBaseNodes(int mapIndex, AchievementGroup mapGroup) {
        List<Template> base = new ArrayList<>();
        for (Template template : TEMPLATE_POOL) {
            if (template.mapIndex == mapIndex && template.isBase) {
                base.add(template);
            }
        }
        if (base.isEmpty()) {
            return;
        }
        Collections.shuffle(base, achievementRandom);
        int count = Math.min(base.size(), achievementRandom.nextInt(3) + 1);
        for (int i = 0; i < count; i++) {
            Template pick = base.get(i);
            CompositeAchievementNode node = createNode(pick);
            if (!mapGroup.getChildren().contains(node)) {
                mapGroup.add(node);
            }
        }
    }

    private CompositeAchievementNode createNode(Template template) {
        CompositeAchievementNode existing = nodes.get(template.id);
        if (existing != null) {
            return existing;
        }

        int required = template.randomRequired(achievementRandom);
        List<Template> childTemplates = resolveChildTemplates(template);
        CompositeAchievementNode node = new CompositeAchievementNode(
            template,
            required,
            childTemplates
        );
        nodes.put(template.id, node);
        return node;
    }

    public PlayerInput getInput() {
        return input;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public void setDefaultValues() {
        resetAchievementProgress();
        //Default Starting Positions
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        gp.currentMap = 0;
        gp.currentArea = gp.outside;

        //Blue Gem Start Position, mapNum = 3;
        //         worldX = gp.tileSize *25;
        //        worldY = gp.tileSize * 9;
        //        gp.currentMap = 3;

        defaultSpeed = 4;
        speed = defaultSpeed;
        direction = "down";

        //PLAYER STATUS
        level = 1;
        maxLife = 10;
        life = maxLife;
        maxMana = 8;
        mana = maxMana;
        ammo = 10;
        strength = 1; // The more strenght he has, the more damage he gives.
        dexterity = 1; // The more dexterity he has, the less damage he receives.
        exp = 0;
        nextLevelExp = 4;
        coin = 40;
        invincible = false;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = null;
        projectile = new OBJ_Fireball(gp);
        //projectile = new OBJ_Rock(gp);
        attack = getAttack(); // The total attack value is decided by strength and weapon
        defense = getDefense(); // The total defense value is decided by dexterity and shield

        getImage();
        getAttackImage();
        getGuardImage();
        setItems();
        //setDialogue();
        trackMapVisit();
    }

    public void setDefaultPositions() {
        gp.currentMap = 0;
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";
    }

    public void setDialogue() {
        dialogues[0][0] =
            "You are level " + level + " now!\n" + "You feel stronger!";
    }

    public void restoreStatus() {
        life = maxLife;
        mana = maxMana;
        speed = defaultSpeed;
        invincible = false;
        transparent = false;
        attacking = false;
        guarding = false;
        knockBack = false;
        lightUpdated = true;
    }

    public void setItems() {
        inventory.clear(); //cuz if game restarts inventory must be cleared first
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        /*inventory.add(new OBJ_Potion_Red(gp));
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Key(gp));

        inventory.add(new OBJ_Lantern(gp));
        inventory.add(new OBJ_Axe(gp));
        inventory.add(new OBJ_Pickaxe(gp));*/
    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        motion1_duration = currentWeapon.motion1_duration;
        motion2_duration = currentWeapon.motion2_duration;
        return attack = strength * currentWeapon.attackValue;
    }

    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    public void equipWeapon(Entity weapon) {
        if (weapon == null) {
            return;
        }
        this.currentWeapon = weapon;
        this.attack = getAttack();
        getAttackImage();
    }

    public void equipShield(Entity shield) {
        if (shield == null) {
            return;
        }
        this.currentShield = shield;
        this.defense = getDefense();
    }

    public int getCurrentWeaponSlot() {
        int currentWeaponSlot = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == currentWeapon) {
                currentWeaponSlot = i;
            }
        }
        return currentWeaponSlot;
    }

    public int getCurrentShieldSlot() {
        int currentShieldSlot = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) == currentShield) {
                currentShieldSlot = i;
            }
        }
        return currentShieldSlot;
    }

    public void getImage() {
        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getSleepingImage(BufferedImage image) {
        up1 = image;
        up2 = image;
        down1 = image;
        down2 = image;
        left1 = image;
        left2 = image;
        right1 = image;
        right2 = image;
    }

    public void getAttackImage() {
        if (currentWeapon.type == type_sword) {
            attackUp1 = setup(
                "/player/boy_attack_up_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackUp2 = setup(
                "/player/boy_attack_up_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown1 = setup(
                "/player/boy_attack_down_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown2 = setup(
                "/player/boy_attack_down_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackLeft1 = setup(
                "/player/boy_attack_left_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackLeft2 = setup(
                "/player/boy_attack_left_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight1 = setup(
                "/player/boy_attack_right_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight2 = setup(
                "/player/boy_attack_right_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
        } else if (currentWeapon.type == type_axe) {
            attackUp1 = setup(
                "/player/boy_axe_up_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackUp2 = setup(
                "/player/boy_axe_up_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown1 = setup(
                "/player/boy_axe_down_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown2 = setup(
                "/player/boy_axe_down_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackLeft1 = setup(
                "/player/boy_axe_left_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackLeft2 = setup(
                "/player/boy_axe_left_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight1 = setup(
                "/player/boy_axe_right_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight2 = setup(
                "/player/boy_axe_right_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
        } else if (currentWeapon.type == type_pickaxe) {
            attackUp1 = setup(
                "/player/boy_pick_up_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackUp2 = setup(
                "/player/boy_pick_up_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown1 = setup(
                "/player/boy_pick_down_1",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackDown2 = setup(
                "/player/boy_pick_down_2",
                gp.tileSize,
                gp.tileSize * 2
            ); // 16x32 px
            attackLeft1 = setup(
                "/player/boy_pick_left_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackLeft2 = setup(
                "/player/boy_pick_left_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight1 = setup(
                "/player/boy_pick_right_1",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
            attackRight2 = setup(
                "/player/boy_pick_right_2",
                gp.tileSize * 2,
                gp.tileSize
            ); // 32x16 px
        }
    }

    public void getGuardImage() {
        guardUp = setup("/player/boy_guard_up", gp.tileSize, gp.tileSize);
        guardDown = setup("/player/boy_guard_down", gp.tileSize, gp.tileSize);
        guardLeft = setup("/player/boy_guard_left", gp.tileSize, gp.tileSize);
        guardRight = setup("/player/boy_guard_right", gp.tileSize, gp.tileSize);
    }

    public void attackAction() {
        if (!attacking && !attackCanceled) {
            gp.gameFacade.playSoundEffect(7);
            attacking = true;
            spriteCounter = 0;
        }
    }

    public void castSpellAction() {
        if (
            projectile != null &&
            projectile.alive == false &&
            shotAvailableCounter == 30 &&
            projectile.haveResource(this)
        ) {
            projectile.set(worldX, worldY, direction, true, this);

            projectile.subtractResource(this);

            for (int i = 0; i < gp.projectile[1].length; i++) {
                if (gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = projectile;
                    break;
                }
            }

            shotAvailableCounter = 0;
            gp.gameFacade.playSoundEffect(10);
        }
    }

    public void update() {
        trackMapVisit();
        if (knockBack == true) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);
            gp.cChecker.checkEntity(this, gp.npc);
            gp.cChecker.checkEntity(this, gp.monster);
            gp.cChecker.checkEntity(this, gp.iTile);

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
        } else if (input.guard() == true) {
            guarding = true;
            guardCounter++;
        } else if (
            input.up() == true ||
            input.down() == true ||
            input.left() == true ||
            input.right() == true ||
            input.interact() == true
        ) {
            if (input.up() == true) {
                direction = "up";
            } else if (input.down() == true) {
                // You can go up and down while you pressing left or right.
                direction = "down"; // But if you going up or down you cannot press left or right
            }
            // The reason is here the if statements order.
            else if (
                input.left() == true // For example when "keyH.upPressed == true", the else if blocks are not working. And you cannot go anyway when you press up.
            ) {
                direction = "left";
            } else if (input.right() == true) {
                direction = "right";
            }
            //CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            //CHECK NPC COLLISION
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc); // npc array. checks any of npc collision
            interactNPC(npcIndex);

            //CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster); // npc array. checks any of npc collision
            contactMonster(monsterIndex);

            //CHECK INTERACTIVE COLLISION
            int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);

            //CHECK EVENT
            gp.eHandler.checkEvent();

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (
                collisionOn == false && input.interact() == false //Without this, player moves when you press INTERACT
            ) {
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

            if (input.attack() == true && attackCanceled == false) {
                gp.gameFacade.playSoundEffect(7);
                attacking = true;
                spriteCounter = 0;
            }

            attackCanceled = false;
            guarding = false;
            guardCounter = 0;

            spriteCounter++;
            if (spriteCounter > 12) {
                if (
                    spriteNum == 1 //spriteNum changes every 12 frames
                ) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0; // spriteCounter reset
            }
        }
        // This is for: If you release the key when you walking, change sprite num to 1 and use player's not-walking sprite.
        else {
            standCounter++;
            if (
                standCounter == 20 // After you release the key player stands 20 frames last position then spriteNum will be 1(default)
            ) {
                spriteNum = 1;
                standCounter = 0; // standCounter reset
            }
            guarding = false;
            guardCounter = 0;
        }

        //PROJECTILE SHOOTING
        if (
            input.cast() &&
            shotAvailableCounter == 30 &&
            projectile.haveResource(this)
        ) {
            Projectile newProjectile = projectile.clone(); // clone prototype
            newProjectile.set(worldX, worldY, direction, true, this);
            newProjectile.subtractResource(this);
            System.out.println(
                "Original projectile: " + System.identityHashCode(projectile)
            );
            System.out.println(
                "New projectile clone: " +
                    System.identityHashCode(newProjectile)
            );
            System.out.println(
                "Original projectile attack area: " +
                    System.identityHashCode(projectile.attackArea)
            );
            System.out.println(
                "New projectile clone attack area: " +
                    System.identityHashCode(newProjectile.attackArea)
            );

            for (int i = 0; i < gp.projectile[1].length; i++) {
                if (gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = newProjectile;
                    break;
                }
            }
            shotAvailableCounter = 0;
            gp.gameFacade.playSoundEffect(10);
            notifyManaChange();
        }

        if (
            input.altCast() &&
            shotAvailableCounter == 30 &&
            projectile.haveResource(this)
        ) {
            Projectile bigProjectile = projectile.clone();

            bigProjectile.solidArea.width = 100;
            bigProjectile.solidArea.height = 100;
            bigProjectile.attack = 1;

            bigProjectile.set(worldX, worldY, direction, true, this);
            bigProjectile.subtractResource(this);

            for (int i = 0; i < gp.projectile[1].length; i++) {
                if (gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = bigProjectile;
                    break;
                }
            }

            shotAvailableCounter = 0;
            gp.gameFacade.playSoundEffect(10);
            notifyManaChange();
        }

        //This needs to be outside of key if statement! // If player receive damage from monster, player's gonna be invincible for a second
        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                transparent = false;
                invincibleCounter = 0;
            }
        }

        if (shotAvailableCounter < 30) {
            shotAvailableCounter++;
        }
        if (
            life > maxLife //for using potion, heal etc.
        ) {
            life = maxLife;
        }
        if (
            mana > maxMana //for using potion, heal etc.
        ) {
            mana = maxMana;
        }
        if (gp.keyH.godModeOn == false) {
            if (life <= 0) {
                gp.gameState = gp.gameOverState;
                gp.ui.commandNum = -1; //for if you die while pressing enter
                gp.gameFacade.stopBackgroundMusic();
                gp.gameFacade.playSoundEffect(12);
            }
        }
    }

    public void castAltSpell() {
        if (
            projectile == null ||
            !projectile.alive ||
            shotAvailableCounter == 30
        ) {
            if (projectile.haveResource(this)) {
                Projectile bigProjectile = projectile.clone();
                bigProjectile.solidArea.width = 100;
                bigProjectile.solidArea.height = 100;
                bigProjectile.attack = this.attack * 2;
                bigProjectile.set(worldX, worldY, direction, true, this);
                bigProjectile.subtractResource(this);

                for (int i = 0; i < gp.projectile[1].length; i++) {
                    if (gp.projectile[gp.currentMap][i] == null) {
                        gp.projectile[gp.currentMap][i] = bigProjectile;
                        gp.gameFacade.playSoundEffect(10);
                        break;
                    }
                }

                shotAvailableCounter = 0;
            }
        }
    }

    public void pickUpObject(int i) {
        if (i != 999) {
            // PICKUP ONLY ITEMS
            if (gp.obj[gp.currentMap][i].type == type_pickupOnly) {
                gp.obj[gp.currentMap][i].use(this);
                gp.obj[gp.currentMap][i] = null;
            }
            //OBSTACLE / INTERACTABLE
            else if (gp.obj[gp.currentMap][i].type == type_obstacle) {
                if (input.interact()) {
                    attackCanceled = true;
                    gp.obj[gp.currentMap][i].interact();
                }
            }
            // INVENTORY ITEMS
            else {
                String text;
                if (
                    canObtainItem(gp.obj[gp.currentMap][i]) == true //if inventory is not full can pick up object
                ) {
                    //inventory.add(gp.obj[gp.currentMap][i]); //canObtainItem() already adds item
                    gp.gameFacade.playSoundEffect(16);
                    text = "Got a " + gp.obj[gp.currentMap][i].name + "!";
                } else {
                    text = "You cannot carry any more";
                }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null;
            }
        }
    }

    public void interactNPC(int i) {
        if (i != 999) {
            if (input.interact()) {
                attackCanceled = true;
                gp.npc[gp.currentMap][i].speak();
            }

            gp.npc[gp.currentMap][i].move(direction);
        }
    }

    public void contactMonster(int i) {
        // CollisionChecker Method Implement //checkPlayer() : Checks who touches to player //checkEntity() : Checks if player touches to an entity;
        if (i != 999) {
            if (
                invincible == false &&
                gp.monster[gp.currentMap][i].dying == false
            ) {
                gp.gameFacade.playSoundEffect(6); //receivedamage.wav

                int damage = gp.monster[gp.currentMap][i].attack - defense;
                if (damage < 1) {
                    damage = 1;
                }
                life -= damage;
                invincible = true;
                transparent = true;
            }
        }
    }

    public int mitigateIncomingDamage(int damage, boolean perfectGuard) {
        int adjustedDamage = Math.max(0, damage);

        int totalMitigationPercent = damageMitigationPercent;
        int totalElementalResistPercent = elementalResistPercent;
        int totalGuardStrength = guardStrength;
        int totalBonusDamagePercent = bonusDamagePercent;
        int totalCritChance = criticalChance;
        int totalCritDamagePercent = bonusCritDamagePercent;
        int totalLifeStealPercent = lifeStealPercent;

        if (currentWeapon != null) {
            totalMitigationPercent += Math.max(
                0,
                currentWeapon.damageMitigationPercent
            );
            totalElementalResistPercent += Math.max(
                0,
                currentWeapon.elementalResistPercent
            );
            totalGuardStrength += Math.max(0, currentWeapon.guardStrength);
            totalBonusDamagePercent += currentWeapon.bonusDamagePercent;
            totalCritChance += currentWeapon.criticalChance;
            totalCritDamagePercent += currentWeapon.bonusCritDamagePercent;
            totalLifeStealPercent += currentWeapon.lifeStealPercent;
        }

        if (currentShield != null) {
            totalMitigationPercent += Math.max(
                0,
                currentShield.damageMitigationPercent
            );
            totalElementalResistPercent += Math.max(
                0,
                currentShield.elementalResistPercent
            );
            totalGuardStrength += Math.max(0, currentShield.guardStrength);
            totalBonusDamagePercent += currentShield.bonusDamagePercent;
            totalCritChance += currentShield.criticalChance;
            totalCritDamagePercent += currentShield.bonusCritDamagePercent;
            totalLifeStealPercent += currentShield.lifeStealPercent;
        }

        totalCritChance = Math.min(100, Math.max(0, totalCritChance));
        totalMitigationPercent = Math.max(0, totalMitigationPercent);
        totalElementalResistPercent = Math.max(0, totalElementalResistPercent);
        totalGuardStrength = Math.max(0, totalGuardStrength);

        criticalHit = false;

        if (perfectGuard) {
            adjustedDamage = Math.max(0, adjustedDamage - totalGuardStrength);
            int combinedMitigation = Math.min(
                95,
                totalMitigationPercent + totalElementalResistPercent
            );
            adjustedDamage = reduceByPercent(
                adjustedDamage,
                combinedMitigation
            );
            return adjustedDamage;
        }

        adjustedDamage = increaseByPercent(
            adjustedDamage,
            totalBonusDamagePercent
        );

        if (totalCritChance > 0 && adjustedDamage > 0) {
            if (combatRandom.nextInt(100) < totalCritChance) {
                criticalHit = true;
                int totalCritBonus = 50 + Math.max(0, totalCritDamagePercent);
                adjustedDamage = increaseByPercent(
                    adjustedDamage,
                    totalCritBonus
                );
            }
        }

        if (totalLifeStealPercent > 0 && adjustedDamage > 0) {
            int healAmount = (int) Math.round(
                adjustedDamage * (totalLifeStealPercent / 100.0)
            );
            if (healAmount <= 0) {
                healAmount = 1;
            }
            int previousLife = life;
            life = Math.min(maxLife, life + healAmount);
            if (life != previousLife) {
                notifyHealthChange();
            }
        }

        return Math.max(0, adjustedDamage);
    }

    private int increaseByPercent(int value, int percent) {
        if (value <= 0 || percent == 0) {
            return Math.max(0, value);
        }
        double multiplier = 1.0 + (percent / 100.0);
        int result = (int) Math.round(value * multiplier);
        if (result <= 0 && value > 0 && percent > 0) {
            return value + 1;
        }
        return Math.max(0, result);
    }

    private int reduceByPercent(int value, int percent) {
        if (value <= 0 || percent <= 0) {
            return Math.max(0, value);
        }
        int clampedPercent = Math.min(95, percent);
        double multiplier = 1.0 - (clampedPercent / 100.0);
        int result = (int) Math.round(value * multiplier);
        if (result < 0) {
            return 0;
        }
        return result;
    }

    private void trackMapVisit() {
        resetTrackingIfMapChanged();
        MapVisitTracker.markVisited(gp.currentMap);
        updateVisitAchievements(gp.currentMap);
    }

    private void resetTrackingIfMapChanged() {
        if (lastTrackedMap == gp.currentMap) {
            return;
        }
        MapKillTracker.reset();
        MapVisitTracker.reset();
        lastTrackedMap = gp.currentMap;
    }

    private void updateVisitAchievements(int mapIndex) {
        int area = gp.currentArea;
        if (mapIndex == 0 || area == gp.outside) {
            recordEvent(EventType.VISIT, 0, 1);
        }
        if (mapIndex == 1 || area == gp.indoor) {
            recordEvent(EventType.VISIT, 1, 1);
        }
        if (mapIndex == 2 || area == gp.dungeon) {
            recordEvent(EventType.VISIT, 2, 1);
        }
    }

    private void updateKillAchievements(int mapIndex, int killsOnMap) {
        int area = gp.currentArea;
        if (mapIndex == 0 || area == gp.outside) {
            recordEvent(EventType.KILL, 0, killsOnMap);
        }
        if (mapIndex == 1 || area == gp.indoor) {
            recordEvent(EventType.KILL, 1, killsOnMap);
        }
        if (mapIndex == 2 || area == gp.dungeon) {
            recordEvent(EventType.KILL, 2, killsOnMap);
        }
    }

    private void recordEvent(EventType eventType, int mapIndex, int value) {
        List<CompositeAchievementNode> snapshot = new ArrayList<>(nodes.values());
        for (CompositeAchievementNode node : snapshot) {
            Template template = node.getTemplate();
            if (!template.matches(eventType, mapIndex)) {
                continue;
            }
            SingleAchievement tracker = node.getTracker();
            boolean wasAchieved = tracker.isAchieved();
            tracker.setProgress(value);
            if (!wasAchieved && tracker.isAchieved()) {
                node.unlockPendingAchievements(
                    nodes,
                    achievementRandom,
                    this::createNode
                );
            }
        }
    }

    private List<Template> resolveChildTemplates(Template template) {
        if (template.childIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Template> resolved = new ArrayList<>();
        for (String id : template.childIds) {
            Template found = TEMPLATE_INDEX.get(id);
            if (found != null) {
                resolved.add(found);
            }
        }
        return resolved;
    }

    public void damageMonster(
        int i,
        Entity attacker,
        int attack,
        int knockBackPower
    ) {
        if (i != 999) {
            Entity target = gp.monster[gp.currentMap][i];

            if (!target.invincible) {
                gp.gameFacade.playSoundEffect(5); // hitmonster.wav

                // Apply knockback if available
                if (knockBackPower > 0) {
                    setKnockBack(target, attacker, knockBackPower);
                }

                // Double damage if monster is off-balance
                if (target.offBalance) {
                    attack *= 2;
                }

                // Calculate damage
                int damage = attack - target.defense;
                if (damage < 1) damage = 1;

                // ✅ Use the takeDamage() method from Entity
                target.takeDamage(damage);

                // Optional: floating UI message (keep if you like your log)
                gp.ui.addMessage(damage + " damage!");

                target.damageReaction();

                // Notify observers (if you use PlayerObserver)
                notifyMonsterDamaged(target, damage);

                // Handle death (count once even if dying was set inside takeDamage)
                if (target.life <= 0 && !target.deathCounted) {
                    target.deathCounted = true;
                    target.dying = true;
                    gp.ui.addMessage("Killed the " + target.name + "!");
                    gp.ui.addMessage("Exp +" + target.exp + "!");
                    int killsOnMap = MapKillTracker.increment(gp.currentMap);
                    updateKillAchievements(gp.currentMap, killsOnMap);
                    exp += target.exp;
                    checkLevelUp();
                }
            }
        }
    }

    public void damageInteractiveTile(int i) {
        if (i == 999) {
            return;
        }

        InteractiveTile tile = gp.iTile[gp.currentMap][i];
        if (tile == null) {
            return;
        }

        TileVisitor visitor = selectTileVisitor();
        if (visitor == null) {
            return;
        }

        tile.accept(visitor);

        if (tile.life <= 0) {
            gp.iTile[gp.currentMap][i] = tile.getDestroyedForm();
        }
    }

    private TileVisitor selectTileVisitor() {
        if (currentWeapon == null) {
            return null;
        }
        if (currentWeapon.type == type_axe) {
            return new ChopVisitor(this);
        }
        if (currentWeapon.type == type_pickaxe) {
            return new SmashVisitor(this);
        }
        return null;
    }

    public void damageProjectile(int i) {
        if (i != 999) {
            Entity projectile = gp.projectile[gp.currentMap][i];
            projectile.alive = false;
            generateParticle(projectile, projectile);
        }
    }

    public void checkLevelUp() {
        while (exp >= nextLevelExp) {
            level++;
            exp = exp - nextLevelExp; //Example: Your exp is 4 and nextLevelExp is 5. You killed a monster and receive 2exp. So, your exp is now 6. Your 1 extra xp will be recovered for the next level.
            if (level <= 4) {
                nextLevelExp = nextLevelExp + 4; //Level 2 to 6: 4xp- 8xp- 12xp- 16xp- 20xp
            } else {
                nextLevelExp = nextLevelExp + 8; //After Level 6: 28xp- 36xp- 44xp- 52xp- 60xp
            }
            maxLife += 2;
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();
            gp.gameFacade.playSoundEffect(8); //levelup.wav

            dialogues[0][0] =
                "You are level " + level + " now!\n" + "You feel stronger!";
            setDialogue();
            startDialogue(this, 0);
            notifyLevelUp();
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(
            gp.ui.playerSlotCol,
            gp.ui.playerSlotRow
        );
        if (itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);

            if (
                selectedItem.type == type_sword ||
                selectedItem.type == type_axe ||
                selectedItem.type == type_pickaxe
            ) {
                currentWeapon = selectedItem;
                attack = getAttack(); //update player attack
                getAttackImage(); //update player attack image (sword/axe)
            }
            if (selectedItem.type == type_shield) {
                currentShield = selectedItem;
                defense = getDefense(); //update player defense
            }
            if (selectedItem.type == type_light) {
                if (currentLight == selectedItem) {
                    currentLight = null;
                } else {
                    currentLight = selectedItem;
                }
                lightUpdated = true;
            }
            if (selectedItem.type == type_consumable) {
                if (selectedItem.use(this) == true) {
                    if (selectedItem.amount > 1) {
                        selectedItem.amount--;
                    } else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }

    public int searchItemInInventory(String itemName) {
        int itemIndex = 999;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).name.equals(itemName)) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
    }

    public boolean canObtainItem(Entity item) {
        boolean canObtain = false;

        Entity newItem = gp.eGenerator.getObject(item.name);

        //CHECK IF STACKABLE
        if (newItem.stackable == true) {
            int index = searchItemInInventory(newItem.name);

            if (index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            } else {
                //New item, so need to check vacancy
                if (inventory.size() != maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        }
        //NOT STACKABLE so check vacancy
        else {
            if (inventory.size() != maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }
        return canObtain;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        // Local player stays centered; remote players are camera-relative.
        int tempScreenX = (this == gp.player) ? screenX : getScreenX();
        int tempScreenY = (this == gp.player) ? screenY : getScreenY();

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
                    tempScreenY = screenY - gp.tileSize; //Adjusted the player's position one tile to up. Explained why I did it at where I call attacking() in update().
                    if (spriteNum == 1) {
                        image = attackUp1;
                    }
                    if (spriteNum == 2) {
                        image = attackUp2;
                    }
                }
                if (guarding == true) {
                    image = guardUp;
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
                if (guarding == true) {
                    image = guardDown;
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
                    tempScreenX = screenX - gp.tileSize; //Adjusted the player's position one tile left. Explained why I did it at where I call attacking() in update().
                    if (spriteNum == 1) {
                        image = attackLeft1;
                    }
                    if (spriteNum == 2) {
                        image = attackLeft2;
                    }
                }
                if (guarding == true) {
                    image = guardLeft;
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
                if (guarding == true) {
                    image = guardRight;
                }
                break;
        }

        //Make player half-transparent (%40) when invincible
        if (transparent == true) {
            g2.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)
            );
        }
        if (
            drawing == true //for boss cutscene making player invisible to move camera.(Cuz camera movement based on player). Only draw the PlayerDummy
        ) {
            g2.drawImage(image, tempScreenX, tempScreenY, null);
        }

        if (name != null && !name.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(Color.white);
            int textWidth = g2.getFontMetrics().stringWidth(name);
            int textX = tempScreenX + (gp.tileSize - textWidth) / 2;
            int textY = tempScreenY - 4;
            g2.drawString(name, textX, textY);
        }

        //Reset graphics opacity / alpha
        g2.setComposite(
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        );

        //DEBUG
        /*g2.setFont(new Font("Arial",Font.PLAIN, 26));
        g2.setColor(Color.white);
        g2.drawString("Invincible:" + invincibleCounter, 10,400);

        g2.setColor(Color.RED);
        g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height); //PLAYER COLLISION CHECKER.RED RECTANGLE.

        tempScreenX = screenX + solidArea.x;
        tempScreenY = screenY + solidArea.y;
        switch(direction) {
            case "up": tempScreenY = screenY - attackArea.height; break;
            case "down": tempScreenY = screenY + gp.tileSize; break;
            case "left": tempScreenX = screenX - attackArea.width; break;
            case "right": tempScreenX = screenX + gp.tileSize; break;
        }
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(tempScreenX, tempScreenY, attackArea.width, attackArea.height);*/
    }

    private enum EventType {
        VISIT,
        KILL
    }

    private static List<Template> createTemplatePool() {
        List<Template> templates = new ArrayList<>();

        // Map 0 - Outside
        templates.add(new Template("map0_visit", 0, EventType.VISIT, "First Footing", "Arrive on the Outside map", 5, 1, 1, true, "map0_first_blood", "map0_pathfinder"));
        templates.add(new Template("map0_first_blood", 0, EventType.KILL, "First Blood (Outside)", "Defeat your first monster outside", 10, 1, 2, true, "map0_hunter", "map0_strider"));
        templates.add(new Template("map0_hunter", 0, EventType.KILL, "Local Hunter", "Defeat multiple mobs on the Outside map", 15, 3, 5, false, "map0_marksman", "map0_ranger"));
        templates.add(new Template("map0_marksman", 0, EventType.KILL, "Marksman's Eye", "Keep the outskirts cleared", 20, 6, 9, false, "map0_watch", "map0_legend"));
        templates.add(new Template("map0_ranger", 0, EventType.KILL, "Ranger of the Cairns", "Stay steady and collect kills", 12, 4, 7, false, "map0_legend"));
        templates.add(new Template("map0_strider", 0, EventType.KILL, "Border Strider", "Hunt while constantly on the move", 14, 2, 4, false, "map0_watch"));
        templates.add(new Template("map0_watch", 0, EventType.KILL, "Watchful Ranger", "Intercept ambush parties on the road", 19, 5, 8, false, "map0_legend"));
        templates.add(new Template("map0_pathfinder", 0, EventType.VISIT, "Trail Pathfinder", "Chart outlying trails and glades", 7, 1, 1, true, "map0_ambusher"));
        templates.add(new Template("map0_ambusher", 0, EventType.KILL, "Ambush Breaker", "Disrupt sudden raids near villages", 16, 3, 5, false, "map0_clearing"));
        templates.add(new Template("map0_clearing", 0, EventType.KILL, "Field Clearing", "Hold an open field against waves", 21, 6, 9, false, "map0_legend"));
        templates.add(new Template("map0_legend", 0, EventType.KILL, "Outside Legend", "Reach legendary kill streaks", 25, 12, 18, false));

        // Map 1 - Indoor
        templates.add(new Template("map1_visit", 1, EventType.VISIT, "House Guest", "Enter the Indoor map", 5, 1, 1, true, "map1_duet", "map1_scout"));
        templates.add(new Template("map1_duet", 1, EventType.KILL, "Duet of Shelters", "Work in pairs to take foes down", 8, 2, 3, true, "map1_sweeper", "map1_broker"));
        templates.add(new Template("map1_sweeper", 1, EventType.KILL, "Hallway Sweeper", "Clear the indoor halls", 15, 5, 7, false, "map1_sentinel", "map1_assault"));
        templates.add(new Template("map1_sentinel", 1, EventType.KILL, "Indoor Sentinel", "Defeat a wave of indoor foes", 22, 8, 12, false, "map1_reclaimer"));
        templates.add(new Template("map1_assault", 1, EventType.KILL, "Coordinated Assault", "Strike multiple indoor foes", 16, 6, 9, false, "map1_reclaimer"));
        templates.add(new Template("map1_reclaimer", 1, EventType.KILL, "Guild Reclaimer", "Earn dominance inside", 28, 14, 18, false));
        templates.add(new Template("map1_scout", 1, EventType.VISIT, "Gallery Scout", "Peek into service hallways", 6, 1, 1, true, "map1_suppressor"));
        templates.add(new Template("map1_suppressor", 1, EventType.KILL, "Suppressing Sweep", "Shut down covert rooms", 18, 4, 6, false, "map1_veteran"));
        templates.add(new Template("map1_veteran", 1, EventType.KILL, "Hall Veteran", "Hold corridors under pressure", 26, 9, 12, false, "map1_reclaimer"));
        templates.add(new Template("map1_broker", 1, EventType.KILL, "Market Broker", "Protect the merchant floor", 12, 3, 4, false, "map1_curator"));
        templates.add(new Template("map1_curator", 1, EventType.KILL, "Relic Curator", "Guard the relic gallery exhibits", 20, 6, 9, false, "map1_guardian"));

        // Map 2 - Dungeon
        templates.add(new Template("map2_visit", 2, EventType.VISIT, "Into the Depths", "Enter the Dungeon map", 10, 1, 1, true, "map2_stalker", "map2_pathfinder"));
        templates.add(new Template("map2_stalker", 2, EventType.KILL, "Dungeon Stalker", "Hunt Dungeon denizens", 25, 8, 11, true, "map2_dredger", "map2_enforcer"));
        templates.add(new Template("map2_dredger", 2, EventType.KILL, "Dungeon Dredger", "Clear deep-level foes", 30, 16, 20, false, "map2_depth_seeker", "map2_juggernaut"));
        templates.add(new Template("map2_depth_seeker", 2, EventType.KILL, "Depth Seeker", "Reach legendary dungeon kills", 40, 22, 28, false));
        templates.add(new Template("map2_pathfinder", 2, EventType.VISIT, "Lower Pathfinder", "Scout the lower antechambers", 14, 1, 1, true, "map2_sentry"));
        templates.add(new Template("map2_sentry", 2, EventType.KILL, "Sentry Subverter", "Disable dungeon sentries", 28, 10, 14, false, "map2_cleanser"));
        templates.add(new Template("map2_enforcer", 2, EventType.KILL, "Depth Enforcer", "Eliminate elite packs", 32, 14, 18, false, "map2_depth_seeker"));
        templates.add(new Template("map2_juggernaut", 2, EventType.KILL, "Juggernaut of Gloom", "Endure a long assault underground", 36, 18, 22, false, "map2_overlord"));

        // Extra achievements per map (extendable without changing logic)
        templates.add(new Template("map0_cleanup", 0, EventType.KILL, "Outskirts Cleanup", "Keep outside clear of stragglers", 18, 7, 10, false, "map0_cull"));
        templates.add(new Template("map0_cull", 0, EventType.KILL, "Cairn Cull", "Drive back lingering threats", 22, 10, 14, false, "map0_purifier"));
        templates.add(new Template("map0_purifier", 0, EventType.KILL, "Purifier of Paths", "Hold the line in the outskirts", 28, 14, 18, false));

        templates.add(new Template("map1_patrol", 1, EventType.KILL, "Hall Patrol", "Secure the halls from intruders", 18, 4, 6, false, "map1_wiper"));
        templates.add(new Template("map1_wiper", 1, EventType.KILL, "Corridor Wiper", "Sweep consecutive foes indoors", 24, 7, 10, false, "map1_guardian"));
        templates.add(new Template("map1_guardian", 1, EventType.KILL, "Hall Guardian", "Maintain control of the corridors", 30, 11, 15, false));

        templates.add(new Template("map2_scout", 2, EventType.VISIT, "Depths Scout", "Chart more of the dungeon routes", 12, 1, 1, false, "map2_cleanser"));
        templates.add(new Template("map2_cleanser", 2, EventType.KILL, "Depths Cleanser", "Stabilize the upper depths", 26, 9, 13, false, "map2_overlord"));
        templates.add(new Template("map2_overlord", 2, EventType.KILL, "Depth Overlord", "Command the deepest paths", 45, 26, 32, false));

        return templates;
    }
    private static Map<String, Template> indexTemplates(List<Template> pool) {
        Map<String, Template> map = new HashMap<>();
        for (Template template : pool) {
            map.put(template.id, template);
        }
        return map;
    }

    private static final class Template {
        private final String id;
        private final int mapIndex;
        private final EventType eventType;
        private final String name;
        private final String description;
        private final int points;
        private final int minRequired;
        private final int maxRequired;
        private final boolean isBase;
        private final List<String> childIds;

        private Template(
            String id,
            int mapIndex,
            EventType eventType,
            String name,
            String description,
            int points,
            int minRequired,
            int maxRequired,
            boolean isBase,
            String... childIds
        ) {
            this.id = id;
            this.mapIndex = mapIndex;
            this.eventType = eventType;
            this.name = name;
            this.description = description;
            this.points = points;
            this.minRequired = minRequired;
            this.maxRequired = maxRequired;
            this.isBase = isBase;
            this.childIds = Collections.unmodifiableList(
                Arrays.asList(childIds)
            );
        }

        private int randomRequired(Random random) {
            int min = Math.max(1, minRequired);
            int max = Math.max(min, maxRequired);
            return min + random.nextInt(max - min + 1);
        }

        private boolean matches(EventType eventType, int mapIndex) {
            return this.eventType == eventType && this.mapIndex == mapIndex;
        }
    }

    private static final class CompositeAchievementNode extends AchievementGroup {
        private final Template template;
        private final SingleAchievement tracker;
        private final List<Template> pendingChildTemplates;

        private CompositeAchievementNode(
            Template template,
            int required,
            List<Template> pendingChildTemplates
        ) {
            super(template.name, template.description);
            this.template = template;
            this.tracker = new SingleAchievement(
                template.name,
                template.description,
                template.points,
                required
            );
            this.pendingChildTemplates = new ArrayList<>(pendingChildTemplates);
            super.add(tracker);
        }

        private Template getTemplate() {
            return template;
        }

        private SingleAchievement getTracker() {
            return tracker;
        }

        private void unlockPendingAchievements(
            Map<String, CompositeAchievementNode> activeNodes,
            Random random,
            Function<Template, CompositeAchievementNode> factory
        ) {
            if (pendingChildTemplates.isEmpty()) {
                return;
            }

            List<Template> candidates = new ArrayList<>();
            for (Template childTemplate : pendingChildTemplates) {
                if (!activeNodes.containsKey(childTemplate.id)) {
                    candidates.add(childTemplate);
                }
            }
            if (candidates.isEmpty()) {
                return;
            }

            Collections.shuffle(candidates, random);
            int maxAdds = Math.max(1, Math.min(3, candidates.size()));
            int addCount = 1 + random.nextInt(maxAdds);

            for (int i = 0; i < addCount; i++) {
                Template pick = candidates.get(i);
                CompositeAchievementNode childNode = factory.apply(pick);
                if (!getChildren().contains(childNode)) {
                    add(childNode);
                }
            }
        }
    }
}
