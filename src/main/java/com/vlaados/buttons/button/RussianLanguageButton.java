package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;

import java.net.http.HttpResponse;

public class RussianLanguageButton implements ButtonHandler {
    private final TelegramBot bot;
    private final ApiClient apiClient = new ApiClient();

    public RussianLanguageButton(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "LANGUAGE_RUSSIAN".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        String chatId = String.valueOf(update.callbackQuery().message().chat().id());

        sendTelegramId(chatId);

        setUserLanguage(chatId, "ru");
            bot.execute(new SendMessage(chatId, "\uD83C\uDDF7\uD83C\uDDFA Вы выбрали русский язык! Напишите /start ещё раз! \uD83C\uDF89"));

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