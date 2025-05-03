package com.telegram.bot.telegram;

import com.telegram.bot.telegram.message.TelegramUpdateMessageHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramUpdateMessageHandler telegramUpdateMessageHandler;

    public TelegramBot(
            @Value("${bot.token}") String botToken,
            TelegramUpdateMessageHandler telegramUpdateMessageHandler
    ) {
        super(new DefaultBotOptions(), botToken);
        this.telegramUpdateMessageHandler = telegramUpdateMessageHandler;
    }

    @Override
    public String getBotUsername() {
        return "${bot.name}";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            var method = processUpdate(update);
            if (method != null ) {
                sendApiMethod(method);
            }
        } catch (Exception e) {
            log.error("Error while processing update", e);
            sendUserErrorMessage(update.getMessage().getChatId());
        }

    }

//    private void startCommandReceived(Long chatId, String name) {
//        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
//                "Enter the currency whose exchange rate" + "\n" +
//                "you want to know in relation to USD." + "\n" +
//                "For example: ETH";
//        sendMessage(chatId, answer);
//    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.info("Stacktrace - {}", e);
        }
    }

    private BotApiMethod<?> processUpdate(Update update) {
        return update.hasMessage()
                ? telegramUpdateMessageHandler.handleMessage(update.getMessage())
                : null;
    }

    @SneakyThrows
    private void sendUserErrorMessage(Long userId) {
        sendApiMethod(SendMessage.builder()
                .chatId(userId)
                .text("Произошла ошибка, попробуйте позже")
                .build());
    }
}