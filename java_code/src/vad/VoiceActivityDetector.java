package vad;

public class VoiceActivityDetector {
    public static int[][] getSignals(short[] shortBuffer, int sampleRate, int signalLength) {
        int framesPerSignal = (int) ((signalLength / 1000f) * sampleRate);
        int[][] signals = new int[shortBuffer.length / framesPerSignal][framesPerSignal];

        int[] signal;
        for (int i = framesPerSignal; i < shortBuffer.length; i += framesPerSignal) {
            signal = new int[framesPerSignal];
            for (int j = i - framesPerSignal; j < i; j++) {
                signal[j % framesPerSignal] = shortBuffer[j];
            }
            signals[(i - framesPerSignal) / framesPerSignal] = signal;
        }

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
