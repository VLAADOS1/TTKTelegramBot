package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class changePhone implements ButtonHandler {

    private final TelegramBot bot;
    private final Map<Long, Boolean> awaitingPhoneInput = new HashMap<>();
    private final ApiClient apiClient = new ApiClient();

    public changePhone(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "changePhone".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.callbackQuery().message().chat().id();

        awaitingPhoneInput.put(chatId, true);

        String language = null;

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);
        String changePhoneText = localManager.getMessage("changePhoneText");

        bot.execute(new SendMessage(chatId, changePhoneText));
    }

    public void handleMessage(Update update) {
        long chatId = update.message().chat().id();
        String messageText = update.message().text();

        if (awaitingPhoneInput.getOrDefault(chatId, false)) {
            if (isValidPhoneNumber(messageText)) {
                // Отправляем обновленный номер телефона на сервер
                boolean updateSuccess = updatePhoneNumber(chatId, messageText);

                String language = null;

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LocalManager localManager = new LocalManager(language);

                if (updateSuccess) {
                    String changePhoneSuccess = localManager.getMessage("changePhoneSuccess");
                    bot.execute(new SendMessage(chatId, changePhoneSuccess));
                } else {
                    String errorMessage = localManager.getMessage("phoneUpdateError");
                    bot.execute(new SendMessage(chatId, errorMessage));
                }

                awaitingPhoneInput.put(chatId, false);
            } else {
                String language = null;

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalManager localManager = new LocalManager(language);
                String invalidPhoneMessage = localManager.getMessage("curnumber");
                bot.execute(new SendMessage(chatId, invalidPhoneMessage));
            }
        }
    }

    private boolean updatePhoneNumber(long chatId, String phoneNumber) {
        String jsonBody = String.format("{\"phone\": \"%s\"}", phoneNumber);

        try {
            HttpResponse<String> response = apiClient.updatePhoneNumber(String.valueOf(chatId), jsonBody);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+7") && phoneNumber.length() == 12 && phoneNumber.substring(2).matches("\\d+");
    }
}