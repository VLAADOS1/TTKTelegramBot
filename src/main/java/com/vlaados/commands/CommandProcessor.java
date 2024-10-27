package com.vlaados.commands;

import com.pengrad.telegrambot.model.Update;

import java.util.List;


public class CommandProcessor {

    private final List<CommandHandler> commandHandlers;

    public CommandProcessor(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    public void process(Update update) {
        String messageText = update.message().text();
        String command = messageText.split(" ")[0];

        for (CommandHandler handler : commandHandlers) {
            if (handler.canHandle(command)) {
                handler.handle(update);
                return;
            }
        }
    }
}