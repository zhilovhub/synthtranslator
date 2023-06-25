package com.example.synthtranslator.recorder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import be.tarsos.dsp.util.fft.FFT;

public class AudioAnalyzer {
    private final int FFTWindowDurationMS;
    private final int SizeFFT = 200;

    private final int sampleRate;
    private final int audioFormatBytes;
    private final int channelCount;

    private final FFT fft = new FFT(SizeFFT);
    private final ArrayList<float[]> signalsFFT = new ArrayList<>();

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
        markVoiceUnvoicedPart(floatBuffer);
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
        int currentLength = signalsFFT.size();

        for (int i = 0; i < currentLength; i++) {
            float[] signalFFT = signalsFFT.get(i);

            transferFFTToSignal(signalFFT);
            byte[] byteBuffer = new byte[signalFFT.length * 2];
            for (int j = 0; j < signalFFT.length; j++) {
                short signalFrame = (short) signalFFT[j];
                byteBuffer[j * 2] = (byte) (signalFrame & 0xff);
                byteBuffer[j * 2 + 1] = (byte) ((signalFrame >> 8) & 0xff);
            }

            try {
                temp.write(byteBuffer);
            } catch (IOException ignored) { }
        }

        signalsFFT.subList(0, currentLength).clear();

        return temp;
    }

    /**
     * Clears all AudioAnalyzer's buffers
     */
    public void clearBuffer() {
        signalsFFT.clear();
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
                shortBuffer[i] = (short) ((byteBuffer[i * 2] & 0xFF) | ((byteBuffer[i * 2 + 1] & 0xFF) << 8));
            }
        }

        return shortBuffer;
    }

    private void markVoiceUnvoicedPart(float[] signalFFT) {
        double valueSTE = computeSTEandZCE(signalFFT);
        System.out.println(valueSTE);
    }

    /**
     * Computes SHORT TIME ENERGY of signal
     * @param signalFFT signal after FFT
     * @return STE value
     */
    private double computeSTEandZCE(float[] signalFFT) {
        double shortTimeEnergy = 0;
        int zeroCrossingRate = 0;

        float lastValue = signalFFT[0];

        for (float value : signalFFT) {
            if (Math.signum(value) - Math.signum(lastValue) != 0) {
               zeroCrossingRate++;
            }
            shortTimeEnergy += value * value;
        }
        shortTimeEnergy = 10 * Math.log10(shortTimeEnergy / 10e7);

        System.out.println(shortTimeEnergy + " " + zeroCrossingRate);

        return shortTimeEnergy;
    }
}
