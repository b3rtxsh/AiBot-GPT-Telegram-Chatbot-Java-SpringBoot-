package com.telegram.bot.openai;

import com.telegram.bot.dto.*;
import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAiClientTest {

        @Mock
        private RestTemplate restTemplate;

        private OpenAiClient openAiClient;

        private final String token = "test-token";

        @BeforeEach
        void setUp() {
            openAiClient = new OpenAiClient(restTemplate, token);
        }

    @Test
    void shouldCreateChatCompletionSuccessfully() {
        // given
        ChatHistory history = new ChatHistory();
        Message message = new Message();
        message.setRole("user");
        message.setContent("Hello!");
        history.setMessages(List.of(message));

        ChatCompletionRequestEntity entity = new ChatCompletionRequestEntity();
        entity.setModel("gpt-4");
        entity.setChatHistory(history);

        ChatCompletionResponseDto mockResponse = new ChatCompletionResponseDto(
                "id-123",
                "object",
                "model",
                List.of(new ChatChoiceDto(0, new MessageDto("assistant", "Hi there!"), null)),
                new UsageDto(10, 20, 30)
        );

        ResponseEntity<ChatCompletionResponseDto> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/chat/completions"), // строгое сравнение
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ChatCompletionResponseDto.class)
        )).thenReturn(responseEntity);



        // when
        ChatCompletionResponseDto result = openAiClient.createChatCompletion(entity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getChoices()).hasSize(1);
        assertThat(result.getChoices().get(0).getMessage().getContent()).isEqualTo("Hi there!");

        verify(restTemplate).exchange(
                eq("https://api.openai.com/v1/chat/completions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(ChatCompletionResponseDto.class)
        );
    }



    @Test
    void shouldCreateTranscriptionSuccessfully() {
        // given
        File mockFile = new File("test.wav");
        TranscriptionResponseDto mockTranscription = new TranscriptionResponseDto("Recognized text");

        ResponseEntity<TranscriptionResponseDto> responseEntity =
                new ResponseEntity<>(mockTranscription, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://api.openai.com/v1/audio/transcriptions"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TranscriptionResponseDto.class)
        )).thenReturn(responseEntity);

        // when
        String text = openAiClient.createTranscription(mockFile, "whisper-1");

        // then
        assertThat(text).isEqualTo("Recognized text");

        ArgumentCaptor<HttpEntity<MultiValueMap<String, Object>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("https://api.openai.com/v1/audio/transcriptions"),
                eq(HttpMethod.POST),
                captor.capture(),
                eq(TranscriptionResponseDto.class)
        );

        // Проверяем, что тело запроса содержит file и model
        MultiValueMap<String, Object> body = captor.getValue().getBody();
        assertThat(body).containsKeys("file", "model");
        assertThat(body.getFirst("file")).isInstanceOf(FileSystemResource.class);
        assertThat(body.getFirst("model")).isEqualTo("whisper-1");
    }

    @Test
    void shouldThrowWhenResponseBodyIsNullForChatCompletion() {
        ChatHistory history = new ChatHistory();
        history.setMessages(List.of(new Message("user", "test")));

        ChatCompletionRequestEntity entity = new ChatCompletionRequestEntity();
        entity.setModel("gpt-4");
        entity.setChatHistory(history);

        ResponseEntity<ChatCompletionResponseDto> emptyResponse =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(ChatCompletionResponseDto.class)
        )).thenReturn(emptyResponse);

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () ->
                openAiClient.createChatCompletion(entity)
        );
    }

    @Test
    void shouldThrowWhenResponseBodyIsNullForTranscription() {
        File mockFile = new File("test.wav");

        ResponseEntity<TranscriptionResponseDto> emptyResponse =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(TranscriptionResponseDto.class)
        )).thenReturn(emptyResponse);

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () ->
                openAiClient.createTranscription(mockFile, "whisper-1")
        );
    }
}

