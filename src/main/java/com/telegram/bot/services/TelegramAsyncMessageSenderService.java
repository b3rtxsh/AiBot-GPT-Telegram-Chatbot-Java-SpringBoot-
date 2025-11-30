package com.telegram.bot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
public class TelegramAsyncMessageSenderService {

    private final TelegramExecutor telegramExecutor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public TelegramAsyncMessageSenderService(TelegramExecutor telegramExecutor) {
        this.telegramExecutor = telegramExecutor;
    }

    public void sendMessageAsync(
            String chatId,
            Supplier<SendMessage> action,
            Function<Throwable, SendMessage> onErrorHandler
    ) {
        log.info("Send message async: chatId={}", chatId);

        try {
            var message = telegramExecutor.execute(SendMessage.builder()
                    .text("Ваш запрос принят в обработку, ожидайте")
                    .chatId(chatId)
                    .build());

            CompletableFuture.supplyAsync(action, executorService)
                    .exceptionally(onErrorHandler)
                    .thenAccept(sendMessage -> {
                        try {
                            log.info("Edit message async: chatId={}", chatId);
                            telegramExecutor.execute(EditMessageText.builder()
                                    .chatId(chatId)
                                    .messageId(message.getMessageId())
                                    .text(sendMessage.getText())
                                    .build());
                        } catch (TelegramApiException e) {
                            log.error("Error while sending edited message", e);
                        }
                    });
        } catch (TelegramApiException e) {
            log.error("Error while sending initial message", e);
        }
    }
}
