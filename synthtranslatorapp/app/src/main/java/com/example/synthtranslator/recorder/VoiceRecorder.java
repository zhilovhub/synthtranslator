package com.example.synthtranslator.recorder;

import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturer = new Capturer();
    private final Player playerThread = new Player();

    private volatile AudioRecord recorder;
    private volatile AudioTrack player;

    private volatile InputStream input_stream;
    private volatile ByteArrayOutputStream byte_output_stream;

    private volatile boolean recordFlag = true;
    private volatile boolean playFlag = true;

    private int maxAmplitude = 0;

    public void setAudioInstruments(AudioRecord recorder, AudioTrack player) {
        this.recorder = recorder;
        this.player = player;
    }

    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * 2];
            byte_output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = recorder.read(temp_buffer, 0, temp_buffer.length)) != -1 && !interrupted()) {
                if (recordFlag)
                    try {
                        maxAmplitude = maxFromBuffer(temp_buffer);
                        byte_output_stream.write(temp_buffer, 0, cnt);
                    } catch (IndexOutOfBoundsException ignored) {

                    }
            }
        }

        private int maxFromBuffer(byte[] buffer) {
            short maxValue = 0;

            for (int i = 0; i < buffer.length / 2; i++) {
                short curSample = getShort(buffer[i * 2], buffer[i * 2 + 1]);
                if (curSample > maxValue) {
                    maxValue = curSample;
                }
            }

            return maxValue;
        }
    }

    private short getShort(byte b1, byte b2) {
        return (short) (b1 | (b2 << 8));
    }

    private final class Player extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[4096];
            int cnt;

            try {
                while (!isInterrupted()) {
                    if (input_stream != null && playFlag) {
                        cnt = input_stream.read(temp_buffer, 0, temp_buffer.length);
                        if (cnt != -1) {
                            player.write(temp_buffer, 0, cnt);
                        }
                    }

                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void captureAudioThreadStart() {
        capturer.start();
    }

    public void playAudioThreadStart() {
        playerThread.start();
    }

    public int getAvailableBytesOfCapturing() {
        int availableBytes = 0;

        if (byte_output_stream != null) {
            availableBytes = byte_output_stream.size();
        }
        return availableBytes;
    }

    public int getAvailableBytesOfSynthesizing() {
        int available_bytes = 0;

        try {
            if (this.input_stream != null)
                available_bytes = this.input_stream.available();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return available_bytes;
    }

    public ByteArrayOutputStream getVoiceStream() {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();

        try {
            temp.write(this.byte_output_stream.toByteArray());
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        this.byte_output_stream.reset();
        return temp;
    }

    public void updateAudioStream(InputStream is) {
        input_stream = is;
    }

    public void continueAudioInstruments() {
        recordFlag = true;
        playFlag = true;
    }

    public void pauseAudioInstruments() {
        recordFlag = false;
        playFlag = false;
        recorder.stop();
        player.stop();
        recorder.release();
        player.release();
        byte_output_stream.reset();
        input_stream = null;
    }

    public void closeResources() {
        try {
            playerThread.interrupt();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            capturer.interrupt();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            player.release();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            recorder.release();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            input_stream.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            byte_output_stream.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public int getAmplitude() {
        return maxAmplitude;
    }
}
