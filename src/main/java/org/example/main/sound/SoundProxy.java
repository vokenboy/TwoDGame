package org.example.main.sound;

import org.example.main.GamePanel;

public class SoundProxy implements SoundInterface {

    private final SoundInterface realSound;
    private final GamePanel gp;

    public SoundProxy(SoundInterface realSound, GamePanel gp) {
        this.realSound = realSound;
        this.gp = gp;
    }

    private boolean isSoundAllowed() {
        // here you can use gp.config or gameState to mute/limit sounds
        // for now just always allow
        return true;
    }

    @Override
    public void setFile(int index) {
        realSound.setFile(index);
    }

    @Override
    public void play() {
        if (isSoundAllowed()) {
            realSound.play();
        }
    }

    @Override
    public void loop() {
        if (isSoundAllowed()) {
            realSound.loop();
        }
    }

    @Override
    public void stop() {
        realSound.stop();
    }

    @Override
    public void setVolumeScale(int scale) {
        realSound.setVolumeScale(scale);
    }

    @Override
    public int getVolumeScale() {
        return realSound.getVolumeScale();
    }
}
