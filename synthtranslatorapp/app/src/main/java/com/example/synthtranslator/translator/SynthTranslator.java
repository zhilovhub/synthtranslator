package com.example.synthtranslator.translator;

import java.util.Map;
import java.util.HashMap;
import java.util.StringJoiner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.HttpURLConnection;

/**
 * Класс, реализующий обращения к API сервисам с целью выполнения трёх функции:<br/>
 * 1. Распознавание речи<br/>
 * 2. Перевод текста<br/>
 * 3. Синтез речи
 */
public class SynthTranslator {
    private final String FOLDER_ID;
    private final String API_KEY;

    /**
     * Constructor
     */
    public SynthTranslator() {
        this.FOLDER_ID = Config.getFolderId();
        this.API_KEY = Config.getApiKey();
    }

    /**
     * Speech to text (STT)
     * @param audioStream ByteArrayOutputStream of recorded speech
     * @return russian recognized text of recorded speech
     */
    public String recognize(ByteArrayOutputStream audioStream) {
        Map<String, String> map = new HashMap<>();
        map.put("topic", "general");
        map.put("lang", "ru-RU");
        map.put("format", "lpcm");
        map.put("sampleRateHertz", "16000");

        StringJoiner params = new StringJoiner("&");
        for (Map.Entry entry : map.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }

        JSONObject resultJson = new JSONObject();

        try {
            URL urlRecognize = new URL("https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?" + params);
            HttpURLConnection connection = (HttpURLConnection) urlRecognize.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(audioStream.toByteArray());
                StringBuilder resultJsonString = new StringBuilder();

                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        while ((line = br.readLine()) != null) {
                            resultJsonString.append(line);
                        }
                    }
                } else {
                    System.out.println(connection.getResponseCode());
                    System.out.println(connection.getResponseMessage());
                }

                JSONParser parser = new JSONParser();
                resultJson = (JSONObject) parser.parse(resultJsonString.toString());
            }
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
            }

        if (resultJson.get("result") == null) {
            return "";
        } else {
            return resultJson.get("result").toString();
        }
    }

    /**
     * Text in another language (russian into english)
     * @param text russian text
     * @return english translation text of russian text
     */
    public String translate(String text) {
        JSONObject json_data = new JSONObject();
        json_data.put("sourceLanguageCode", "ru");
        json_data.put("targetLanguageCode", "en");
        json_data.put("format", "PLAIN_TEXT");
        json_data.put("texts", text);

        JSONObject translation_text = new JSONObject();

        try {
            URL url_translate = new URL("https://translate.api.cloud.yandex.net/translate/v2/translate");
            HttpURLConnection connection = (HttpURLConnection) url_translate.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(json_data.toJSONString().getBytes());
                StringBuilder result_json_string = new StringBuilder();

                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        while ((line = br.readLine()) != null) {
                            result_json_string.append(line);
                        }
                    }
                }

                JSONParser parser = new JSONParser();
                JSONObject translations = (JSONObject) parser.parse(result_json_string.toString());
                JSONArray translations_list = (JSONArray) translations.get("translations");
                translation_text = (JSONObject) parser.parse(translations_list.iterator().next().toString());
            }
            } catch (Exception e) {
                System.out.println("ERROR: " + e);
        }

        if (translation_text.get("text") == null) {
            return "";
        } else {
            return translation_text.get("text").toString();
        }
    }

    /**
     * Text to speech (TTS)
     * @param text english text which should be synthesized
     * @return InputStream with synthesized text
     */
    public InputStream synthesize(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("lang", "en-US");
        map.put("voice", "nick");
        map.put("speed", "1.2");
        map.put("format", "lpcm");
        map.put("sampleRateHertz", "48000");

        StringJoiner data = new StringJoiner("&");

        for (Map.Entry entry : map.entrySet()) {
            data.add(entry.getKey() + "=" + entry.getValue());
        }

        InputStream is = null;

        try {
            URL url_synthesize = new URL("https://tts.api.cloud.yandex.net/speech/v1/tts:synthesize");
            HttpURLConnection connection = (HttpURLConnection) url_synthesize.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Api-Key " + this.API_KEY);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(data.toString().getBytes());

                if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                    is = connection.getInputStream();
                } else {
                    System.out.println(connection.getResponseCode());
                    System.out.println(connection.getResponseMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        return is;
    }
}
