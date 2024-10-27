package com.vlaados.buttons.button;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.vlaados.api.ApiClient;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.classification.GetKey;
import com.vlaados.config.BotConfig;
import com.vlaados.local.LocalManager;
import okhttp3.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class mainapp implements ButtonHandler {

    private final TelegramBot bot;


    private final Map<Long, Boolean> isListening = new HashMap<>();

    public mainapp(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean canHandle(String buttonData) {
        return "mainapp".equalsIgnoreCase(buttonData);
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
        String appconnect = localManager.getMessage("appconnect");
        bot.execute(new SendMessage(chatId, appconnect));
        isListening.put(update.callbackQuery().message().chat().id(), true);
    }

    public void handleMessage(Update update) {
        long chatId = update.message().chat().id();
        String language = null;

        ApiClient apiClient = new ApiClient();

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalManager localManager = new LocalManager(language);
        String error = localManager.getMessage("error");

        if (!isListening.getOrDefault(chatId, false)) {
            return;
        }

        if (update.message().voice() != null) {
            GetFile getFile = new GetFile(update.message().voice().fileId());
            GetFileResponse fileResponse;
            try {
                fileResponse = bot.execute(getFile);
            } catch (Exception e) {
                bot.execute(new SendMessage(String.valueOf(chatId), error));
                e.printStackTrace();
                isListening.put(chatId, false);
                return;
            }

            String fileUrl = bot.getFullFilePath(fileResponse.file());
            sendVoiceUrlToServer(fileUrl, chatId);
        } else if (update.message().text() != null) {
            String userText = update.message().text();
            sendTextToServer(userText, chatId);
            isListening.put(chatId, false);
        }
    }

    private void sendVoiceUrlToServer(String fileUrl, long chatId) {
        BotConfig botConfig = new BotConfig();
        String serverAddress = botConfig.getIp();
        int serverPort = botConfig.getPort();
        String language = null;

        ApiClient apiClient = new ApiClient();

        try {
            language = apiClient.extractLocal(String.valueOf(chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalManager localManager = new LocalManager(language);
        String error = localManager.getMessage("error");

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            byte[] urlBytes = fileUrl.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(urlBytes.length);
            dos.write(urlBytes);
            dos.flush();

            String response;
            StringBuilder resultText = new StringBuilder();
            while ((response = in.readLine()) != null) {
                resultText.append(response).append("\n");
            }

            sendTextToServer(resultText.toString().trim(), chatId);
            isListening.put(chatId, false);

        } catch (IOException e) {
            bot.execute(new SendMessage(String.valueOf(chatId), error));
            e.printStackTrace();
            isListening.put(chatId, false);
        }
    }

    private void sendTextToServer(String text, long chatId) {
        OkHttpClient client = new OkHttpClient();
        BotConfig botConfig = new BotConfig();
        String serverAddress = "http://" + botConfig.getIpTip() + ":" + botConfig.getPortTip();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"text\":\"" + text + "\"}"
        );

        Request request = new Request.Builder()
                .url(serverAddress)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                String key = GetKey.getkey(Integer.parseInt(responseBody));

                String language = null;

                ApiClient apiClient = new ApiClient();

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LocalManager localManager = new LocalManager(language);
                String bottonkey = localManager.getMessage(key);
                String print = localManager.getMessage("getrequest");


                InlineKeyboardButton button = new InlineKeyboardButton(print).callbackData(key);
                InlineKeyboardMarkup keyboards = new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                        {button}
                });

                if(!key.equals("null")) {
                    bot.execute(new SendMessage(String.valueOf(chatId), bottonkey).replyMarkup(keyboards));
                } else{
                    bot.execute(new SendMessage(String.valueOf(chatId), bottonkey));
                }

            } else {
                String language = null;

                ApiClient apiClient = new ApiClient();

                try {
                    language = apiClient.extractLocal(String.valueOf(chatId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LocalManager localManager = new LocalManager(language);
                String error = localManager.getMessage("error");
                bot.execute(new SendMessage(String.valueOf(chatId), error));
            }
        } catch (IOException e) {
            e.printStackTrace();
            String language = null;

            ApiClient apiClient = new ApiClient();

            try {
                language = apiClient.extractLocal(String.valueOf(chatId));
            } catch (Exception es) {
                es.printStackTrace();
            }
            LocalManager localManager = new LocalManager(language);
            String error = localManager.getMessage("error");
            bot.execute(new SendMessage(String.valueOf(chatId), error));
        }
    }
}