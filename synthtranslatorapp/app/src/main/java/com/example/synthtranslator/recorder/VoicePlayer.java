package com.example.synthtranslator.recorder;

import android.media.AudioTrack;

import java.io.InputStream;
import java.io.IOException;


public class VoicePlayer {
    private final Player playerThread = new Player();
    private volatile AudioTrack player;

    private volatile boolean playFlag = true;
    private volatile InputStream inputStream;

    public VoicePlayer(AudioTrack player) {
        this.player = player;
    }

    /**
     * Thread for playing InputStream
     */
    private final class Player extends Thread {
        public void run() {
            byte[] buffer = new byte[4096];
            int cnt;

            try {
                while (!this.isInterrupted()) {
                    if (inputStream != null && playFlag) {
                        cnt = inputStream.read(buffer, 0, buffer.length);
                        if (cnt != -1) {
                            player.write(buffer, 0, cnt);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Updates InputStream for playing
     * @param inputStream InputStream which should be played
     */
    public void updateInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Releases AudioTrack and turns off point of the PlayerThread
     */
    public void stopPlaying() {
        playFlag = false;
        player.stop();
        player.release();
        inputStream = null;
    }

    /**
     * Updates Player's AudioTrack (for example after realise)
     * @param player new created AudioTrack
     */
    public void updateAudioTrack(AudioTrack player) {
        this.player = player;
    }

    /**
     * Starts playerThread
     */
    public void startPlaying() {
        playerThread.start();
    }

    /**
     * @return Second that is not played yet
     */
    public float getAvailableSecondsOfPlaying() {
        int availableFrames = getAvailableFramesOfPlaying();
        int sampleRate = player.getSampleRate();

        return availableFrames * (1f / sampleRate);
    }

    /**
     *
     * @return Frames count that is not played yet
     */
    private int getAvailableFramesOfPlaying() {
        int availableBytes = 0;
        int audioFormatBytes = 2;
        int channelCount = player.getChannelCount();

        try {
            if (inputStream != null) {
                availableBytes = inputStream.available();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return availableBytes / audioFormatBytes / channelCount;
    }

    /**
     * Close every resource. Player unable after calling this method
     */
    public void closeResources() {
        playFlag = false;
        try {
            playerThread.interrupt();
        } catch (Exception ignore) {}
        try {
            player.release();
        } catch (Exception ignore) {}
        try {
            inputStream.close();
        } catch (Exception ignore) {}
    }
}
