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

        CreateTranscriptionRequestEntity requestEntity = CreateTranscriptionRequestEntity.builder()
                .audioFilePath(audioFile.getAbsolutePath())
                .model("whisper-1")
                .build();


        createTranscriptionRequestRepository.save(requestEntity);


        TranscriptionResponseEntity responseEntity = openAiClient.createTranscription(requestEntity);


        transcriptionResponseRepository.save(responseEntity);


        return responseEntity.getText();
    }
}
