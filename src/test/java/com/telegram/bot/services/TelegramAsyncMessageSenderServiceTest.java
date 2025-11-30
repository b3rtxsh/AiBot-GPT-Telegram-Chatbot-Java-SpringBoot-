package com.telegram.bot.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class TelegramAsyncMessageSenderServiceTest {

    @Mock
    private TelegramExecutor telegramExecutor;

    @InjectMocks
    private TelegramAsyncMessageSenderService service;

    @Test
    void shouldSendMessageAsyncSuccessfully() throws Exception {
        SendMessage initial = SendMessage.builder().chatId("1").text("init").build();
        SendMessage result = SendMessage.builder().chatId("1").text("done").build();
        org.telegram.telegrambots.meta.api.objects.Message sentMessage = new org.telegram.telegrambots.meta.api.objects.Message();
        sentMessage.setMessageId(10);

        when(telegramExecutor.execute(any(SendMessage.class))).thenReturn(sentMessage);
        when(telegramExecutor.execute(any(EditMessageText.class))).thenReturn(null);

        service.sendMessageAsync("1", () -> result, e -> result);

        verify(telegramExecutor, timeout(1000)).execute(any(EditMessageText.class));
    }

    @Test
    void shouldHandleErrorWithOnErrorHandler() throws Exception {
        // given
        Message initialMessage = new Message();
        initialMessage.setMessageId(777);

        when(telegramExecutor.execute(any(SendMessage.class))).thenReturn(initialMessage);

        // when
        service.sendMessageAsync(
                "77",
                () -> { throw new RuntimeException("Test failure"); },
                ex -> SendMessage.builder().chatId("77").text("Recovered after error").build()
        );

        // then
        Thread.sleep(400);

        verify(telegramExecutor, atLeastOnce()).execute(argThat(edit ->
                edit instanceof EditMessageText &&
                        ((EditMessageText) edit).getText().equals("Recovered after error")
        ));
    }
}
