package org.example.main.sound;

public interface SoundInterface {

    // load which sound to use
    void setFile(int index);

    // play once
    void play();

    // loop continuously
    void loop();

    // stop playing
    void stop();

    // volume handling
    void setVolumeScale(int scale);
    int getVolumeScale();
}
