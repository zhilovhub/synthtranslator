import translator.SynthTranslator;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();

        System.out.println(st.recognize("audio"));

        System.out.println(st.translate("Здравствуйте не могли бывы подсказать как дойти до метро"));

        System.out.println(st.synthesize("text"));
    }
}
