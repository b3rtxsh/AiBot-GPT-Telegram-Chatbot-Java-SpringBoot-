package com.telegram.bot.configuration;

import com.telegram.bot.telegram.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
public class BotInitializer {

    private final TelegramBot telegramBot;
    private final TelegramBotProperties properties;

    @Value("${telegram.webhook.url}")
    private String webhookBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void initWebhook() {
        String fullWebhookUrl = webhookBaseUrl + "/" + telegramBot.getBotPath();
        String token = properties.getToken();

        try {
            String deleteUrl = "https://api.telegram.org/bot" + token + "/deleteWebhook";
            String setUrl = "https://api.telegram.org/bot" + token + "/setWebhook?url=" + fullWebhookUrl;

            new RestTemplate().getForObject(deleteUrl, String.class);
            new RestTemplate().getForObject(setUrl, String.class);

            System.out.println("✅ Webhook updated to: " + fullWebhookUrl);
        } catch (Exception e) {
            System.err.println("❌ Ошибка установки вебхука: " + e.getMessage());
        }
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        SetWebhook setWebhook = SetWebhook.builder().url(webhookBaseUrl + "/" + telegramBot.getBotPath()).build();

        botsApi.registerBot(telegramBot, setWebhook);
        return botsApi;
    }
}
