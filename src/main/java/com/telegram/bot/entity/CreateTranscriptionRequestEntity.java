package com.telegram.bot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "create_transcription_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTranscriptionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String audioFilePath;

    @Column(name = "ai_model", nullable = false)
    private String model;
}
