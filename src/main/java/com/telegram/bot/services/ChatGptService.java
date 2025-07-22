package com.telegram.bot.services;

import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.ChatCompletionResponseEntity;
import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.UserMessage;
import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.repository.ChatCompletionRequestRepository;
import com.telegram.bot.repository.ChatCompletionResponseRepository;
import com.telegram.bot.repository.UserMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final OpenAiClient openAiClient;
    private final ChatGptHistoryService chatGptHistoryService;
    private final ChatCompletionRequestRepository chatCompletionRequestRepository;
    private final ChatCompletionResponseRepository chatCompletionResponseRepository;
    private final UserMessageRepository userMessageRepository;

    @Transactional
    public String getResponseChatForUser(Long userId, String userTextInput) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {

                ChatHistory chatHistory = chatGptHistoryService.addMessageToHistory(
                        userId,
                        UserMessage.builder().content(userTextInput).role("user").build()
                );


                UserMessage firstMessage = chatHistory.getMessages().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No messages found in chat history"));

                var requestEntity = ChatCompletionRequestEntity.builder()
                        .model("gpt-4")
                        .messages(firstMessage)
                        .chatHistory(chatHistory)
                        .build();

                firstMessage.setCompletion(requestEntity);
                requestEntity.setMessages(firstMessage);

                chatCompletionRequestRepository.save(requestEntity);


                var response = openAiClient.createChatCompletion(requestEntity);

                var responseEntity = ChatCompletionResponseEntity.builder()
                        .content(response.getChoices().get(0).getMessage().getContent())
                        .chatCompletionRequest(requestEntity)
                        .build();


                chatCompletionResponseRepository.save(responseEntity);

                var messageFromGpt = response.getChoices().get(0).getMessage();


               chatGptHistoryService.addMessageToHistory(userId, firstMessage);

                return messageFromGpt.getContent();

            } catch (Exception e) {
                if (e instanceof jakarta.persistence.OptimisticLockException && attempt < 2) {
                    System.out.println("Retrying transaction due to OptimisticLockException...");
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Unexpected error in transaction.");
    }
}