package vad;

import recorder.VoiceRecorder;

public class TestVAD {
    public static void main(String[] args) {
        VoiceRecorder vr = new VoiceRecorder();
        byte[] audioBytes;
        short[] audioShorts;
        int[][] audioSignals;

        vr.capture_audio();

        while (true) {
            if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 1) {
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
        for (int[] frames : audioSignals) {
            for (int frame : frames) {
                System.out.print(frame + " ");
            }
            System.out.println();
        }
    }
}
