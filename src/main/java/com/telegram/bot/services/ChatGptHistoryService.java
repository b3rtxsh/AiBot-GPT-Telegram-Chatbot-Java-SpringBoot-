package com.telegram.bot.services;

import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.GptMessage;
import com.telegram.bot.repository.ChatHistoryRepository;
import com.telegram.bot.repository.GptMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatGptHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final GptMessageRepository gptMessageRepository;

    @Transactional
    public ChatHistory createHistory(Long userId) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(userId);
        chatHistory.setCreatedAt(LocalDateTime.now());
        return chatHistoryRepository.save(chatHistory);
    }

    @Transactional
    public ChatHistory createHistoryIfNotExist(Long userId) {
        return chatHistoryRepository.findById(userId)
                .orElseGet(() -> createHistory(userId));
    }

    @Transactional
    public ChatHistory addMessageToHistory(Long userId, GptMessage gptMessage) {
        ChatHistory chatHistory = createHistoryIfNotExist(userId);

        gptMessage.setChatHistory(chatHistory);
        chatHistory.getMessages().add(gptMessage);

        gptMessageRepository.save(gptMessage);

        return chatHistory;
    }


    public void clearHistory(Long userId) {
        chatHistoryRepository.deleteById(userId);
    }
}
