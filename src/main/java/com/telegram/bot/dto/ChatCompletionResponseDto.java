package com.telegram.bot.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponseDto {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
}