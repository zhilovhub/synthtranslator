package recorder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import be.tarsos.dsp.util.fft.FFT;

public class AudioAnalyzer {
    private final int FFTWindowDurationMS;
    private final int SizeFFT = 200;
    private final int lowerFrequencyBound = 100;
    private final int upperFrequencyBound = 3200;

    private final int sampleRate;
    private final int audioFormatBytes;
    private final int channelCount;

    private final FFT fft = new FFT(SizeFFT);
    private volatile ArrayList<float[]> signalsFFT = new ArrayList<>();

    private double energyMax;
    private double energyMin;
    private double delta = 1;
    private double growthFactor = 1.0001;
    private final int hangoverCount = 2;
    private int currentVoiceHangoverCount = 0;

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

        markVoiceUnvoicedPart(floatBuffer);
        signalsFFT.add(floatBuffer);
    }

    private void transferSignalToFFT(float[] floatBuffer) {
        fft.forwardTransform(floatBuffer);
    }

    private void cutFrequency(float[] floatBuffer, int lowerBound, int upperBound) {
        int n = floatBuffer.length;
        int minIndex = (int) (((float) lowerBound / sampleRate) * (n / 2));
        int maxIndex = (int) (((float) upperBound / sampleRate) * (n / 2));

        for (int k = minIndex; k <= maxIndex; k++) {
            floatBuffer[2 * k] = 0;
            floatBuffer[2 * k + 1] = 0;
        }
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

//            transferFFTToSignal(signalFFT);
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
                shortBuffer[i] = (short) (byteBuffer[i * 2] << 8 | (byteBuffer[i * 2 + 1] & 0xFF));
            }
        } else {
            for (int i = 0; i < byteBuffer.length / 2; i++) {
                shortBuffer[i] = (short) ((byteBuffer[i * 2] & 0xFF) | ((byteBuffer[i * 2 + 1]) << 8));
            }
        }

        return shortBuffer;
    }

    private void markVoiceUnvoicedPart(float[] signalFFT) {
        double shortTimeEnergy = computeSTE(signalFFT);

        if (signalsFFT.size() == 3) {
            energyMax = shortTimeEnergy * 2;
            energyMin = 12; // устанавливается вручную
        } else if (signalsFFT.size() > 3) {
            if (shortTimeEnergy > energyMax) {
                energyMax = shortTimeEnergy;
            } if (shortTimeEnergy < energyMin) {
                energyMin = shortTimeEnergy;
            }

            double lambda = (energyMax - energyMin) / energyMax;
            double threshold = (1 - lambda) * energyMax + lambda * energyMin;

            delta *= growthFactor;
            energyMin *= delta;

            if (shortTimeEnergy > threshold) {
                currentVoiceHangoverCount = 0;
                System.out.println("Речь обнаружена");
            } else {
                if (currentVoiceHangoverCount < hangoverCount) {
                    System.out.println("Речь обнаружена");
                    currentVoiceHangoverCount += 1;
                } else {
                    System.out.println("Речь не обнаружена");
                }
            }
        }
    }

    /**
     * Computes SHORT TIME ENERGY of signal
     * @param signalFFT signal after FFT
     */
    private double computeSTE(float[] signalFFT) {
        double shortTimeEnergy = 0;

        for (float value : signalFFT) {
            shortTimeEnergy += value * value;
        }
        shortTimeEnergy = Math.sqrt(shortTimeEnergy / 480);
//        shortTimeEnergy = 20 * Math.log10(shortTimeEnergy / 10e7);

        return shortTimeEnergy;
    }

    private int maxFromFloatBuffer(float[] buffer) {
        float maxValue = 0;

        for (int i = 0; i < buffer.length; i++) {
            float curSample = buffer[i];
            if (curSample > maxValue) {
                maxValue = curSample;
            }
        }

        return (int) maxValue;
    }
}
