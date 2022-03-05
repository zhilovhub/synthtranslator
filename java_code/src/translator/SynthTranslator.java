package translator;

public class SynthTranslator {
    private final String FOLDER_ID;
    private final String API_KEY;

    public SynthTranslator() {
        this.FOLDER_ID = Config.get_folder_id();
        this.API_KEY = Config.get_api_key();
    }

    public String recognize(String audio) {
        return "Recognizing finished!";
    }

    public String translate(String text) {
        return "Translating finished!";
    }

    public String synthesize(String text) {
        return "Synthesizing finished!";
    }
}
