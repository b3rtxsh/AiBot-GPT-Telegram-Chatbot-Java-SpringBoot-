package com.telegram.bot.telegram.message;

import com.telegram.bot.services.ChatGptService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@AllArgsConstructor
public class TelegramTextHandler {

    private final ChatGptService gptService;
    public SendMessage processTextMessage(Message message) {
        var text = message.getText();
        var chatId = message.getChatId();
        if(!text.matches(".*[<>\"'`;\\\\].*")) {
            var gptGeneratedText = gptService.getResponseChatForUser(chatId, text);
            return new SendMessage(chatId.toString(), gptGeneratedText);
        } else {
            throw new IllegalStateException("Denied symbols");
        }
    }
}

