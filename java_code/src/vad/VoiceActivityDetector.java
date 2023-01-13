package vad;

public class VoiceActivityDetector {
    int signalLength = 30;

    public static short[] getSignals(short[] temp_buffer) {
        return temp_buffer;
    }

    public static short[] getShort(byte[] byteBuffer) {
        short[] shortBuffer = new short[byteBuffer.length / 2];

        for (int i = 0; i < byteBuffer.length / 2; i++) {
            shortBuffer[i] = (short) (byteBuffer[i * 2] | (byteBuffer[i * 2 + 1] << 8));
        }

        return shortBuffer;
    }
}
