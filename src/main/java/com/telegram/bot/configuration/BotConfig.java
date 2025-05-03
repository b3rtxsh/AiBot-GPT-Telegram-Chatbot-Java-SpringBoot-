package com.telegram.bot.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
@PropertySource("application.properties")
@ComponentScan(basePackages = "com.telegram.bot")
@EnableJpaRepositories(basePackages = "com.telegram.bot.repository")
@EntityScan(basePackages = "com.telegram.bot.entity")
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
