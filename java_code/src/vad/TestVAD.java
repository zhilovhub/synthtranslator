package vad;

import recorder.VoiceRecorder;

public class TestVAD {
    public static void main(String[] args) {
        VoiceRecorder vr = new VoiceRecorder();
        byte[] audioBytes;
        short[] audioShorts;

        vr.capture_audio();

        while (true) {
            if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 4) {
                vr.stop_capturing();
                System.out.println("[INFO] Capturing ENDED!");
                break;
            }
        }

        audioBytes = vr.get_voice_stream().toByteArray();
        audioShorts = VoiceActivityDetector.getShort(audioBytes);
        System.out.println(audioBytes.length);
        System.out.println(audioShorts.length);
    }
}
