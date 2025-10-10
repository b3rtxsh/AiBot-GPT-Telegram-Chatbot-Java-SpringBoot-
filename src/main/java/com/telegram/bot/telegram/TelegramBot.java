package com.telegram.bot.telegram;

import com.telegram.bot.configuration.TelegramBotProperties;
import com.telegram.bot.telegram.message.TelegramUpdateMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumbnail;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;

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

    @Override
    public Boolean execute(SetStickerSetThumbnail setStickerSetThumbnail) throws TelegramApiException {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail setStickerSetThumbnail) {
        return null;
    }
}