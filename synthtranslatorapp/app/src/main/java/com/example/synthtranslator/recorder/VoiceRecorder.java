package com.example.synthtranslator.recorder;

import android.media.AudioRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VoiceRecorder {
    private final Capturer capturerThread = new Capturer();
    private volatile AudioRecord recorder;

    private volatile boolean recordFlag = true;
    private volatile ByteArrayOutputStream byte_output_stream;

    private int maxAmplitude = 0;

    public VoiceRecorder(AudioRecord recorder) {
        this.recorder = recorder;
    }

    public void updateAudioRecord(AudioRecord recorder) {
        this.recorder = recorder;
    }

    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[1024 * 2];
            byte_output_stream = new ByteArrayOutputStream();
            int cnt;

            while ((cnt = recorder.read(temp_buffer, 0, temp_buffer.length)) != -1 && !isInterrupted()) {
                if (recordFlag) {
                    try {
                        maxAmplitude = maxFromBuffer(temp_buffer);
                        byte_output_stream.write(temp_buffer, 0, cnt);
                    } catch (IndexOutOfBoundsException ignored) {

                    }
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

    public void startRecording() {
        capturerThread.start();
    }

    public int getAvailableBytesOfCapturing() {
        int availableBytes = 0;

        if (byte_output_stream != null) {
            availableBytes = byte_output_stream.size();
        }
        return availableBytes;
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

    public void continueRecording() {
        recordFlag = true;
    }

    public void stopRecording() {
        recordFlag = false;
        recorder.stop();
        recorder.release();
        byte_output_stream.reset();
    }

    public void closeResources() {
        try {
            capturerThread.interrupt();
        } catch (Exception ignore) { }
        try {
            recorder.release();
        } catch (Exception ignore) { }
        try {
            byte_output_stream.close();
        } catch (Exception ignore) { }
    }

    public int getAmplitude() {
        return maxAmplitude;
    }
}
