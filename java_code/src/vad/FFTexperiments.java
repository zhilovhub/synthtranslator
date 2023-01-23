package vad;

import be.tarsos.dsp.util.fft.FFT;
import java.util.Arrays;

public class FFTexperiments {
    public static void main(String[] args) {
        float[] array = new float[] {0, 1, 3, 1, 2};
        float[] amplitudes = new float[array.length / 2];
        FFT fft = new FFT(array.length);

        fft.forwardTransform(array);
        System.out.println(Arrays.toString(array));
        fft.modulus(array, amplitudes);
        System.out.println(Arrays.toString(amplitudes));
        fft.backwardsTransform(array);
        System.out.println(Arrays.toString(array));
    }
}
