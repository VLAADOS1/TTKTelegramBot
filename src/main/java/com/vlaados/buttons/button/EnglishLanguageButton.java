package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;

import java.net.http.HttpResponse;

public class EnglishLanguageButton implements ButtonHandler {
    private final TelegramBot bot;
    private final ApiClient apiClient = new ApiClient();

    public EnglishLanguageButton(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "LANGUAGE_ENGLISH".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        String chatId = String.valueOf(update.callbackQuery().message().chat().id());

        // Отправляем telegramId, если еще не зарегистрирован
        sendTelegramId(chatId);

        // Устанавливаем язык пользователя на английский
        if (setUserLanguage(chatId, "en")) {
            bot.execute(new SendMessage(chatId, "\uD83C\uDDFA\uD83C\uDDF2 You selected English! Welcome to the bot! \uD83C\uDF89"));
        } else {
            bot.execute(new SendMessage(chatId, "Error setting language. Please try again later."));
        }
    }

    private boolean sendTelegramId(String telegramId) {
        String jsonBody = String.format("{\"telegramId\": \"%s\"}", telegramId);

        try {
            HttpResponse<String> response = apiClient.postTelegram(jsonBody);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean setUserLanguage(String telegramId, String language) {
        String jsonBody = String.format("{\"local\": \"%s\"}", language);

        try {
            HttpResponse<String> response = apiClient.putTelegramLocal(telegramId, jsonBody);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}