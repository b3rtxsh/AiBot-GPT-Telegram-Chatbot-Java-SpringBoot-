package com.telegram.bot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "bot")
public class TelegramBotProperties {
    private String token;
    private String username;
    private String path;
}