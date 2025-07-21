package com.telegram.bot.telegram;

import com.telegram.bot.configuration.TelegramBotProperties;
import com.telegram.bot.telegram.message.TelegramUpdateMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramBot extends TelegramWebhookBot {

    private final TelegramUpdateMessageHandler telegramUpdateMessageHandler;
    private final TelegramBotProperties properties;

    public TelegramBot(TelegramUpdateMessageHandler handler, TelegramBotProperties properties) {
        super(properties.getToken());
        this.telegramUpdateMessageHandler = handler;
        this.properties = properties;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return update.hasMessage()
                    ? telegramUpdateMessageHandler.handleMessage(update.getMessage())
                    : null;
        } catch (Exception e) {
            log.error("UPDATE ERROR", e);
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text("REQUEST ERROR")
                    .build();
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }


    @Override
    public String getBotPath() {
        return properties.getPath();
    }
}