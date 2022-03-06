import translator.SynthTranslator;

import java.net.MalformedURLException;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();

        System.out.println(st.recognize("audio"));

        try {
            System.out.println(st.translate("text"));
        } catch (MalformedURLException e) {
            System.out.println("Error: " + e);
        }

        System.out.println(st.synthesize("text"));
    }
}
