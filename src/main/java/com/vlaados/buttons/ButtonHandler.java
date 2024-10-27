package com.vlaados.buttons;

import com.pengrad.telegrambot.model.Update;

public interface ButtonHandler {
    boolean canHandle(String buttonData);
    void handle(Update update);
}