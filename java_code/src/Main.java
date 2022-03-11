import java.io.ByteArrayOutputStream;

import translator.SynthTranslator;
import recorder.VoiceRecorder;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();
        VoiceRecorder vr = new VoiceRecorder(st);

        ByteArrayOutputStream voice_stream = vr.capture_audio();

        System.out.println(st.recognize(voice_stream));

//        System.out.println(st.translate("Здравствуйте не могли бывы подсказать как дойти до метро"));

//        System.out.println(st.synthesize("My name is Ilya. I am from Russia and I hove everything will be good"));
    }
}
