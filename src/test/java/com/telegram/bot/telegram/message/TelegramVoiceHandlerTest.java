package com.telegram.bot.telegram.message;

import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.services.ChatGptService;
import com.telegram.bot.services.TelegramFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TelegramVoiceHandlerTest {

    @Mock
    private ChatGptService gptService;

    @Mock
    private TelegramFileService telegramFileService;

    @Mock
    private OpenAiClient openAiClient;

    @InjectMocks
    private TelegramVoiceHandler telegramVoiceHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessVoiceAndReturnSendMessage() {
        // given
        long chatId = 12345L;
        String fileId = "voice_file_id";
        String transcribedText = "Привет, как дела?";
        String gptReply = "Все отлично, чем могу помочь?";

        File mockFile = mock(File.class);

        // Telegram message mock
        Message telegramMessage = mock(Message.class);
        Voice voice = mock(Voice.class);

        when(telegramMessage.getChatId()).thenReturn(chatId);
        when(telegramMessage.getVoice()).thenReturn(voice);
        when(voice.getFileId()).thenReturn(fileId);

        // Service mocks
        when(telegramFileService.getFile(fileId)).thenReturn(mockFile);
        when(openAiClient.createTranscription(mockFile, "whisper-1")).thenReturn(transcribedText);
        when(gptService.getResponseChatForUser(chatId, transcribedText)).thenReturn(gptReply);

        // when
        SendMessage result = telegramVoiceHandler.processVoice(telegramMessage);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChatId()).isEqualTo(String.valueOf(chatId));
        assertThat(result.getText()).isEqualTo(gptReply);

        // verify interactions
        verify(telegramFileService).getFile(fileId);
        verify(openAiClient).createTranscription(mockFile, "whisper-1");
        verify(gptService).getResponseChatForUser(chatId, transcribedText);
    }
}
