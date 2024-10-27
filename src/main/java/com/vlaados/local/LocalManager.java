package com.vlaados.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class LocalManager {

    private Map<String, String> messages;

    public LocalManager(String language) {
        loadMessages(language);
    }

    private void loadMessages(String language) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String filename = language + ".yml";
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                filename = "ru.yml";
                System.out.println("Файл для языка " + language + " не найден. Используется язык по умолчанию: ru.");
                input = getClass().getClassLoader().getResourceAsStream(filename);

                if (input == null) {
                    throw new IllegalArgumentException("Не удалось загрузить файл локализации по умолчанию: " + filename);
                }
            }
            messages = objectMapper.readValue(input, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            messages = Map.of();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key,
                "?? Ошибка в локализации.\n" +
                        "Пожалуйста, обратитесь в техподдержку.\n\n" +
                        "?? Localization error.\n" +
                        "Please contact support."
        );
    }
}