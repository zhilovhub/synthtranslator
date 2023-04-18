package com.example.synthtranslator.recorder;

import android.media.AudioRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.synthtranslator.AudioAnalyzer;

/**
 * Класс, отвечающий за запись звука с источника (микрофона)
 */
public class VoiceRecorder {
    private final AudioAnalyzer audioAnalyzer;

    private final Capturer capturerThread = new Capturer();
    private volatile AudioRecord recorder;

    private volatile boolean recordFlag = true;

    private int maxAmplitude = 0;

    /**
     * Constructor
     * @param recorder AudioRecord
     */
    public VoiceRecorder(AudioRecord recorder) {
        this.recorder = recorder;
        audioAnalyzer = new AudioAnalyzer(30, recorder.getSampleRate(),
                recorder.getAudioFormat(), recorder.getChannelCount());
    }

    /**
     * Updates recorder's AudioRecord object
     * @param recorder new AudioRecord
     */
    public void updateAudioRecord(AudioRecord recorder) {
        this.recorder = recorder;
    }

    /**
     * Thread for recording audio
     */
    private final class Capturer extends Thread {
        public void run() {
            byte[] temp_buffer = new byte[960];

            while (recorder.read(temp_buffer, 0, temp_buffer.length) != -1 && !isInterrupted()) {
                if (recordFlag) {
                    try {
                        maxAmplitude = maxFromBuffer(temp_buffer);
                        audioAnalyzer.feedRecordedRawSignal(temp_buffer, false);
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                }
            }
        }

        /**
         * Calculates max amplitude value from byte buffer
         * @param buffer ByteBuffer
         * @return max amplitude value
         */
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

    /**
     * Transform two byte values into one short value (little-endian)
     * @param b1 small byte
     * @param b2 big byte
     * @return short value of two bytes
     */
    private short getShort(byte b1, byte b2) {
        return (short) (b1 | (b2 << 8));
    }

    /**
     * Starts recorder thread
     */
    public void startRecording() {
        capturerThread.start();
    }

    /**
     * Calculates how mush seconds have already recorded
     * @return seconds count
     */
    public float getAvailableSecondsOfCapturing() {
        return audioAnalyzer.getAvailableSecondsOfCapturing();
    }

    /**
     * Returns recorded speech
     * @return ByteArrayOutputStream of audio
     */
    public ByteArrayOutputStream getVoiceStream() {
        ByteArrayOutputStream temp = audioAnalyzer.getVoiceStream();
        return temp;
    }

    /**
     * Sets recordFlag to True
     */
    public void continueRecording() {
        recordFlag = true;
    }

    /**
     * Releases AudioRecord, clears recorded buffer and sets recordFlag to False
     */
    public void stopRecording() {
        recordFlag = false;
        recorder.stop();
        recorder.release();
        audioAnalyzer.clearBuffer();
    }

    /**
     * Close every resource. Recorder unable after calling this method
     */
    public void closeResources() {
        try {
            capturerThread.interrupt();
        } catch (Exception ignore) { }
        try {
            recorder.release();
        } catch (Exception ignore) { }
    }

    /**
     * @return max amplitude value by the current moment
     */
    public int getAmplitude() {
        return maxAmplitude;
    }
}
