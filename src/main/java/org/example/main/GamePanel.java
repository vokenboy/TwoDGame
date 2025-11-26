package org.example.main;

import org.example.ai.PathFinder;
import org.example.data.SaveLoad;
import org.example.entity.Entity;
import org.example.entity.Player;
import org.example.environment.EnvironmentManager;
import org.example.tile.Map;
import org.example.tile.TileManager;
import org.example.tile_interactive.InteractiveTile;
import org.example.Main;
import org.example.tile_interactive.InteractiveTileFactory;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable {
    //SCREEN SETTINGS
    public List<DamageNumber> damageNumbers = new ArrayList<>();
    final int originalTileSize = 16; // 16*16  tile. default
    final int scale = 3; // 16*3 scale

    public final int tileSize = originalTileSize * scale; // 48*48 tile // public cuz we use it in Player Class
    public final int maxScreenCol = 20; // 4:3 window
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;  //48*20 = 960 pixels
    public final int screenHeight = tileSize * maxScreenRow;  //48*12 = 576 pixels  // GAME SCREEN SIZE

    //WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    //FOR FULLSCREEN
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = false;


    //FPS
    int FPS = 60;

    //SYSTEM
    public EntityManager entityManager = new EntityManager(this);
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler(this);
    public EventHandler eHandler = new EventHandler(this);
    Sound music = new Sound(); // Created 2 different objects for Sound Effect and Music. If you use 1 object SE or Music stops sometimes.
    Sound se = new Sound();
    public GameFacade gameFacade; // Facade Pattern - unified interface for audio and collision
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter  aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Config config = Config.getInstance(this);
    public PathFinder pFinder = new PathFinder(this);
    EnvironmentManager eManager = new EnvironmentManager(this);
    Map map = new Map(this);
    SaveLoad saveLoad = new SaveLoad(this);
    public EntityGenerator eGenerator = new EntityGenerator(this);
    public CutsceneManager csManager = new CutsceneManager(this);
    Thread gameThread;

    //ENTITY AND OBJECT
    public Player player = new Player(this,keyH);
    public Entity obj[][] = new Entity[maxMap][20]; // display 10 objects same time
    public Entity npc[][] = new Entity[maxMap][10];
    public Entity monster[][] = new Entity[maxMap][20];
    public InteractiveTile iTile[][] = new InteractiveTile[maxMap][50];
    public Entity projectile[][] = new Entity[maxMap][20]; // cut projectile
    //public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();
    public InteractiveTileFactory iTileFactory;


    //GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int optionsState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;
    public final int sleepState = 9;
    public final int mapState = 10;
    public final int cutsceneState = 11;
    public final int enchantState = 12;

    //OTHERS
    public boolean bossBattleOn = false;

    //AREA
    public int currentArea;
    public int nextArea;
    public final int outside = 50;
    public final int indoor = 51;
    public final int dungeon = 52;


    public GamePanel() // constructor
    {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // JPanel size
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // improve game's rendering performance
        this.setFocusable(true);

        this.iTileFactory = new InteractiveTileFactory(this);
        this.gameFacade = new GameFacade(this, music, se); // Initialize facade
    }

    public void setupGame()
    {
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setInteractiveTile();
        eManager.setup();

        gameState = titleState;
        //FOR FULLSCREEN
        tempScreen = new BufferedImage(screenWidth,screenHeight,BufferedImage.TYPE_INT_ARGB); //blank screen
        g2 = (Graphics2D) tempScreen.getGraphics(); // g2 attached to this tempScreen. g2 will draw on this tempScreen buffered image.
        if(fullScreenOn == true)
        {
            setFullScreen();
        }
    }

    public void resetGame(boolean restart)
    {
        gameFacade.stopBackgroundMusic();
        currentArea = outside;
        removeTempEntity();
        bossBattleOn = false;
        player.setDefaultPositions();
        player.restoreStatus();
        aSetter.setMonster();
        aSetter.setNPC();
        player.resetCounter();

        if(restart == true)
        {
            player.setDefaultValues();
            aSetter.setObject();
            aSetter.setInteractiveTile();
            eManager.lighting.resetDay();
            gameFacade.stopBackgroundMusic();
        }
    }

    public void setFullScreen()
    {
        //GET LOCAL SCREEN DEVICE
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        //GET FULL SCREEN WIDTH AND HEIGHT
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window.getHeight();
    }

    public void startGameThread()
    {
        gameThread = new Thread(this);
        gameThread.start(); // run'ı cagirir
    }

    @Override
    public void run()
    {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null)
        {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if(delta >= 1)
            {
                update();
                drawToTempScreen(); //FOR FULL SCREEN - Draw everything to the buffered image
                drawToScreen();     //FOR FULL SCREEN - Draw the buffered image to the screen
                delta--;
            }
        }
    }

    public void update()
    {
        if(gameState == playState)
        {
            //PLAYER
            player.update();

            //NPC
            var itNPC = entityManager.getNPCIterator();
            while (itNPC.hasNext()) {
                itNPC.next().update();
            }


            //MONSTER
            var it = entityManager.getMonsterIterator();
            while (it.hasNext()) {
                Entity m = it.next();

                if (m.alive && !m.dying) {
                    m.update();
                } else if (!m.alive) {
                    m.checkDrop();
                    entityManager.removeMonster(m);
                }
            }


            //PROJECTILE
            for(int i = 0; i < projectile[1].length; i++)
            {
                if(projectile[currentMap][i] != null)
                {
                    if(projectile[currentMap][i].alive == true)
                    {
                        projectile[currentMap][i].update();
                    }
                    if(projectile[currentMap][i].alive == false)
                    {
                        projectile[currentMap][i] = null;
                    }
                }
            }

            // DAMAGE NUMBERS
            for (int i = 0; i < damageNumbers.size(); i++) {
                DamageNumber dn = damageNumbers.get(i);
                if (dn != null) {
                    dn.update();
                    if (!dn.alive) {
                        damageNumbers.remove(i);
                        i--;
                    }
                }
            }

            //PARTICLE
            for(int i = 0; i < particleList.size(); i++)
            {
                if(particleList.get(i)!= null)
                {
                    if(particleList.get(i).alive == true)
                    {
                        particleList.get(i).update();
                    }
                    if(particleList.get(i).alive == false)
                    {
                        particleList.remove(i);
                    }
                }
            }

            //INTERACTIVE TILE
            for(int i = 0; i < iTile[1].length; i++)
            {
                if(iTile[currentMap][i] != null)
                {
                    iTile[currentMap][i].update();
                }
            }

            eManager.update();
        }

        keyH.update();

        if(gameState == pauseState)
        {
            //nothing, just pause screen
        }
    }

    //FOR FULL SCREEN (FIRST DRAW TO TEMP SCREEN INSTEAD OF JPANEL)
    public void drawToTempScreen()
    {
        //DEBUG
        long drawStart = 0;
        if(keyH.showDebugText == true)
        {
            drawStart = System.nanoTime();
        }

        //TITLE SCREEN
        if(gameState == titleState)
        {
            ui.draw(g2);
        }
        //MAP SCREEN
        else if(gameState == mapState)
        {
            map.drawFullMapScreen(g2);
        }
        //OTHERS
        else
        {
            //TILE
            tileM.draw(g2);

            //INTERACTIVE TILE
            for(int i = 0; i < iTile[1].length; i++)
            {
                if(iTile[currentMap][i] != null)
                {
                    iTile[currentMap][i].draw(g2);
                }
            }

            //ADD ENTITIES TO THE LIST
            //PLAYER
            entityList.add(player);

            //NPCs
            var itNPC = entityManager.getNPCIterator();
            while (itNPC.hasNext()) {
                entityList.add(itNPC.next());
            }


            //OBJECTS
            var itObj = entityManager.getObjectIterator();
            while (itObj.hasNext()) {
                entityList.add(itObj.next());
            }

            //MONSTERS
            var itMon = entityManager.getMonsterIterator();
            while (itMon.hasNext()) {
                entityList.add(itMon.next());
            }

            //PROJECTILES
            for(int i = 0; i < projectile[1].length; i++)
            {
                if(projectile[currentMap][i] != null)
                {
                    entityList.add(projectile[currentMap][i]);
                }
            }

            // DAMAGE NUMBERS
            for (DamageNumber dn : damageNumbers) {
                dn.draw(g2, this);
            }

            //PARTICLES
            for(int i = 0; i < particleList.size(); i++)
            {
                if(particleList.get(i) != null)
                {
                    entityList.add(particleList.get(i));
                }
            }

            //SORT
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
            });

            //DRAW ENTITIES
            for(int i = 0; i < entityList.size(); i++)
            {
                entityList.get(i).draw(g2);
            }

            //EMPTY ENTITY LIST
            entityList.clear();

            //ENVIRONMENT
            eManager.draw(g2);

            //MINI MAP
            map.drawMiniMap(g2);

            //CUTSCENE
            csManager.draw(g2);

            //UI
            ui.draw(g2);

            //DEBUG
            if(keyH.showDebugText == true)
            {
                long drawEnd = System.nanoTime();
                long passed = drawEnd - drawStart;

                g2.setFont(new Font("Arial", Font.PLAIN,20));
                g2.setColor(Color.white);
                int x = 10;
                int y = 400;
                int lineHeight = 20;

                g2.drawString("WorldX " + player.worldX,x,y);
                y+= lineHeight;
                g2.drawString("WorldY " + player.worldY,x,y);
                y+= lineHeight;
                g2.drawString("Col " + (player.worldX + player.solidArea.x) / tileSize,x,y);
                y+= lineHeight;
                g2.drawString("Row " + (player.worldY + player.solidArea.y) / tileSize,x,y);
                y+= lineHeight;
                g2.drawString("Map " + currentMap,x,y);
                y+= lineHeight;
                g2.drawString("Draw time: " + passed,x,y);
                y+= lineHeight;
                g2.drawString("God Mode: " + keyH.godModeOn, x, y);
            }
        }
    }

    public Config getConfig() {
        return config;
    }

    public void drawToScreen()
    {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0,screenWidth2,screenHeight2,null);
        g.dispose();
    }

    //COMMENTED FOR FULLSCREEN
    /*public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g; // Graphics2D extends Graphics class

        //DEBUG
        long drawStart = 0;
        if(keyH.showDebugText == true)
        {
            drawStart = System.nanoTime();
        }

        //TITLE SCREEN
        if(gameState == titleState)
        {
            ui.draw(g2);
        }
        //OTHERS
        else
        {
            //TILE
            tileM.draw(g2);

            //INTERACTIVE TILE
            for(int i = 0; i < iTile.length; i++)
            {
                if(iTile[i] != null)
                {
                    iTile[i].draw(g2);
                }
            }

            //ADD ENTITIES TO THE LIST
            //PLAYER
            entityList.add(player);

            //NPCs
            for(int i = 0; i < npc.length; i++)
            {
                if(npc[i] != null)
                {
                    entityList.add(npc[i]);
                }
            }

            //OBJECTS
            for(int i = 0; i < obj.length; i++)
            {
                if(obj[i] != null)
                {
                    entityList.add(obj[i]);
                }
            }

            //MONSTERS
            for(int i = 0; i < monster.length; i++)
            {
                if(monster[i] != null)
                {
                    entityList.add(monster[i]);
                }
            }

            //PROJECTILES
            for(int i = 0; i < projectileList.size(); i++)
            {
                if(projectileList.get(i) != null)
                {
                    entityList.add(projectileList.get(i));
                }
            }

            //PARTICLES
            for(int i = 0; i < particleList.size(); i++)
            {
                if(particleList.get(i) != null)
                {
                    entityList.add(particleList.get(i));
                }
            }

            //SORT
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);   // result returns : (x=y : 0, x>y : >0, x<y : <0)
                    return result;
                }
            });

            //DRAW ENTITIES
            for(int i = 0; i < entityList.size(); i++)
            {
                entityList.get(i).draw(g2);
            }

            //EMPTY ENTITY LIST
            entityList.clear();

            //UI
            ui.draw(g2);

            //DEBUG

            if(keyH.showDebugText == true)
            {
                long drawEnd = System.nanoTime();
                long passed = drawEnd - drawStart;

                g2.setFont(new Font("Arial", Font.PLAIN,20));
                g2.setColor(Color.white);
                int x = 10;
                int y = 400;
                int lineHeight = 20;

                g2.drawString("WorldX " + player.worldX,x,y);
                y+= lineHeight;
                g2.drawString("WorldY " + player.worldY,x,y);
                y+= lineHeight;
                g2.drawString("Col " + (player.worldX + player.solidArea.x) / tileSize,x,y);
                y+= lineHeight;
                g2.drawString("Row " + (player.worldY + player.solidArea.y) / tileSize,x,y);
                y+= lineHeight;

                g2.drawString("Draw time : " + passed,x,y);
            }
            g2.dispose();
        }
    }*/

    public void changeArea()
    {
        if(nextArea != currentArea)
        {
            gameFacade.stopBackgroundMusic();

            if(nextArea == outside)
            {
                gameFacade.playBackgroundMusic(0);
            }
            if(nextArea == indoor)
            {
                gameFacade.playBackgroundMusic(18);
            }
            if(nextArea == dungeon)
            {
                gameFacade.playBackgroundMusic(19);
            }
            aSetter.setNPC();
        }

        currentArea = nextArea;
        aSetter.setMonster();
    }

    public void removeTempEntity()
    {
        for(int mapNum = 0; mapNum < maxMap; mapNum++)
        {
            for(int i = 0; i < obj[1].length; i++)
            {
                if(obj[mapNum][i] != null && obj[mapNum][i].temp == true)
                {
                    obj[mapNum][i] = null;
                }
            }
        }
    }
}