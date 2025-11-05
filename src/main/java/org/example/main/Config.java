package org.example.main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Config {
    GamePanel gp;
    private static Config instance;

    private Config(GamePanel gp) {
        this.gp = gp;
    }
    public static Config getInstance(GamePanel gp) {
        if (instance == null) {
            instance = new Config(gp);
        }
        return instance;
    }

    public void saveConfig()
    {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));

            // Full Screen
            if(gp.fullScreenOn == true)
            {
                bw.write("On");
            }
            if(gp.fullScreenOn == false)
            {
                bw.write("Off");
            }
            bw.newLine();

            //Music Volume
            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            //SE Volume
            bw.write(String.valueOf(gp.se.volumeScale));
            bw.newLine();

            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.txt")) {

            if (input == null) {
                throw new FileNotFoundException("config.txt not found in resources folder!");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String s = br.readLine();

            // Full Screen
            if ("On".equalsIgnoreCase(s)) {
                gp.fullScreenOn = true;
            } else {
                gp.fullScreenOn = false;
            }

            // Music Volume
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);

            // SE Volume
            s = br.readLine();
            gp.se.volumeScale = Integer.parseInt(s);

            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
