package com.telegram.bot.openai;

import com.telegram.bot.dto.ChatCompletionRequestDto;
import com.telegram.bot.dto.ChatCompletionResponseDto;
import com.telegram.bot.dto.GptMessageDto;
import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.CreateTranscriptionRequestEntity;
import com.telegram.bot.entity.TranscriptionResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
public class OpenAiClient {

    private final RestTemplate restTemplate;
    private final String token;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public ChatCompletionResponseDto createChatCompletion(ChatCompletionRequestEntity requestEntity) {

        List<GptMessageDto> messages = requestEntity.getChatHistory().getMessages().stream()
                .map(msg -> new GptMessageDto(msg.getRole(), msg.getContent()))
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
    public TranscriptionResponseEntity createTranscription(
            CreateTranscriptionRequestEntity request
    ) {
        String url = "https://api.openai.com/v1/audio/transcriptions";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Content-type", "multipart/form-data");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(request.getAudioFilePath()));
        body.add("model", request.getModel());

        var httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<TranscriptionResponseEntity> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, TranscriptionResponseEntity.class
        );

        TranscriptionResponseEntity result = responseEntity.getBody();
        if (result != null) {
            result.setCreateTranscriptionRequest(request); // 🔥 ВАЖНО!
        }

        return result;
    }


}

