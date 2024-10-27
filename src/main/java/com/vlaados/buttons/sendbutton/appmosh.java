package com.vlaados.buttons.sendbutton;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;

public class appmosh implements ButtonHandler {

    private final TelegramBot bot;
    private final ApiClient apiClient = new ApiClient();

    public appmosh(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "appmosh".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        String chatId = String.valueOf(update.callbackQuery().message().chat().id());

        String language = null;

        try {
            language = apiClient.extractLocal(chatId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);
        String request = localManager.getMessage("request");

        // ���������� ������ �� /api/request � telegramId � type=2
        boolean requestSuccess = sendRequest(chatId);

        if (requestSuccess) {
            bot.execute(new SendMessage(chatId, request));
        } else {
            bot.execute(new SendMessage(chatId, "������ ��� �������� �������. ����������, ���������� �����."));
        }
    }

    private boolean sendRequest(String chatId) {
        String jsonBody = String.format("{\"telegramId\": \"%s\", \"type\": 2}", chatId);

        try {
            HttpResponse<String> response = apiClient.createRequest(jsonBody);
            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}