package com.telegram.bot.services;

import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.UserMessage;
import com.telegram.bot.repository.ChatHistoryRepository;
import com.telegram.bot.repository.UserMessageRepository;
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
    private final UserMessageRepository userMessageRepository;

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
    public ChatHistory addMessageToHistory(Long userId, UserMessage userMessage) {
        ChatHistory chatHistory = createHistoryIfNotExist(userId);

        userMessage.setChatHistory(chatHistory);
        chatHistory.getMessages().add(userMessage);

        userMessageRepository.save(userMessage);

        return chatHistory;
    }


    public void clearHistory(Long userId) {
        chatHistoryRepository.deleteById(userId);
    }
}
