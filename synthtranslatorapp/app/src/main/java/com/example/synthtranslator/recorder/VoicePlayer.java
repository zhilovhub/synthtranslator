package com.example.synthtranslator.recorder;

import android.media.AudioTrack;

import java.io.InputStream;
import java.io.IOException;


public class VoicePlayer {
    private volatile boolean playFlag = true;

    private volatile InputStream inputStream;

    private final class Player extends Thread {
        public void run() {
            byte[] buffer = new byte[4096];
            int cnt;

            try {
                while (!this.isInterrupted()) {
                    if (playFlag) {
                        throw new IOException();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }
}
