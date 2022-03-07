import translator.SynthTranslator;

public class Main {
    public static void main(String[] args) {
        SynthTranslator st = new SynthTranslator();

        System.out.println(st.recognize("audios/output.pcm"));

        System.out.println(st.translate("Здравствуйте не могли бывы подсказать как дойти до метро"));

        System.out.println(st.synthesize("My name is Ilya. I am from Russia and I hove everything will be good"));
    }
}
