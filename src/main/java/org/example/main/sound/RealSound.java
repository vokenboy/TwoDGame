package org.example.main.sound;

import org.example.main.Sound;

public class RealSound implements SoundInterface {

    private final Sound sound;

    public RealSound(Sound sound) {
        this.sound = sound;
    }

    @Override
    public void setFile(int index) {
        sound.setFile(index);
    }

    @Override
    public void play() {
        sound.play();
    }

    @Override
    public void loop() {
        sound.loop();
    }

    @Override
    public void stop() {
        sound.stop();
    }

    @Override
    public void setVolumeScale(int scale) {
        sound.setVolumeScale(scale);
    }

    @Override
    public int getVolumeScale() {
        return sound.getVolumeScale();
    }
}
