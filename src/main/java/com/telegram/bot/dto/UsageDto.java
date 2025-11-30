package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageDto {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}

