package com.vlaados.buttons;

import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class ButtonProcessor {

    private final List<ButtonHandler> buttonHandlers;

    public ButtonProcessor(List<ButtonHandler> buttonHandlers) {
        this.buttonHandlers = buttonHandlers;
    }

    public void process(Update update) {
        String buttonData = update.callbackQuery().data();

        for (ButtonHandler handler : buttonHandlers) {
            if (handler.canHandle(buttonData)) {
                handler.handle(update);
                return;
            }
        }
    }
}