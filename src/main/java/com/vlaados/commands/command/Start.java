package com.vlaados.commands.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.commands.CommandHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;
import java.util.Objects;

public class Start implements CommandHandler {

    private final TelegramBot bot;

    ApiClient apiClient = new ApiClient();

    public Start(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String command) {
        return "/start".equalsIgnoreCase(command);
    }

    @Override
    public void handle(Update update) {
        String chatId = String.valueOf(update.message().chat().id());

        String language = null;

        ApiClient apiClient = new ApiClient();

        System.out.println();

        try {
            language = apiClient.extractLocal(chatId);
        } catch (Exception e) {
            sendTelegramId(chatId);
            try {
                language = apiClient.extractLocal(chatId);
            } catch (Exception es) {
            }
            e.printStackTrace();
        }

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFA Русский").callbackData("LANGUAGE_RUSSIAN"),
                        new InlineKeyboardButton("\uD83C\uDDEC\uD83C\uDDE7 English").callbackData("LANGUAGE_ENGLISH")
                }
        );

        SendMessage message = new SendMessage(chatId, "\uD83D\uDC4B Добро пожаловать в бота! / Welcome to the bot!\n" +
                "\n" +
                "Пожалуйста, выберите язык, который вам удобен.\n" +
                "Please, select your preferred language.\n" +
                "\n" +
                "\uD83C\uDDF7\uD83C\uDDFA Русский – Нажмите, чтобы продолжить на русском языке.\n" +
                "\uD83C\uDDEC\uD83C\uDDE7 English – Click here to continue in English.")
                .replyMarkup(keyboard);

        if (language == null) {
            bot.execute(message);
        } else {

            HttpResponse<String> response = null;

            try {
                response = apiClient.getTelegramById(chatId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null && response.statusCode() != 404) {
                String responseBody = response.body();

                if (responseBody.contains("\"contractNumber\":") && !responseBody.contains("\"contractNumber\":null")) {
                    LocalManager localManager = new LocalManager(language);

                    String maininfo = localManager.getMessage("maininfo");
                    String mainapp = localManager.getMessage("mainapp");
                    String mainprofil = localManager.getMessage("mainprofil");
                    String maintp = localManager.getMessage("maintp");

                    InlineKeyboardMarkup keyboards = new InlineKeyboardMarkup(
                            new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(maininfo).callbackData("maininfo"),
                                    new InlineKeyboardButton(mainapp).callbackData("mainapp"),
                                    new InlineKeyboardButton(mainprofil).callbackData("mainprofil"),
                                    new InlineKeyboardButton(maintp).callbackData("maintp")
                            }
                    );

                    String test = localManager.getMessage("mainmess");

                    SendMessage messages = new SendMessage(chatId, test).replyMarkup(keyboards);
                    bot.execute(messages);

                } else {
                    LocalManager localManager = new LocalManager(language);

                    String login = localManager.getMessage("loginclient");
                    String create = localManager.getMessage("createcontract");

                    InlineKeyboardMarkup keyboards = new InlineKeyboardMarkup(
                            new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(login).callbackData("loginclient"),
                                    new InlineKeyboardButton(create).callbackData("createcontract")
                            }
                    );

                    String test = localManager.getMessage("authorization");

                    SendMessage messages = new SendMessage(chatId, test).replyMarkup(keyboards);
                    bot.execute(messages);
                }
            }
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

}