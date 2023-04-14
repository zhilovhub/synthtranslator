// Rename this file to Config

package com.example.synthtranslator.translator;

/**
 * Класс, содержащий необходимые секретные и приватные ключи для обращений к другим сервисам
 */
final class ConfigExample {
    private final static String FOLDER_ID = ""; // write here
    private final static String API_KEY = ""; // write here

    static String get_folder_id() {
        return FOLDER_ID;
    }

    static String get_api_key() {
        return API_KEY;
    }
}
