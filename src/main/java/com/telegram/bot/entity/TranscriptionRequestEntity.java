package com.telegram.bot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "transcription_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private File audioFile;

    private String aiModel;

    @Column(columnDefinition = "TEXT")
    private String transcriptionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_message_id")
    private Message userMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_history_id")
    private ChatHistory chatHistory;

    private Long userId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

