package com.telegram.bot.services;

import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.Message;
import com.telegram.bot.repository.ChatHistoryRepository;
import com.telegram.bot.repository.UserMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // поднимает in-memory H2 + конфиг JPA
@Import(ChatGptHistoryService.class)
@ActiveProfiles("test")

class ChatGptHistoryServiceTest {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private ChatGptHistoryService chatGptHistoryService;

    @Test
    void createHistory_savesNewHistory() {
        // when
        ChatHistory history = chatGptHistoryService.createHistory(1L);

        // then
        Optional<ChatHistory> fromDb = chatHistoryRepository.findById(1L);
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getCreatedAt()).isNotNull();
    }

    @Test
    void addMessageToHistory_savesMessageAndLinksToHistory() {
        // given
        Long userId = 2L;
        Message message = Message.builder()
                .role("user")
                .content("Привет!")
                .build();

        // when
        ChatHistory history = chatGptHistoryService.addMessageToHistory(userId, message);

        // then
        ChatHistory fromDb = chatHistoryRepository.findById(userId).orElseThrow();
        assertThat(fromDb.getMessages()).hasSize(1);

        Message savedMessage = userMessageRepository.findAll().get(0);
        assertThat(savedMessage.getContent()).isEqualTo("Привет!");
        assertThat(savedMessage.getChatHistory().getId()).isEqualTo(userId);
    }
}
