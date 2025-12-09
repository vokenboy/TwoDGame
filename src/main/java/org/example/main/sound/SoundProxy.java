package org.example.main.sound;

import org.example.main.GamePanel;

public class SoundProxy implements SoundInterface {

    private final SoundInterface realSound;
    private final GamePanel gp;

    private boolean musicLooping = false;
    private long lastSfxTime = 0;
    private int lastSfxIndex = -1;
    private int currentIndex = -1;
    private static final long SFX_COOLDOWN = 120; // ms
    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 5;

    public SoundProxy(SoundInterface realSound, GamePanel gp) {
        this.realSound = realSound;
        this.gp = gp;
    }

    private boolean isSoundAllowed() {

        return true;
    }

    @Override
    public void setFile(int index) {
        if (index < 0) return;
        currentIndex = index;
        musicLooping = false;
        realSound.setFile(index);
    }


    @Override
    public void play() {
        if (!isSoundAllowed()) return;

        long now = System.currentTimeMillis();

        //uztikrina kad nebutu to pacio garso spamo
        if (lastSfxIndex == currentIndex &&
                now - lastSfxTime < SFX_COOLDOWN) {
            return;
        }

        lastSfxIndex = currentIndex;
        lastSfxTime = now;

        realSound.play();
    }

    public void loop() {
        if (!isSoundAllowed()) return;

        // uztikrinama, kad groja tik viena
        if (musicLooping) {
            stop();
        }

        realSound.stop();
        musicLooping = false;

        realSound.loop();
        musicLooping = true;
    }


    @Override
    public void stop() {
        realSound.stop();
        musicLooping = false;
        lastSfxIndex = -1;
    }

    public void fullReset() {
        realSound.stop();
        musicLooping = false;
        lastSfxIndex = -1;
        currentIndex = -1;
    }

    @Override
    public void setVolumeScale(int scale) {
        if (scale < MIN_VOLUME) scale = MIN_VOLUME;
        if (scale > MAX_VOLUME) scale = MAX_VOLUME;
        realSound.setVolumeScale(scale);
    }


    @Override
    public int getVolumeScale() {
        return realSound.getVolumeScale();
    }

}
