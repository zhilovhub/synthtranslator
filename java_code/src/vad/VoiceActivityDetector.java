package vad;

public class VoiceActivityDetector {
    /**
     *
     * @param shortBuffer array of audio short values
     * @param sampleRate frames per second
     * @param signalLength length of the window in ms
     * @return two-dimensional array of frames' windows
     */
    public static int[][] getSignals(short[] shortBuffer, int sampleRate, int signalLength) {
        int framesPerSignal = (int) ((signalLength / 1000f) * sampleRate);
        int[][] signals = new int[(shortBuffer.length + framesPerSignal - 1) / framesPerSignal][framesPerSignal];

        int[] signal;
        for (int i = framesPerSignal; i < shortBuffer.length; i += framesPerSignal) {
            signal = new int[framesPerSignal];
            for (int j = i - framesPerSignal; j < i; j++) {
                signal[j % framesPerSignal] = shortBuffer[j];
                System.out.println(j);
            }
            signals[(i - framesPerSignal) / framesPerSignal] = signal;
        }

        int restFrames = shortBuffer.length - (shortBuffer.length / framesPerSignal) * framesPerSignal;
        signal = new int[framesPerSignal];
        for (int i = restFrames; i > 0; i--) {
            System.out.println(shortBuffer.length - i);
            signal[restFrames - i] = shortBuffer[shortBuffer.length - i];
        }
        signals[signals.length - 1] = signal;

        return signals;
    }

    public static short[] getShort(byte[] byteBuffer) {
        short[] shortBuffer = new short[byteBuffer.length / 2];

        for (int i = 0; i < byteBuffer.length / 2; i++) {
            shortBuffer[i] = (short) (byteBuffer[i * 2] | (byteBuffer[i * 2 + 1] << 8));
        }

        return shortBuffer;
    }
}
