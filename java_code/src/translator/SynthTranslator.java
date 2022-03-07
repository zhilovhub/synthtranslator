package translator;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;

import java.util.Map;
import java.util.HashMap;
import java.util.StringJoiner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.json.simple.JSONObject;

import java.net.URL;
import java.net.HttpURLConnection;

public class SynthTranslator {
    private final String FOLDER_ID;
    private final String API_KEY;

    public SynthTranslator() {
        this.FOLDER_ID = Config.get_folder_id();
        this.API_KEY = Config.get_api_key();
    }

    public String recognize(String audio_path) {
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder result = new StringBuilder();

        StringJoiner params = new StringJoiner("&");

        Map<String, String> map = new HashMap<>();
        map.put("topic", "general");
        map.put("lang", "ru-RU");
        map.put("format", "lpcm");
        map.put("sampleRateHertz", "16000");

        for (Map.Entry entry : map.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }

        try {
            URL url_recognize = new URL("https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?" + params);
            HttpURLConnection connection = (HttpURLConnection) url_recognize.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            os = connection.getOutputStream();
            os.write(Files.readAllBytes(Path.of(audio_path)));

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                br = new BufferedReader(isr = new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            } else {
                System.out.println(connection.getResponseCode());
                System.out.println(connection.getResponseMessage());
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
                    isr.close();
                } catch (IOException ignored) {
                    System.out.println("Error: " + ignored);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                    System.out.println("Error: " + ignored);
                }
            }
        }

        return result.toString();
    }

    public String translate(String text) {
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder result = new StringBuilder();

        JSONObject json_data = new JSONObject();
        json_data.put("sourceLanguageCode", "ru");
        json_data.put("targetLanguageCode", "en");
        json_data.put("format", "PLAIN_TEXT");
        json_data.put("texts", text);

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
            os.write(json_data.toJSONString().getBytes());

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
        OutputStream os = null;

        StringJoiner data = new StringJoiner("&");

        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("lang", "en-US");
        map.put("voice", "nick");
        map.put("speed", "1.2");
        map.put("format", "lpcm");
        map.put("sampleRateHertz", "48000");

        for (Map.Entry entry : map.entrySet()) {
            data.add(entry.getKey() + "=" + entry.getValue());
        }

        try {
            URL url_synthesize = new URL("https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize");
            HttpURLConnection connection = (HttpURLConnection) url_synthesize.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            os = connection.getOutputStream();
            os.write(data.toString().getBytes());

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                Files.copy(connection.getInputStream(), Path.of("audios/translated.pcm"), StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.out.println(connection.getResponseCode());
                System.out.println(connection.getResponseMessage());
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
        }

        return "Synthesizing finished!";
    }
}
