package vad;

import recorder.VoiceRecorder;

public class TestVAD {
    public static void main(String[] args) {
        VoiceRecorder vr = new VoiceRecorder();
        byte[] audioBytes;
        short[] audioShorts;
        float[][] audioSignals;

        vr.capture_audio();
        System.out.println("[INFO] Capturing STARTED!");

        while (true) {
            if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 6) {
                vr.stop_capturing();
                System.out.println("[INFO] Capturing ENDED!");
                break;
            }
        }

        audioBytes = vr.get_voice_stream().toByteArray();
        audioShorts = VoiceActivityDetector.getShort(audioBytes, false);

        System.out.println(audioShorts.length);

        audioSignals = VoiceActivityDetector.getSignals(audioShorts, 16000, 30);
        System.out.println(audioSignals.length);
        VoiceActivityDetector.signalsFFT(audioSignals);

        for (float[] frames : audioSignals) {
            double shortTimeEnergy = 0;
            for (float frame : frames) {
                shortTimeEnergy += frame * frame;
            }
            shortTimeEnergy = 10 * Math.log10(shortTimeEnergy / 10e7);
            System.out.println(shortTimeEnergy);
        }
    }
}
