package com.telegram.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Chat;

import com.telegram.bot.services.ChatGptService;
import com.telegram.bot.telegram.message.TelegramTextHandler;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class StarterApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}


	@Test
	void contextLoads() {
	}

	@ExtendWith(MockitoExtension.class)
	class TelegramTextHandlerTest {

		@Mock
		private ChatGptService chatGptService;

		@InjectMocks
		private TelegramTextHandler telegramTextHandler;

		@Test
		void shouldReturnSendMessageForUserInput() {
			// given
			Message tgMessage = new Message();
			Chat chat = new Chat();
			chat.setId(12345L);
			tgMessage.setChat(chat);
			tgMessage.setText("Hello bot!");
			when(chatGptService.getResponseChatForUser(12345L, "Hello bot!"))
					.thenReturn("Hi human!");

			// when
			SendMessage result = telegramTextHandler.processTextMessage(tgMessage);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getChatId()).isEqualTo("12345");
			assertThat(result.getText()).isEqualTo("Hi human!");

			verify(chatGptService).getResponseChatForUser(12345L, "Hello bot!");
		}

		@Test
		void shouldThrowExceptionWhenTextContainsDeniedSymbols() {
			// given
			Message tgMessage = new Message();
			Chat chat = new Chat();
			chat.setId(12345L);
			tgMessage.setChat(chat);
			tgMessage.setText("Hello <script>");

			// when / then
			assertThatThrownBy(() -> telegramTextHandler.processTextMessage(tgMessage))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("Denied symbols");

			verifyNoInteractions(chatGptService);
		}
	}

}
