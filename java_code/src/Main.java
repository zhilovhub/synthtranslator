import translator.SynthTranslator;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();

        System.out.println(st.recognize("audio"));
        System.out.println(st.translate("text"));
        System.out.println(st.synthesize("text"));
    }
}
