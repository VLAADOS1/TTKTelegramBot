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

public class changeAdress implements ButtonHandler {

    private final TelegramBot bot;
    private final Map<Long, Boolean> awaitingAddressInput = new HashMap<>();
    private final ApiClient apiClient = new ApiClient();

    public changeAdress(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "changeAdress".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.callbackQuery().message().chat().id();

        awaitingAddressInput.put(chatId, true);

        String language = null;

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);
        String changeAddressText = localManager.getMessage("changeAdressText");

        bot.execute(new SendMessage(chatId, changeAddressText));
    }

    public void handleMessage(Update update) {
        long chatId = update.message().chat().id();
        String messageText = update.message().text();

        if (awaitingAddressInput.getOrDefault(chatId, false)) {
            boolean updateSuccess = updateAddress(chatId, messageText);

            String language = null;

            try {
                language = apiClient.extractLocal(String.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }

            LocalManager localManager = new LocalManager(language);

            if (updateSuccess) {
                String changeAddressSuccess = localManager.getMessage("changeAdressSuccess");
                bot.execute(new SendMessage(chatId, changeAddressSuccess));
            } else {
                String errorMessage = localManager.getMessage("addressUpdateError");
                bot.execute(new SendMessage(chatId, errorMessage));
            }

            awaitingAddressInput.put(chatId, false);
        }
    }

    private boolean updateAddress(long chatId, String address) {
        String jsonBody = String.format("{\"address\": \"%s\"}", address);

        try {
            HttpResponse<String> response = apiClient.updateAddress(String.valueOf(chatId), jsonBody);
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
