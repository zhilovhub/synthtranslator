import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        byte[] buffer = new byte[] {-1, 127};
        System.out.println(Arrays.toString(getShort(buffer, false)));
    }

    private static short[] getShort(byte[] byteBuffer, boolean bigEndian) {
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
}
