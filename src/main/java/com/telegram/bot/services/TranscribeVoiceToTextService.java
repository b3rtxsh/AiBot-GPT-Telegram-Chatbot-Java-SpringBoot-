package com.telegram.bot.services;

import com.telegram.bot.entity.CreateTranscriptionRequestEntity;
import com.telegram.bot.entity.TranscriptionResponseEntity;
import com.telegram.bot.openai.OpenAiClient;
import com.telegram.bot.repository.CreateTranscriptionRequestRepository;
import com.telegram.bot.repository.TranscriptionResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
public class TranscribeVoiceToTextService {

    private final OpenAiClient openAiClient;
    private final CreateTranscriptionRequestRepository createTranscriptionRequestRepository;
    private final TranscriptionResponseRepository transcriptionResponseRepository;

    public String transcribe(File audioFile) {
        // Step 1: Create a Transcription Request Entity
        CreateTranscriptionRequestEntity requestEntity = CreateTranscriptionRequestEntity.builder()
                .audioFilePath(audioFile.getAbsolutePath()) // Store file path instead of File object
                .model("whisper-1")
                .build();

        // Save the request entity to the database
        createTranscriptionRequestRepository.save(requestEntity);

        // Step 2: Make the API call via OpenAIClient
        TranscriptionResponseEntity responseEntity = openAiClient.createTranscription(requestEntity);

        // Save the response entity to the database
        transcriptionResponseRepository.save(responseEntity);

        // Step 3: Return the transcription text
        return responseEntity.getText();
    }
}
