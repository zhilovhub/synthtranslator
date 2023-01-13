package vad;

import recorder.VoiceRecorder;

public class TestVAD {
    public static void main(String[] args) {
        VoiceRecorder vr = new VoiceRecorder();

        vr.capture_audio();

        while (true) {
            if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 4) {
                vr.stop_capturing();
                break;
            }
        }
        System.out.println("ENDED!");
    }
}
