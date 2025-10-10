package com.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Choice {
    private int index;
    private MessageDto message;
    @JsonProperty("finish_reason")
    private String finishReason;
}
