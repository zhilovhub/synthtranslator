package translator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.URL;
import java.net.HttpURLConnection;

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
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder result = new StringBuilder();

        try {
            URL url_translate = new URL("https://translate.api.cloud.yandex.net/translate/v2/translate");
            HttpURLConnection connection = (HttpURLConnection) url_translate.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            os = connection.getOutputStream();
            os.write("{\"texts\":\"Здравствуйте не могли бывы подсказать как дойти до метро\", \"format\":\"PLAIN_TEXT\", \"sourceLanguageCode\":\"ru\", \"targetLanguageCode\":\"en\"}".getBytes());

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                br = new BufferedReader(isr = new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                    System.out.println("Error: " + ignored);
                }
            }
            if (isr != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                    System.out.println("Error: " + ignored);
                }
            }
            if (br != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                    System.out.println("Error: " + ignored);
                }
            }
        }

        return result.toString();
    }

    public String synthesize(String text) {
        return "Synthesizing finished!";
    }
}
