package com.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    private int index;
    private GptMessageDto message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
