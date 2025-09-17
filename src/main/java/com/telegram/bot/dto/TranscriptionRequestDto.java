package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptionRequestDto {
    private String filePath;
    private String model;
}