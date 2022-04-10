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

public class SynthTranslator {
    private final String FOLDER_ID;
    private final String API_KEY;
    private boolean isRunning = true;

    public SynthTranslator() {
        this.FOLDER_ID = Config.getFolderId();
        this.API_KEY = Config.getApiKey();
    }

    public String recognize(ByteArrayOutputStream audio_stream) {
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder result_json_string = new StringBuilder();
        JSONObject result_json = new JSONObject();

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
            os.write(audio_stream.toByteArray());

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                br = new BufferedReader(isr = new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = br.readLine()) != null) {
                    result_json_string.append(line);
                }
            } else {
                System.out.println(connection.getResponseCode());
                System.out.println(connection.getResponseMessage());
            }

            JSONParser parser = new JSONParser();
            result_json = (JSONObject) parser.parse(result_json_string.toString());
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

        if (result_json.get("result") == null) {
            stopLoop();
            return "";
        } else {
            return result_json.get("result").toString();
        }
    }

    public String translate(String text) {
        OutputStream os = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuilder result_json_string = new StringBuilder();
        JSONObject translation_text = new JSONObject();

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
                    result_json_string.append(line);
                }
            }

            JSONParser parser = new JSONParser();
            JSONObject translations = (JSONObject) parser.parse(result_json_string.toString());
            JSONArray translations_list = (JSONArray) translations.get("translations");
            translation_text = (JSONObject) parser.parse(translations_list.iterator().next().toString());

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
        if (translation_text.get("text") == null) {
            stopLoop();
            return "";
        } else {
            return translation_text.get("text").toString();
        }
    }

    public InputStream synthesize(String text) {
        OutputStream os = null;
        InputStream is = null;

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
                is = connection.getInputStream();
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

        return is;
    }

    public boolean checkLooping() {
        return this.isRunning;
    }

    private void stopLoop() {
        this.isRunning = false;
    }
}
