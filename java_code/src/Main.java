import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import translator.SynthTranslator;
import recorder.VoiceRecorder;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();
        VoiceRecorder vr = new VoiceRecorder(st);

        String recognized_text;
        String translated_text;
        InputStream synthesized_stream;

        ByteArrayOutputStream voice_stream = vr.capture_audio();

        recognized_text = st.recognize(voice_stream);
        translated_text = st.translate(recognized_text);
        synthesized_stream = st.synthesize(translated_text);

        vr.play_audio(synthesized_stream);

        System.out.println(recognized_text);
        System.out.println(translated_text);
    }
}
