package com.vlaados.commands;

import com.pengrad.telegrambot.model.Update;

public interface CommandHandler {
    boolean canHandle(String command);
    void handle(Update update);
}