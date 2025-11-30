package com.telegram.bot.telegram.message;

import com.telegram.bot.services.ChatGptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramTextHandlerTest {

    @Mock
    private ChatGptService gptService;

    @InjectMocks
    private TelegramTextHandler handler;

    @Test
    void shouldReturnSendMessageWithGptResponse() {
        // given
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);
        when(message.getText()).thenReturn("Hello");
        when(gptService.getResponseChatForUser(123L, "Hello"))
                .thenReturn("Hi there!");

        // when
        SendMessage result = handler.processTextMessage(message);

        // then
        assertThat(result.getChatId()).isEqualTo("123");
        assertThat(result.getText()).isEqualTo("Hi there!");
        verify(gptService).getResponseChatForUser(123L, "Hello");
    }
}

