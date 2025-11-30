package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatChoiceDto {
    private int index;
    private MessageDto message;
    private String finishReason;
}
