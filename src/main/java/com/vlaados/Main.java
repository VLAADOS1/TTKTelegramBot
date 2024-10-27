package com.vlaados;

import com.pengrad.telegrambot.TelegramBot;
import com.vlaados.config.BotConfig;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        BotConfig botConfig = new BotConfig();
        String token = botConfig.getToken();

        TelegramBot bot = new TelegramBot(token);

        BotManager BotManager = new BotManager(bot);
        BotManager.initializeBot();

    }

}
