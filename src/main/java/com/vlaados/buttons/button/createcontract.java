package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.pengrad.telegrambot.request.SendMessage;

public class createcontract implements ButtonHandler {
    private final TelegramBot bot;
    private final ApiClient apiClient = new ApiClient();
    private final Map<Long, String> awaitingInputMap = new HashMap<>();
    private final Map<Long, String> userPhoneMap = new HashMap<>();

    public createcontract(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "createcontract".equalsIgnoreCase(buttonData);
    }

    @Override
    public void handle(Update update) {
        long chatId = update.callbackQuery().message().chat().id();

        awaitingInputMap.put(chatId, "awaitingPhone");
        String language = null;

        ApiClient apiClient = new ApiClient();

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalManager localManager = new LocalManager(language);
        String getPhoneMessage = localManager.getMessage("getphone");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(
                new KeyboardButton(localManager.getMessage("sharephone")).requestContact(true)
        ).oneTimeKeyboard(true).resizeKeyboard(true);

        bot.execute(new SendMessage(chatId, getPhoneMessage).replyMarkup(keyboard));
    }

    public void handleMessage(Update update) {
        long chatId = update.message().chat().id();
        String currentState = awaitingInputMap.getOrDefault(chatId, "");
        String language = null;

        ApiClient apiClient = new ApiClient();

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalManager localManager = new LocalManager(language);

        if ("awaitingPhone".equals(currentState)) {
            handlePhoneNumber(update, chatId, localManager);
        } else if ("awaitingAddress".equals(currentState)) {
            handleAddress(update, chatId, localManager);
        }
    }

    private void handlePhoneNumber(Update update, long chatId, LocalManager localManager) {
        if (update.message().contact() != null) {
            Contact contact = update.message().contact();
            String phoneNumber = contact.phoneNumber();
            userPhoneMap.put(chatId, phoneNumber);

            bot.execute(new SendMessage(chatId, localManager.getMessage("getadres")).replyMarkup(new ReplyKeyboardRemove()));
            awaitingInputMap.put(chatId, "awaitingAddress");

        } else if (update.message().text() != null && isValidPhoneNumber(update.message().text())) {
            String phoneNumber = update.message().text();
            userPhoneMap.put(chatId, phoneNumber);

            bot.execute(new SendMessage(chatId, localManager.getMessage("getadres")).replyMarkup(new ReplyKeyboardRemove()));
            awaitingInputMap.put(chatId, "awaitingAddress");

        } else {
            bot.execute(new SendMessage(chatId, localManager.getMessage("curnumber")));
        }
    }

    private void handleAddress(Update update, long chatId, LocalManager localManager) {
        String address = update.message().text();
        String phoneNumber = userPhoneMap.get(chatId);

        if (phoneNumber != null) {
            String contractNumber = generateContractNumber();

            sendTelegramId(String.valueOf(chatId));

            registerUser(chatId, contractNumber, phoneNumber, address);

            bot.execute(new SendMessage(chatId, localManager.getMessage("succesreg")));
            awaitingInputMap.remove(chatId);
            userPhoneMap.remove(chatId);
        } else {
            bot.execute(new SendMessage(chatId, "Ошибка: не удалось найти телефонный номер."));
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

    private void registerUser(long chatId, String contractNumber, String phoneNumber, String address) {
        String jsonBody = String.format("{\"contractNumber\": \"%s\", \"phoneNumber\": \"%s\", \"address\": \"%s\", \"telegramId\": \"%s\"}",
                contractNumber, phoneNumber, address, chatId);

        System.out.println(jsonBody);

        try {
            HttpResponse<String> response = apiClient.registerTelegram(String.valueOf(chatId), jsonBody);

            if (response.statusCode() == 200) {
                System.out.println("User registered successfully: " + response.body());
            } else {
                System.err.println("Failed to register user: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+7") && phoneNumber.length() == 12 && phoneNumber.substring(2).matches("\\d+");
    }

    public String generateContractNumber() {
        Random random = new Random();
        int randomSixDigits = 100000 + random.nextInt(900000);
        String contractNumber = "516" + randomSixDigits;
        System.out.println(contractNumber);
        return contractNumber;
    }

}