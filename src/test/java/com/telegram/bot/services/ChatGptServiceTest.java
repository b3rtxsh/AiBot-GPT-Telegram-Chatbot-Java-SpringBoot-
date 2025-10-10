package com.telegram.bot.services;

import com.telegram.bot.dto.ChatCompletionResponseDto;
import com.telegram.bot.dto.Choice;
import com.telegram.bot.dto.MessageDto;
import com.telegram.bot.entity.ChatCompletionRequestEntity;
import com.telegram.bot.entity.ChatHistory;
import com.telegram.bot.entity.Message;
import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.repository.ChatCompletionRequestRepository;
import com.telegram.bot.repository.UserMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChatGptServiceTest {

    @Mock
    private OpenAiClient openAiClient;
    @Mock
    private ChatGptHistoryService chatGptHistoryService;
    @Mock
    private ChatCompletionRequestRepository chatCompletionRequestRepository;
    @Mock
    private UserMessageRepository userMessageRepository;
    @Mock
    private SafeDbExecutor safeDbExecutor;

    @InjectMocks
    private ChatGptService chatGptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // safeDbExecutor.execute → выполняем supplier.get()
        lenient().when(safeDbExecutor.execute(any(), anyString()))
                .thenAnswer(invocation -> {
                    var supplier = (java.util.function.Supplier<?>) invocation.getArgument(0);
                    return supplier.get();
                });

        // safeDbExecutor.run → просто выполняем runnable.run()
        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(safeDbExecutor).run(any(), anyString());
    }


    @Test
    void getResponseChatForUser_returnsGptResponse_andSavesMessages() {
        // given
        Long userId = 1L;
        String userInput = "Привет, бот!";

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(userId);

        when(chatGptHistoryService.addMessageToHistory(eq(userId), any(Message.class)))
                .thenReturn(chatHistory);

        // --- мок ответа от OpenAI ---
        MessageDto gptMessage = MessageDto.builder()
                .role("assistant")
                .content("Привет, человек!")
                .build();

        Choice choice = Choice.builder()
                .index(0)
                .message(gptMessage)
                .finishReason("stop")
                .build();

        ChatCompletionResponseDto responseDto = ChatCompletionResponseDto.builder()
                .choices(List.of(choice))
                .build();

        when(openAiClient.createChatCompletion(any(ChatCompletionRequestEntity.class)))
                .thenReturn(responseDto);

        // when
        String result = chatGptService.getResponseChatForUser(userId, userInput);

        // then
        assertThat(result).isEqualTo("Привет, человек!");

        verify(chatCompletionRequestRepository).save(any(ChatCompletionRequestEntity.class));
        verify(userMessageRepository, atLeastOnce()).save(any(Message.class));
        verify(chatGptHistoryService, times(2)).addMessageToHistory(eq(userId), any(Message.class));
        verify(openAiClient).createChatCompletion(any(ChatCompletionRequestEntity.class));
    }
}
