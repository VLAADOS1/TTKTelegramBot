package com.vlaados;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.HashMap;
import java.util.Map;

public class UserSessionManager {

    private final Map<Long, Boolean> awaitingInput = new HashMap<>();

    public boolean isAwaitingInput(long chatId) {
        return awaitingInput.getOrDefault(chatId, false);
    }

    public void setAwaitingInput(long chatId) {
        awaitingInput.put(chatId, true);
    }

    public void reset(long chatId) {
        awaitingInput.put(chatId, false);
    }
}