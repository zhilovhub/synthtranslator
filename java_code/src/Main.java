import java.io.ByteArrayOutputStream;

import translator.SynthTranslator;
import recorder.VoiceRecorder;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();
        VoiceRecorder vr = new VoiceRecorder(st);

        String recognized_text;
        String translated_text;

        ByteArrayOutputStream voice_stream = vr.capture_audio();

        recognized_text = st.recognize(voice_stream);
        translated_text = st.translate("Распознавание перевод текста");

        System.out.println(recognized_text);
        System.out.println(translated_text);

//        System.out.println(st.synthesize("My name is Ilya. I am from Russia and I hove everything will be good"));
    }
}
