package com.telegram.bot.openai;

import com.telegram.bot.dto.ChatCompletionRequestDto;
import com.telegram.bot.dto.ChatCompletionResponseDto;
import com.telegram.bot.dto.MessageDto;
import com.telegram.bot.dto.TranscriptionResponseDto;
import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.Message;
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

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public ChatCompletionResponseDto createChatCompletion(ChatCompletionRequestEntity requestEntity) {

        List<MessageDto> messages = requestEntity.getChatHistory().getMessages().stream()
                .map(msg -> new MessageDto(msg.getRole(), msg.getContent()))
                .toList();


        ChatCompletionRequestDto requestDto = new ChatCompletionRequestDto(
                requestEntity.getModel(),
                messages
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ChatCompletionRequestDto> httpEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<ChatCompletionResponseDto> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                httpEntity,
                ChatCompletionResponseDto.class
        );

        return response.getBody();
    }

    @SneakyThrows
    public String createTranscription(File audioFile, String model) {
        String url = "https://api.openai.com/v1/audio/transcriptions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", model);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TranscriptionResponseDto> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, TranscriptionResponseDto.class
        );

        return Objects.requireNonNull(response.getBody()).getText();
    }


}

