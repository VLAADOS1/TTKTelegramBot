package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.UserSessionManager;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class loginclient implements ButtonHandler {

    private final TelegramBot bot;
    private final Map<Long, Boolean> awaitingInputMap = new HashMap<>();
    private final ApiClient apiClient = new ApiClient();

    public loginclient(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "loginclient".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.callbackQuery().message().chat().id();

        awaitingInputMap.put(chatId, true);

        String language = null;
        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);
        String getContract = localManager.getMessage("getcontract");

        bot.execute(new SendMessage(chatId, getContract));
    }

    public void handleMessage(Update update) {
        long chatId = update.message().chat().id();
        String contractNumber = update.message().text();

        if (awaitingInputMap.getOrDefault(chatId, false)) {
            boolean contractExists = checkContract(chatId, contractNumber);

            if (contractExists) {
                String language = null;

                ApiClient apiClient = new ApiClient();

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LocalManager localManager = new LocalManager(language);
                String succeslog = localManager.getMessage("succeslog");

                bot.execute(new SendMessage(chatId, succeslog));
            } else {
                String language = null;

                ApiClient apiClient = new ApiClient();

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalManager localManager = new LocalManager(language);
                String logerr = localManager.getMessage("logerr");
                bot.execute(new SendMessage(chatId, logerr));
            }

            awaitingInputMap.put(chatId, false);
        }
    }

    private boolean checkContract(long chatId, String contractNumber) {
        String jsonBody = String.format("{\"contractNumber\": \"%s\"}", contractNumber);

        try {
            HttpResponse<String> response = apiClient.loginTelegram(String.valueOf(chatId), jsonBody);

            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
