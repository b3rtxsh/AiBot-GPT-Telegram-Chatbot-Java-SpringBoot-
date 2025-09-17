package com.telegram.bot.telegram.message;


import com.telegram.bot.entity.TranscriptionRequestEntity;
import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.services.ChatGptHistoryService;
import com.telegram.bot.services.ChatGptService;
import com.telegram.bot.services.TelegramFileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import java.io.File;

@Service
@AllArgsConstructor
public class TelegramVoiceHandler {

    private final ChatGptService gptService;
    private final TelegramFileService telegramFileService;
    private final OpenAiClient openAiClient;

    public SendMessage processVoice(org.telegram.telegrambots.meta.api.objects.Message message) {
        Long chatId = message.getChatId();
        String model = "whisper-1";
        String fileId = message.getVoice().getFileId();

        File audioFile = telegramFileService.getFile(fileId);
        var transcribedText = openAiClient.createTranscription(audioFile, model);


        String gptReply = gptService.getResponseChatForUser(chatId, transcribedText);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(gptReply)
                .build();
    }

}
