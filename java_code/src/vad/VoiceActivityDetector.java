package vad;

public class VoiceActivityDetector {
    /**
     *
     * @param shortBuffer array of audio short values
     * @param sampleRate frames per second
     * @param signalLength length of the window in ms
     * @return two-dimensional array of frames' windows
     */
    public static float[][] getSignals(short[] shortBuffer, int sampleRate, int signalLength) {
        int framesPerSignal = (int) ((signalLength / 1000f) * sampleRate);
        float[][] signals = new float[(shortBuffer.length + framesPerSignal - 1) / framesPerSignal][framesPerSignal];

        float[] signal;
        for (int i = framesPerSignal; i < shortBuffer.length; i += framesPerSignal) {
            signal = new float[framesPerSignal];
            for (int j = i - framesPerSignal; j < i; j++) {
                signal[j % framesPerSignal] = shortBuffer[j];
            }
            signals[(i - framesPerSignal) / framesPerSignal] = signal;
        }

        int restFrames = shortBuffer.length - (shortBuffer.length / framesPerSignal) * framesPerSignal;
        signal = new float[framesPerSignal];
        for (int i = restFrames; i > 0; i--) {
            signal[restFrames - i] = shortBuffer[shortBuffer.length - i];
        }
        signals[signals.length - 1] = signal;

        return signals;
    }

    /**
     *
     * @param byteBuffer array of audio byte values
     * @param bigEndian indicates whether the data for a single sample is stored in big-endian byte order
     *                  (false means little-endian)
     * @return shortBuffer instead of byteBuffer
     */
    public static short[] getShort(byte[] byteBuffer, boolean bigEndian) {
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
}
