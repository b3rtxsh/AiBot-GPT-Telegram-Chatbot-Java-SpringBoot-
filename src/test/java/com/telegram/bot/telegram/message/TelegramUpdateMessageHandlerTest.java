package com.telegram.bot.telegram.message;

import com.telegram.bot.command.TelegramCommandsDispatcher;
import com.telegram.bot.services.RateLimiterService;
import com.telegram.bot.services.TelegramAsyncMessageSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TelegramUpdateMessageHandlerTest {

    @Mock
    private TelegramCommandsDispatcher dispatcher;
    @Mock
    private TelegramAsyncMessageSenderService asyncSender;
    @Mock
    private TelegramTextHandler textHandler;
    @Mock
    private TelegramVoiceHandler voiceHandler;
    @Mock
    private RateLimiterService rateLimiter;

    @InjectMocks
    private TelegramUpdateMessageHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- TEST 1 ----------
    @Test
    void shouldProcessCommandMessage() {
        Message message = mock(Message.class);
        SendMessage expected = SendMessage.builder()
                .chatId("123")
                .text("Command OK")
                .build();

        when(dispatcher.isCommand(message)).thenReturn(true);
        Mockito.<BotApiMethod<?>>when(dispatcher.processCommand(eq(message)))
                .thenReturn(expected);

        var result = handler.handleMessage(message);

        assertThat(result).isEqualTo(expected);
        verify(dispatcher).processCommand(message);
        verifyNoInteractions(asyncSender, textHandler, voiceHandler);
    }

    // ---------- TEST 2 ----------
    @Test
    void shouldReturnRateLimitMessageWhenTooManyRequests() {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);

        when(rateLimiter.isAllowed("user:123", 5, 10)).thenReturn(false);

        var result = handler.handleMessage(message);

        assertThat(result).isInstanceOf(SendMessage.class);
        var send = (SendMessage) result;
        assertThat(send.getText()).contains("Пожалуйста, не так быстро");

        verifyNoInteractions(asyncSender, textHandler, voiceHandler);
    }

    // ---------- TEST 3 ----------
    @Test
    void shouldHandleTextMessage() {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);
        when(rateLimiter.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(true);
        when(dispatcher.isCommand(message)).thenReturn(false);
        when(message.hasText()).thenReturn(true);
        when(message.hasVoice()).thenReturn(false);

        SendMessage mockResponse = SendMessage.builder()
                .chatId("123")
                .text("Reply text")
                .build();

        when(textHandler.processTextMessage(message)).thenReturn(mockResponse);

        handler.handleMessage(message);

        verify(asyncSender).sendMessageAsync(
                eq("123"),
                any(),
                any()
        );
    }

    // ---------- TEST 4 ----------
    @Test
    void shouldHandleVoiceMessage() {
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);
        when(rateLimiter.isAllowed(anyString(), anyInt(), anyInt())).thenReturn(true);
        when(dispatcher.isCommand(message)).thenReturn(false);
        when(message.hasVoice()).thenReturn(true);
        when(message.hasText()).thenReturn(false);

        SendMessage mockResponse = SendMessage.builder()
                .chatId("123")
                .text("Voice reply")
                .build();

        when(voiceHandler.processVoice(message)).thenReturn(mockResponse);

        handler.handleMessage(message);

        verify(asyncSender).sendMessageAsync(
                eq("123"),
                any(),
                any()
        );
    }

    // ---------- TEST 5 ----------
    @Test
    void shouldReturnErrorMessageWhenAsyncThrows() {
        String chatId = "123";
        var error = new RuntimeException("Test exception");

        SendMessage result = ReflectionTestUtils.invokeMethod(
                handler,
                "getErrorMessage",
                error,
                chatId
        );

        assertThat(result).isNotNull();
        assertThat(result.getChatId()).isEqualTo(chatId);
        assertThat(result.getText()).contains("Произошла ошибка");
    }
}
