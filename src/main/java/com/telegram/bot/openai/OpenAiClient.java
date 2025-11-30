package com.telegram.bot.openai;

import com.telegram.bot.dto.*;
import com.telegram.bot.entity.ChatCompletionRequestEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate;
    private final String token;

    private static final String CHAT_COMPLETION_URL = "https://api.openai.com/v1/chat/completions";
    private static final String TRANSCRIPTION_URL = "https://api.openai.com/v1/audio/transcriptions";

    /**
     * Отправляет запрос в OpenAI Chat API и возвращает полный ответ DTO.
     */
    public ChatCompletionResponseDto createChatCompletion(ChatCompletionRequestEntity requestEntity) {
        // Преобразуем историю сообщений в DTO
        List<MessageDto> messages = requestEntity.getChatHistory().getMessages().stream()
                .map(msg -> new MessageDto(msg.getRole(), msg.getContent()))
                .toList();

        // Формируем тело запроса
        ChatCompletionRequestDto requestDto = new ChatCompletionRequestDto(
                requestEntity.getModel(),
                messages
        );

        // Заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ChatCompletionRequestDto> httpEntity = new HttpEntity<>(requestDto, headers);

        // Отправляем POST запрос
        ResponseEntity<ChatCompletionResponseDto> response = restTemplate.exchange(
                CHAT_COMPLETION_URL,
                HttpMethod.POST,
                httpEntity,
                ChatCompletionResponseDto.class
        );

        // Возвращаем тело (DTO)
        return Objects.requireNonNull(response.getBody(), "Response body is null");
    }

    /**
     * Отправляет аудио файл на Whisper для транскрипции.
     */
    @SneakyThrows
    public String createTranscription(File audioFile, String model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", model);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TranscriptionResponseDto> response = restTemplate.exchange(
                TRANSCRIPTION_URL,
                HttpMethod.POST,
                httpEntity,
                TranscriptionResponseDto.class
        );

        return Objects.requireNonNull(response.getBody(), "Transcription response is null").getText();
    }
}
