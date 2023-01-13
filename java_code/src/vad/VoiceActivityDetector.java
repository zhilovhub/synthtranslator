package vad;

public class VoiceActivityDetector {
    int signalLength = 30;

    public static int[] getSignals(short[] shortBuffer) {
        int signal;
        int[] signals = new int[shortBuffer.length / 480];

        for (int i = 480; i < shortBuffer.length; i += 480) {
            signal = 0;
            for (int j = i - 480; j < i; j++) {
                signal += shortBuffer[j];
            }
            signals[(i - 480) / 480] = signal;
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
