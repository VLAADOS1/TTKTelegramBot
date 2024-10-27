package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

public class maintp implements ButtonHandler {

    private final TelegramBot bot;


    public maintp(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "maintp".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        String chatId = String.valueOf(update.callbackQuery().message().chat().id());

        String language = null;

        ApiClient apiClient = new ApiClient();

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);

        String support = localManager.getMessage("support");

        bot.execute(new SendMessage(chatId, support).parseMode(ParseMode.valueOf("HTML")));
    }
}