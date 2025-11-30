package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionResponseDto {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatChoiceDto> choices;
    private UsageDto usage;

    public ChatCompletionResponseDto(String id, String object, String model, List<ChatChoiceDto> choices, UsageDto usage) {
        this.id = id;
        this.object = object;
        this.model = model;
        this.choices = choices;
        this.usage = usage;
        this.created = System.currentTimeMillis() / 1000; // любое значение, чтобы не было 0
    }
}