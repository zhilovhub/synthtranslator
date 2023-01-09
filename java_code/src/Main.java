import java.io.InputStream;

import translator.SynthTranslator;
import recorder.VoiceRecorder;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();
        VoiceRecorder vr = new VoiceRecorder();

        String recognized_text;
        String translated_text;
        InputStream synthesized_stream;

        vr.capture_audio();
        vr.play_audio();

        while (true) {
            if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 4) {
                recognized_text = st.recognize(vr.get_voice_stream());
                translated_text = st.translate(recognized_text);
                synthesized_stream = st.synthesize(translated_text);

                vr.update_audio_stream(synthesized_stream);

                System.out.println(recognized_text);
                System.out.println(translated_text);

                while (true) {
                    if (vr.get_available_bytes_of_capturing() >= 16000 * 2 * 3 && vr.get_available_bytes_of_synthesizing() <= 16000 * 2 * 2) {
                        recognized_text = st.recognize(vr.get_voice_stream());
                        translated_text = st.translate(recognized_text);
                        synthesized_stream = st.synthesize(translated_text);

                        System.out.println(recognized_text);
                        System.out.println(translated_text);

                        vr.update_audio_stream(synthesized_stream);
                    }
                }
            }
        }
    }
}
