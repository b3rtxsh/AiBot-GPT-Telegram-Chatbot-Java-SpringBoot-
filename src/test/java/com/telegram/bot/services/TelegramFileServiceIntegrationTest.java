package com.telegram.bot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TelegramFileServiceIntegrationTest {

    private DefaultAbsSender defaultAbsSender;
    private TelegramFileService spyService;

    @BeforeEach
    void setUp() {
        defaultAbsSender = mock(DefaultAbsSender.class);
        TelegramFileService realService = new TelegramFileService(defaultAbsSender, "dummy_token");
        spyService = Mockito.spy(realService);
    }

    @Test
    void shouldDownloadFileSuccessfully() throws Exception {
        // given
        File mockFile = mock(File.class);
        java.io.File tempFile = java.io.File.createTempFile("test_file", ".ogg");

        when(mockFile.getFileUrl(any())).thenReturn("https://test.com/file.ogg");
        when(defaultAbsSender.execute(any(GetFile.class))).thenReturn(mockFile);

        // подменяем приватный метод getFileFromUrl
        doReturn(tempFile).when(spyService).getFileFromUrl(anyString());

        // when
        java.io.File result = spyService.getFile("some_file_id");

        // then
        assertNotNull(result);
        assertEquals(tempFile, result);
        verify(spyService).getFileFromUrl(anyString());
        verify(defaultAbsSender).execute(any(GetFile.class));
    }

    @Test
    void shouldThrowWhenDownloadFails() throws Exception {
        // given
        File mockFile = mock(File.class);
        when(mockFile.getFileUrl(any())).thenReturn("https://broken.com/file.ogg");
        when(defaultAbsSender.execute(any(GetFile.class))).thenReturn(mockFile);

        // симулируем ошибку скачивания файла
        doThrow(new RuntimeException("Network error"))
                .when(spyService).getFileFromUrl(anyString());

        // then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.getFile("some_file_id"));
        assertEquals("Network error", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTelegramApiFails() throws Exception {
        // given: Telegram API падает
        when(defaultAbsSender.execute(any(GetFile.class)))
                .thenThrow(new TelegramApiException("Telegram API error"));

        // then
        TelegramApiException ex = assertThrows(TelegramApiException.class, () -> spyService.getFile("some_file_id"));
        assertEquals("Telegram API error", ex.getMessage());
        verify(defaultAbsSender).execute(any(GetFile.class));
    }
}
