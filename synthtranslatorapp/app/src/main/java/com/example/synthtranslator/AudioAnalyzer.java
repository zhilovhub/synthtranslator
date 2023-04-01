package com.example.synthtranslator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import be.tarsos.dsp.util.fft.FFT;

public class AudioAnalyzer {
    private final int FFTWindowDurationMS;
    private final int SizeFFT = 200;

    private final int sampleRate;
    private final int audioFormatBytes;
    private final int channelCount;

    private FFT fft = new FFT(SizeFFT);
    private ArrayList<float[]> signalsFFT = new ArrayList<>();

    public AudioAnalyzer(int FFTWindowsDurationMS, int sampleRate, int audioFormatBytes,
                         int channelCount) {
        this.FFTWindowDurationMS = FFTWindowsDurationMS;
        this.sampleRate = sampleRate;
        this.audioFormatBytes = audioFormatBytes;
        this.channelCount = channelCount;
    }

    public void feedRecordedRawSignal(byte[] byteBuffer, boolean bigEndian) {
        short[] shortBuffer = getShort(byteBuffer, bigEndian);
        float[] floatBuffer = new float[shortBuffer.length];

        for (int i = 0; i < shortBuffer.length; i++) {
            floatBuffer[i] = shortBuffer[i];
        }

        transferSignalToFFT(floatBuffer);
        signalsFFT.add(floatBuffer);
    }

    private void transferSignalToFFT(float[] floatBuffer) {
        fft.forwardTransform(floatBuffer);
    }

    private void transferFFTToSignal(float[] floatBuffer) {
        fft.backwardsTransform(floatBuffer);
    }

    public float getAvailableSecondsOfCapturing() {
        return signalsFFT.size() * (FFTWindowDurationMS / 1000f);
    }

    public ByteArrayOutputStream getVoiceStream() {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();

        for (float[] signalFFT : signalsFFT) {
            transferFFTToSignal(signalFFT);
            byte[] byteBuffer = new byte[signalFFT.length * 2];
            for (float i : signalFFT) {

            }
        }
    }

    /**
     *
     * @param byteBuffer array of audio byte values
     * @param bigEndian indicates whether the data for a single sample is stored in big-endian byte order
     *                  (false means little-endian)
     * @return shortBuffer instead of byteBuffer
     */
    private short[] getShort(byte[] byteBuffer, boolean bigEndian) {
        short[] shortBuffer = new short[byteBuffer.length / 2];

        if (bigEndian) {
            for (int i = 0; i < byteBuffer.length / 2; i++) {
                shortBuffer[i] = (short) (byteBuffer[i * 2] << 8 | (byteBuffer[i * 2 + 1]));
            }
        } else {
            for (int i = 0; i < byteBuffer.length / 2; i++) {
                shortBuffer[i] = (short) (byteBuffer[i * 2] | (byteBuffer[i * 2 + 1] << 8));
            }
        }

        return shortBuffer;
    }

    public ByteArrayInputStream copyFromInputStream(InputStream is) {
        ByteArrayOutputStream tempByteOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[16384];
        int cnt;

        try {
            while ((cnt = is.read(buffer, 0, buffer.length)) != -1) {
                tempByteOutputStream.write(buffer, 0, cnt);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return new ByteArrayInputStream(tempByteOutputStream.toByteArray());
    }
}
