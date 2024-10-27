package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.local.LocalManager;

import java.net.http.HttpResponse;


public class mainprofil implements ButtonHandler {

    private final TelegramBot bot;
    private final ApiClient apiClient = new ApiClient();

    public mainprofil(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "mainprofil".equalsIgnoreCase(buttonData);
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

        String contractInfo = fetchContractInfo(chatId);

        String changePhone = localManager.getMessage("changePhone");
        String changeAddress = localManager.getMessage("changeAdress");

        InlineKeyboardMarkup keyboards = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton(changePhone).callbackData("changePhone"),
                        new InlineKeyboardButton(changeAddress).callbackData("changeAdress")
                }
        );

        String profileMessage = localManager.getMessage("myprofilMain") + "\n\n" + contractInfo;

        bot.execute(new SendMessage(chatId, profileMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboards));
    }

    private String fetchContractInfo(String chatId) {
        try {
            HttpResponse<String> response = apiClient.getContractInfo(chatId);
            if (response.statusCode() == 200) {
                String responseBody = response.body();

                String contractNumber = extractJsonValue(responseBody, "contractNumber");
                String phoneNumber = extractJsonValue(responseBody, "phoneNumber");
                String address = extractJsonValue(responseBody, "address");

                return String.format(
                        "<b>Контрактный номер:</b> %s\n<b>Номер телефона:</b> %s\n<b>Адрес:</b> %s",
                        contractNumber != null ? contractNumber : "N/A",
                        phoneNumber != null ? phoneNumber : "N/A",
                        address != null ? address : "N/A"
                );
            } else {
                return "Error fetching contract information.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching contract information.";
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey) + searchKey.length();
        if (startIndex == -1) return null;

        int endIndex = json.indexOf(",", startIndex);
        if (json.charAt(startIndex) == '"') {
            startIndex++;
            endIndex = json.indexOf("\"", startIndex);
        }
        return endIndex != -1 ? json.substring(startIndex, endIndex).replace("\"", "") : null;
    }
}