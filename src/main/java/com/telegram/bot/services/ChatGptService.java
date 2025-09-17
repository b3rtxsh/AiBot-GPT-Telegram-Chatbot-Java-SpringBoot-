package com.telegram.bot.services;

import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.Message;
import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.repository.ChatCompletionRequestRepository;
import com.telegram.bot.repository.UserMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final OpenAiClient openAiClient;
    private final ChatGptHistoryService chatGptHistoryService;
    private final ChatCompletionRequestRepository chatCompletionRequestRepository;
    private final UserMessageRepository userMessageRepository;
    private final SafeDbExecutor safeDbExecutor;

    public String getResponseChatForUser(Long userId, String userTextInput) {

        Message userMessage = Message.builder()
                .content(userTextInput)
                .role("user")
                .build();

        ChatHistory chatHistory = safeDbExecutor.execute(
                () -> chatGptHistoryService.addMessageToHistory(userId, userMessage),
                "Ошибка сохранения истории для userId=" + userId
        );
        var requestEntity = ChatCompletionRequestEntity.builder()
                .model("gpt-4")
                .messages(userMessage)
                .chatHistory(chatHistory)
                .build();

        userMessage.setCompletion(requestEntity);

        safeDbExecutor.run(
                () -> chatCompletionRequestRepository.save(requestEntity),
                "Ошибка сохранения ChatCompletionRequestEntity"
        );


                var response = openAiClient.createChatCompletion(requestEntity);

                var responseEntity = Message.builder()
                        .content(response.getChoices().get(0).getMessage().getContent())
                        .completion(requestEntity)
                        .chatHistory(userMessage.getChatHistory())
                        .role("assistant")
                        .build();

        safeDbExecutor.run(
                () -> userMessageRepository.save(responseEntity),
                "Ошибка сохранения ответа от GPT"
        );

        safeDbExecutor.run(
                () -> chatGptHistoryService.addMessageToHistory(userId, userMessage),
                "Ошибка обновления истории для userId=" + userId
        );

                var messageFromGpt = response.getChoices().get(0).getMessage();

                return messageFromGpt.getContent();

    }
}