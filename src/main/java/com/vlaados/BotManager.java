package com.vlaados;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.vlaados.buttons.ButtonHandler;
import com.vlaados.buttons.ButtonProcessor;
import com.vlaados.buttons.button.*;
import com.vlaados.buttons.sendbutton.*;
import com.vlaados.commands.CommandHandler;
import com.vlaados.commands.CommandProcessor;
import com.vlaados.commands.command.Start;

import java.util.ArrayList;
import java.util.List;

public class BotManager {

    private final TelegramBot bot;
    private final List<CommandHandler> commands = new ArrayList<>();
    private final List<ButtonHandler> buttons = new ArrayList<>();
    private final loginclient loginClientButton;
    private final createcontract createContractButton;
    private final changePhone changePhoneButton;
    private final changeAdress changeAddressButton;
    private final mainapp mainAppButton;

    public BotManager(TelegramBot bot) {
        this.bot = bot;
        this.loginClientButton = new loginclient(bot);
        this.createContractButton = new createcontract(bot);
        this.changePhoneButton = new changePhone(bot);
        this.changeAddressButton = new changeAdress(bot);
        this.mainAppButton = new mainapp(bot);
        registerCommands();
        registerButtons();
    }

    private void registerCommands() {
        commands.add(new Start(bot));
    }

    private void registerButtons() {
        buttons.add(new RussianLanguageButton(bot));
        buttons.add(new EnglishLanguageButton(bot));
        buttons.add(new maininfo(bot));
        buttons.add(mainAppButton);
        buttons.add(new mainprofil(bot));
        buttons.add(new maintp(bot));
        buttons.add(new appmax(bot));
        buttons.add(new appmosh(bot));
        buttons.add(new apphonest(bot));
        buttons.add(new appkasper(bot));
        buttons.add(new appip(bot));
        buttons.add(new appmen(bot));
        buttons.add(new approuter(bot));
        buttons.add(changePhoneButton);
        buttons.add(changeAddressButton);
        buttons.add(loginClientButton);
        buttons.add(createContractButton);
    }

    public void initializeBot() {
        CommandProcessor commandProcessor = new CommandProcessor(commands);
        ButtonProcessor buttonProcessor = new ButtonProcessor(buttons);

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null) {
                    if (update.message().contact() != null) {
                        createContractButton.handleMessage(update);
                    } else if (update.message().voice() != null) {
                        mainAppButton.handleMessage(update);
                    } else if (update.message().text() != null) {
                        mainAppButton.handleMessage(update);
                        commandProcessor.process(update);
                        loginClientButton.handleMessage(update);
                        createContractButton.handleMessage(update);
                        changePhoneButton.handleMessage(update);
                        changeAddressButton.handleMessage(update);
                    }
                } else if (update.callbackQuery() != null) {
                    buttonProcessor.process(update);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}