package translator;

import java.util.Map;
import java.util.HashMap;

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

        try {
//            Map<String, String> data = new HashMap<>();
//            data.put("sourceLanguageCode", "ru");
//            data.put("targetLanguageCode", "en");
//            data.put("format", "PLAIN_TEXT");
//            data.put("texts", "Здравствуйте не могли бывы подсказать как дойти до метро");

//            System.out.println(data.toString());

//            byte[] data_bytes =  data.toString().getBytes();

            URL translate_url = new URL("https://translate.api.cloud.yandex.net/translate/v2/translate");
            HttpURLConnection con = (HttpURLConnection) translate_url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.connect();

            StringBuilder result = new StringBuilder();

            try {
                os = con.getOutputStream();
                os.write("{\"texts\":\"Здравствуйте не могли бывы подсказать как дойти до метро\", \"format\":\"PLAIN_TEXT\", \"sourceLanguageCode\":\"ru\", \"targetLanguageCode\":\"en\"}".getBytes());
            } catch(Exception e) {
                return "Error: " + e;
            }

            if (HttpURLConnection.HTTP_OK == con.getResponseCode()) {
                isr = new InputStreamReader(con.getInputStream());
                br = new BufferedReader(isr);

                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            }
            else {
//                System.out.println(con.getResponseMessage());
//                System.out.println(con.getResponseCode());
//                System.out.println(con.getResponseMessage());
                return "oh no";
            }

        } catch (IOException e) {
            return "Error: " + e;
        } finally {
            try {
                isr.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            try {
                br.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            try {
                os.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public String synthesize(String text) {
        return "Synthesizing finished!";
    }
}
