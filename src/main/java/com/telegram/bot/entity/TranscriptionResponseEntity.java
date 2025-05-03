package com.telegram.bot.entity;

import com.telegram.bot.entity.CreateTranscriptionRequestEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "create_transcription_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscriptionResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transcription_text", nullable = false)
    private String text; // Transcribed text

    @ManyToOne
    @JoinColumn(name = "transcription_request_id", nullable = false)
    private CreateTranscriptionRequestEntity createTranscriptionRequest;
}
