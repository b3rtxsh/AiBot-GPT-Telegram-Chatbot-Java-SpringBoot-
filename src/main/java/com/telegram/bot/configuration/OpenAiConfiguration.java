package com.telegram.bot.configuration;

import com.telegram.bot.openai.OpenAiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfiguration {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
    @Bean
    public OpenAiClient openAiClient(
            @Value("${openai.token}") String botToken,
            RestTemplateBuilder restTemplateBuilder
    ) {
        return new OpenAiClient(restTemplateBuilder.build(), botToken);
    }

}

